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
import com.rs.lottoweb.domain.LottoVariable;
import com.rs.lottoweb.mapper.LottoExclusionMapper;
import com.rs.lottoweb.mapper.LottoFrequentMapper;
import com.rs.lottoweb.mapper.LottoHistoryMapper;

@Service
@Transactional(readOnly=true)
public class LottoServiceImpl implements LottoService{
	
	private Map<String, List<LottoAnalysis>> pairExclusionCache = new HashMap<String, List<LottoAnalysis>>();
	private Map<String, List<Integer>> seqExclusionCache = new HashMap<String, List<Integer>>();
	
	private Map<String, List<LottoAnalysis>> pairFrequentCache = new HashMap<String, List<LottoAnalysis>>();
	private Map<String, List<Integer>> seqFrequentCache = new HashMap<String, List<Integer>>();
	
	
	
	@Autowired
	LottoHistoryMapper lottoHistoryMapper;
	
	@Autowired
	LottoExclusionMapper lottoExclusionMapper;
	
	@Autowired 
	LottoFrequentMapper lottoFrequentMapper;
	
	@Autowired
	LottoVariableService lottoVariableService;
	
	//주기적으로 제외수 분석
	@Override
	@Scheduled(cron="0 30 2 */1 * *")
	@Transactional(readOnly=false)
	public void scheduleAnalysis(){
		clearAllCache();
		
		//제외수가 없으면
		int round = getCurrentNumber() + 1;
		List<Integer> exclusionNums = getAnalysedExclusionNumbers(round);
		if(exclusionNums == null || exclusionNums.size() < 1){
			//TODO 환경변수에서 가져와야 함!
			int analysisCount = lottoVariableService.selectByName(LottoVariable.EX_ANAL_COUNT, LottoVariable.EX_ANAL_COUNT_VAL);
			int minRange = lottoVariableService.selectByName(LottoVariable.EX_MIN_RANGE, LottoVariable.EX_MIN_RANGE_VAL);
			int maxRange = lottoVariableService.selectByName(LottoVariable.EX_MAX_RANGE, LottoVariable.EX_MAX_RANGE_VAL);
			int rangeIncrease = lottoVariableService.selectByName(LottoVariable.EX_RANGE_INC, LottoVariable.EX_RANGE_INC_VAL);
			int minSeq = lottoVariableService.selectByName(LottoVariable.EX_MIN_SEQUENCE, LottoVariable.EX_MIN_SEQUENCE_VAL);
			int maxSeq = lottoVariableService.selectByName(LottoVariable.EX_MAX_SEQUENCE, LottoVariable.EX_MAX_SEQUENCE_VAL);
			
			List<AnalysisResult> analList = analysisExclusion(
					round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			exclusionNums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
				exclusionNums.addAll(getExclusionNumber(round, anal.getRange(), anal.getSequence()));
			}
			
			exclusionNums = removeDuplicate(exclusionNums);
			
			//nums를 돌아가면서 insert  db 커넥션을 줄이기 위해 한번에 삽입
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("round", round);
			params.put("list", exclusionNums);
			lottoExclusionMapper.insert(params);
		}
		
		
		List<Integer> frequentNums = getAnalysedFrequentNumbers(round);
		if(frequentNums == null || frequentNums.size() < 1){
			//TODO 환경변수에서 가져와야 함!! 추천수를 가져온다.
			int analysisCount = lottoVariableService.selectByName(LottoVariable.FR_ANAL_COUNT, LottoVariable.FR_ANAL_COUNT_VAL);
			int minRange = lottoVariableService.selectByName(LottoVariable.FR_MIN_RANGE, LottoVariable.FR_MIN_RANGE_VAL);
			int maxRange = lottoVariableService.selectByName(LottoVariable.FR_MAX_RANGE, LottoVariable.FR_MAX_RANGE_VAL);
			int rangeIncrease = lottoVariableService.selectByName(LottoVariable.FR_RANGE_INC, LottoVariable.FR_RANGE_INC_VAL);
			int minSeq = lottoVariableService.selectByName(LottoVariable.FR_MIN_SEQUENCE, LottoVariable.FR_MIN_SEQUENCE_VAL);
			int maxSeq = lottoVariableService.selectByName(LottoVariable.FR_MAX_SEQUENCE, LottoVariable.FR_MAX_SEQUENCE_VAL);

			List<AnalysisResult> frequentList = analysisFrequent(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			frequentNums = new ArrayList<Integer>();
			for(AnalysisResult anal : frequentList){
				frequentNums.addAll(getFrequentNumber(round, anal.getRange(), anal.getSequence()));
			}
			frequentNums = removeDuplicate(frequentNums);
			frequentNums.removeAll(exclusionNums);
			
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("round", round);
			params.put("list", frequentNums);
			lottoFrequentMapper.insert(params);
		}
		
		
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
		
		String key = new StringBuffer().append(lottoRound).append("_").append(analRange).append("_").append(sequence).toString();
		List<Integer> nums = seqExclusionCache.get(key);
		if(nums != null){
			return nums;
		}
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("analRange", analRange);
		nums = new ArrayList<Integer>();
		for(String column : columns){
			int start = lottoRound - 1;
			param.put("column", column);
			param.put("start", start);
			
			String pairKey = start + column + analRange;
			
			List<LottoAnalysis> list = pairExclusionCache.get(pairKey);
			if(list == null){
				list = lottoHistoryMapper.selectExclusionPair(param);
				pairExclusionCache.put(pairKey, list);
			}
			
			list = getMaxCount(list, sequence);
			
			param.put("round", lottoRound);
			param.put("list", list);
			nums.addAll(lottoHistoryMapper.selectDiff(param));
		}
		
		nums = removeDuplicate(nums);
		seqExclusionCache.put(key	, nums);
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
		
		// 시퀀스 캐시에 있으면 찾아서 리턴.
		String key = new StringBuffer().append(lottoRound).append("_").append(analRange).append("_").append(sequence).toString();
		List<Integer> nums = seqFrequentCache.get(key);
		if(nums != null){
			return nums;
		}
		
		//캐시에 없는 경우 DB에서 분석결과를 가져온다.
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("analRange", analRange);
		nums = new ArrayList<Integer>();
		for(String column : columns){
			int start = lottoRound-1;
			params.put("column", column);
			params.put("start", start);
			
			//캐시에서 분석결과를 가져온다.
			String pairKey = start + column + analRange;
			List<LottoAnalysis> list = pairFrequentCache.get(pairKey);
			
			//캐시에 없는경우 DB에서 분석결과를 가져온다.
			if(list == null){
				list = lottoHistoryMapper.selectFrequentPair(params);
				pairFrequentCache.put(pairKey, list);
			}
			list = getMaxCount(list, sequence);
			
			params.put("round", lottoRound);
			params.put("list", list);
			nums.addAll(lottoHistoryMapper.selectDiff(params));
		}
		
		nums = removeDuplicate(nums);
		seqFrequentCache.put(key, nums);
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
					
					if(nums.size() < 45 && getHitCount(nums, round) == 6 )
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
	public double getHitRateDenominator(int all, int hitCount, int expect){
		try {
			return CombinatoricsUtils.binomialCoefficientDouble(all, 6) / (CombinatoricsUtils.binomialCoefficientDouble(hitCount, expect) * CombinatoricsUtils.binomialCoefficientDouble(all-hitCount, 6-expect));
		} catch (Exception e) {
			return -1;
		}
	}
	
	@Override
	public String getHitRateString(int all, int hitCount, int expect){
		if(hitCount < expect
				|| all-hitCount < 6-expect 
				|| all < 6)
			return "0";
		
		String rate = String.format("1 / %f.2", getHitRateDenominator(all, hitCount, expect));
		
		return rate;
	}
	
	@Override
	public void clearAllCache(){
		pairExclusionCache.clear();
		pairFrequentCache.clear();
		seqExclusionCache.clear();
		seqFrequentCache.clear();
	}


	@Override
	public List<Integer> getAnalysedFrequentNumbers(int round) {
		return lottoFrequentMapper.selectByRound(round);
	}
	
	
}