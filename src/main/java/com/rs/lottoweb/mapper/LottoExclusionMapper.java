package com.rs.lottoweb.mapper;

import java.util.List;

import com.rs.lottoweb.domain.LottoExclusion;

public interface LottoExclusionMapper {

	public int insert(LottoExclusion lottoExclusion);
	
	public List<Integer> selectByRound(int round);
}
