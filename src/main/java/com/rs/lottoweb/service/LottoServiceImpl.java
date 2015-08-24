package com.rs.lottoweb.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.fluent.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.rs.lottoweb.domain.ExclusionAnalysis;
import com.rs.lottoweb.domain.LottoAnalysis;
import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.mapper.LottoHistoryMapper;

@Service
@Transactional(readOnly=true)
public class LottoServiceImpl implements LottoService{
	
	@Autowired
	LottoHistoryMapper lottoMapper;
	
	//주기적으로 데이터를 인서트
	@Scheduled
	public int scheduleInsert() throws IOException{
		System.out.println("scheduled insert");
		
		String url = "http://www.lottonumber.co.kr/ajax.winnum.php?cnt=";
		
		// 모든 회차 가져옴.
		List<LottoHistory> lottoList = lottoMapper.selectAllRound();
		int currentRound = getCurrentNumber();
		int insertCount = 0;
		
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
				insertCount++;
			} else {
				start++;
			}
		}
		
		return insertCount;
	}
	
	
	@Override
	@Transactional(readOnly=false)
	public int insert(LottoHistory lottoHistory) {
		return lottoMapper.insert(lottoHistory);
	}

	@Override
	public List<Integer> getExclusionNumber(int lottoRound, int limit, int sequence) {
		
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("limit", limit);
		
		List<Integer> nums = new ArrayList<Integer>();
		
		for(String column : columns){
			param.put("column", column);
			param.put("round", lottoRound-1);
			
			List<LottoAnalysis> list = lottoMapper.selectExclusion(param);
			list = getMaxCount(list, sequence);
			
			param.put("round", lottoRound);
			param.put("list", list);
			nums.addAll(lottoMapper.selectDiff(param));
		}
		
		nums = removeDuplicate(nums);
		return nums;
	}
	
	/**
	 * 가장 많이나온 회차 - sequence 만큼 리턴
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
	
	
	private List<Integer> removeDuplicate(List<Integer> list){
		
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
		return lottoMapper.selectByRound(round);
	}

	
	@Override
	public ExclusionAnalysis analysisExclusion(int analysisCount, int minLimit, int maxLimit, int minSeq, int maxSeq) {
		int limitPlus = 5;
		int seqPlus = 1;
		
		int maxSuccess = -1;
		int recLimit = -1;
		int recSeq = -1;
		
		for(int limit = minLimit; limit <= maxLimit; limit+=limitPlus ){
			for(int seq = minSeq; seq <= maxSeq; seq+=seqPlus){
				int successCount = 0;
				for(int i = 0; i < analysisCount; i++){
					
					int round = getCurrentNumber() - i;
					List<Integer> nums = getExclusionNumber(round, limit, seq);
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
					maxSuccess = successCount;
					recLimit = limit;
					recSeq = seq;
				}
			}
		}
		
		return new ExclusionAnalysis(recLimit, recSeq);
	}
	
}
