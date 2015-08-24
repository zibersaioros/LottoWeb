package com.rs.lottoweb.mapper;

import java.util.List;
import java.util.Map;

import com.rs.lottoweb.domain.LottoAnalysis;
import com.rs.lottoweb.domain.LottoHistory;


public interface LottoHistoryMapper {
	/**
	 * 로또 데이터를 insert
	 * @param lottoHistory
	 * @return
	 */
	public int insert(LottoHistory lottoHistory);
	
	public List<LottoAnalysis> selectExclusion(Map<String, Object> param);
	public List<Integer> selectDiff(Map<String, Object> param);
	public LottoHistory selectByRound(int round);
	
	public Integer testRecursive();

	/**
	 * 모든 회차를(회차만) 가져온다.
	 * @return
	 */
	public List<LottoHistory> selectAllRound();
}
