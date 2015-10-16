package com.rs.lottoweb.mapper;

import java.util.List;
import java.util.Map;

public interface LottoFrequentMapper {

	/**
	 * 추천수 DB에 삽입
	 * int round, List<Integer> list
	 * @param params
	 * @return
	 */
	public int insert(Map<String, Object> params);
	
	public List<Integer> selectByRound(int round);
}
