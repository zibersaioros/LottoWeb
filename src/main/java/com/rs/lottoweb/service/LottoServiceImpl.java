package com.rs.lottoweb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.rs.lottoweb.domain.AnalysisResult;
import com.rs.lottoweb.domain.LottoAnalysis;
import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.mapper.LottoExclusionMapper;
import com.rs.lottoweb.mapper.LottoHistoryMapper;

@Service
@Transactional(readOnly=true)
public class LottoServiceImpl implements LottoService{
	
	private Map<String, List<LottoAnalysis>> pairCache = new HashMap<String, List<LottoAnalysis>>();
	private Map<String, List<Integer>> sequenceCache = new HashMap<String, List<Integer>>();
	
	
	@Autowired
	LottoHistoryMapper lottoHistoryMapper;
	
	@Autowired
	LottoExclusionMapper lottoExclusionMapper;
	
	//주기적으로 제외수 분석
	@Override
	@Scheduled(cron="0 30 2 */1 * *")
	@Transactional(readOnly=false)
	public void scheduleExclusion(){
		int analysisCount = 12;
		int minRange = 10;
		int maxRange = 120;
		int rangeIncrease = 5;
		int minSeq = 4;
		int maxSeq = 6;
		
		int round = getCurrentNumber() + 1;
		List<AnalysisResult> analList = analysisExclusion(
				round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
		List<Integer> nums = new ArrayList<Integer>();
		for(AnalysisResult anal : analList){
			nums.addAll(getExclusionNumber(round, anal.getRange(), anal.getSequence()));
		}
		
		//nums를 돌아가면서 insert  db 커넥션을 줄이기 위해 한번에 삽입
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("round", round);
		params.put("list", nums);
		lottoExclusionMapper.insert(params);
	}
	
	
	//주기적으로 데이터를 인서트
	@Override
	@Scheduled(cron="0 0 */6 * * *") //12시간마다 돌림.
	@Transactional(readOnly=false)
	public void scheduleInsert() throws IOException{
		String url = "http://www.lottonumber.co.kr/ajax.winnum.php?cnt=";
		
		// 모든 회차 가져옴.
		List<LottoHistory> lottoList = lottoHistoryMapper.selectAllRound();
		int currentRound = getCurrentNumber();
		
		Gson gson = new Gson();
		
		//없는 회차는 DB에 집어넣음.
		int start = 0;
		for(int i = min; i <= currentRound; i++){
			LottoHistory history = null;
			try {
				history = lottoList.get(start);
			} catch (Exception e) {}
			
			if(history == null || history.getRound() != i){
				String jsonString = Request.Get(url + i).execute().returnContent().asString();
				history = gson.fromJson(jsonString, LottoHistory.class);
				history.setRound(i);
				insert(history);
			} else {
				start++;
			}
		}
		
	}
	
	
	@Override
	@Transactional(readOnly=false)
	public int insert(LottoHistory lottoHistory) {
		return lottoHistoryMapper.insert(lottoHistory);
	}

	@Override
	public List<Integer> getExclusionNumber(int lottoRound, int analRange, int sequence) {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("analRange", analRange);
		
		String key = new StringBuffer().append(lottoRound).append("_").append(analRange).append("_").append(sequence).toString();
		List<Integer> nums = sequenceCache.get(key);
		if(nums != null){
			return nums;
		}
		
		nums = new ArrayList<Integer>();
		for(String column : columns){
			int start = lottoRound - 1;
			param.put("column", column);
			param.put("start", start);
			
			String pairKey = start + column + analRange;
			
			List<LottoAnalysis> list = pairCache.get(pairKey);
			if(list == null){
				list = lottoHistoryMapper.selectExclusionPair(param);
				pairCache.put(pairKey, list);
			}
			
			list = getMaxCount(list, sequence);
			
			param.put("round", lottoRound);
			param.put("list", list);
			nums.addAll(lottoHistoryMapper.selectDiff(param));
		}
		
		nums = removeDuplicate(nums);
		sequenceCache.put(key	, nums);
		return nums;
	}
	
	/**
	 * 가장 많이나온 회차 - sequence 번째 리턴
	 * @param list
	 * @return
	 */
	private List<LottoAnalysis> getMaxCount(List<LottoAnalysis> list, int sequence){
		Collections.sort(list, new Comparator<LottoAnalysis>() {
			@Override
			public int compare(LottoAnalysis o1, LottoAnalysis o2) {
				return o2.getCnt() - o1.getCnt();
			}
		});
		
		int count = 0;
		int compareIndex = 0;
		for(int i = 1; i < list.size(); i++){
			if(list.get(compareIndex).getCnt() != list.get(i).getCnt()){
				if(count == sequence)
					return list.subList(compareIndex, i);
				count++;
				compareIndex = i;
			}
		}
		return list;
	}
	
	@Override
	public  List<Integer> removeDuplicate(List<Integer> list){
		
		Collections.sort(list);
		
		List<Integer> newList = new ArrayList<Integer>();
		for(int i = 0; i < list.size(); i++){
			int size = newList.size(); 
			int num = list.get(i);
			if(size == 0 
					|| newList.get(size-1) != num ){
				newList.add(num);
			}
		}
		
		return newList;
	}
	
	/**
	 * 현재 회차 리턴
	 * @return
	 */
	public int getCurrentNumber(){
		Calendar old = Calendar.getInstance();
		old.set(year, month - 1, day + 1);

		long diff = new Date().getTime() - old.getTimeInMillis();

		long diffRound = diff / (1000 * 60 * 60 * 24 * 7);
		return (int) diffRound + min;
	}

	
	@Override
	public LottoHistory selectByRound(int round) {
		return lottoHistoryMapper.selectByRound(round);
	}

	
	
	@Override
	public List<AnalysisResult> analysisExclusion(int startRound, int analysisCount, int minRange, int maxRange, int rangeIncrease
			, int minSeq, int maxSeq) {
		
		int maxSuccess = -1;
		
		List<AnalysisResult> analysisList = new ArrayList<AnalysisResult>();
		
		for(int range = minRange; range <= maxRange; range+=rangeIncrease ){
			for(int seq = (minSeq == 0 ? 0 : (int)Math.round(range / (minRange*1.0 / minSeq))) ; seq <= (maxSeq == 0 ? 0 : Math.round(range / (minRange*1.0 / maxSeq))); seq+= Math.round(range / minRange*1.0)){
				int successCount = 0;
				for(int i = 0; i < analysisCount; i++){
					
					int round = startRound - i;
					List<Integer> nums = getExclusionNumber(round, range, seq);
					if(nums.size() > 39)
						continue;
					LottoHistory history = selectByRound(round);
					
					if(nums.contains(history.getNum1_ord())
							|| nums.contains(history.getNum2_ord())
							|| nums.contains(history.getNum3_ord())
							|| nums.contains(history.getNum4_ord())
							|| nums.contains(history.getNum5_ord())
							|| nums.contains(history.getNum6_ord())){
						continue;
					}
					successCount++;
				}
				
				if(successCount > maxSuccess){
					analysisList.clear();
					analysisList.add(new AnalysisResult(range, seq));
					
					maxSuccess = successCount;
				} else if (successCount == maxSuccess){
					analysisList.add(new AnalysisResult(range, seq));
				}
			}
		}
	
		return analysisList;
	}


	@Override
	public double getExclusionRate(int count) {
		return CombinatoricsUtils.binomialCoefficientDouble(39, count) 
				/ CombinatoricsUtils.binomialCoefficientDouble(45, count);
	}


	@Override
	public List<Integer> getAnalysedExclusionNumbers(int round) {
		return lottoExclusionMapper.selectByRound(round);
	}
	
	
	@Override
	public List<Integer> getFrequentNumber(int lottoRound, int analRange, int sequence) {
		
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("analRange", analRange);
		
		List<Integer> nums = new ArrayList<Integer>();
		
		for(String column : columns){
			params.put("column", column);
			params.put("start", lottoRound-1);
			
			List<LottoAnalysis> list = lottoHistoryMapper.selectFrequentPair(params);
			list = getMaxCount(list, sequence);
			
			params.put("round", lottoRound);
			params.put("list", list);
			nums.addAll(lottoHistoryMapper.selectDiff(params));
		}
		
		nums = removeDuplicate(nums);
		return nums;
	}
	
	
	@Override
	public List<AnalysisResult> analysisFrequent(int startRound, int analysisCount, int minRange, int maxRange, int rangeIncrease
			, int minSeq, int maxSeq) {
		
		
		int seqPlus = 1;
		int maxSuccess = 1;
		
		List<AnalysisResult> analysisList = new ArrayList<AnalysisResult>();
		
		for(int range = minRange; range <= maxRange; range+=rangeIncrease ){
			for(int seq = minSeq; seq <= maxSeq; seq+=seqPlus){
				int successCount = 0;
				for(int i = 0; i < analysisCount; i++){
					
					int round = startRound - i;
					List<Integer> nums = getFrequentNumber(round, range, seq);
					
					if(getHitCount(nums, round) == 6 )
						successCount++;
				}
				
				if(successCount > maxSuccess){
					analysisList.clear();
					analysisList.add(new AnalysisResult(range, seq));
					
					maxSuccess = successCount;
				} else if (successCount == maxSuccess){
					analysisList.add(new AnalysisResult(range, seq));
				}
			}
		}
		
		return analysisList;
	}
	

	@Override
	public int getHitCount(List<Integer> nums, int round){
		int count = 0;
		
		LottoHistory history = selectByRound(round);
		
		if(nums.contains(history.getNum1_ord()))
			count++;
		if(nums.contains(history.getNum2_ord()))
			count++;
		if(nums.contains(history.getNum3_ord()))
			count++;
		if(nums.contains(history.getNum4_ord()))
			count++;
		if(nums.contains(history.getNum5_ord()))
			count++;
		if(nums.contains(history.getNum6_ord()))
			count++;
		
		return count;
	}
	
	
	@Override
	public String getHitRate(int all, int hitCount, int expect){
		if(hitCount < expect
				|| all-hitCount < 6-expect 
				|| all < 6)
			return "0";
		
		String rate = String.format("1 / %f.2",  CombinatoricsUtils.binomialCoefficientDouble(all, 6) / (CombinatoricsUtils.binomialCoefficientDouble(hitCount, expect) * CombinatoricsUtils.binomialCoefficientDouble(all-hitCount, 6-expect)));
		
		
		return rate;
	}
	
	@Override
	public void clearAllCache(){
		pairCache.clear();
		sequenceCache.clear();
	}
}