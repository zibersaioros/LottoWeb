package com.rs.lottoweb.mapper;

import java.util.List;
import java.util.Map;

import com.rs.lottoweb.domain.LottoAnalysis;
import com.rs.lottoweb.domain.LottoHistory;


public interface LottoHistoryMapper {
	public int insert(LottoHistory lottoHistory);
	
	public List<LottoAnalysis> selectExclusion(Map<String, Object> param);
	public List<Integer> selectDiff(Map<String, Object> param);
	public LottoHistory selectByRound(int round);
	
	public Integer testRecursive();
}
