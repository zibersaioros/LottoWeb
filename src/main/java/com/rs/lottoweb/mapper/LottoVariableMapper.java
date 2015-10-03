package com.rs.lottoweb.mapper;

import com.rs.lottoweb.domain.LottoVariable;

public interface LottoVariableMapper {
	public int insert(LottoVariable lottoVariable);
	public String selectByName(String name);
	public int update(LottoVariable lottoVariable);
}
