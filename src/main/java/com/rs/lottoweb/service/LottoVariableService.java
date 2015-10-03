package com.rs.lottoweb.service;

import com.rs.lottoweb.domain.LottoVariable;


public interface LottoVariableService {

	public int insertLottoVariable(LottoVariable lottoVariable);
	public int updateLottoVariable(LottoVariable lottoVariable);
	public String selectByName(String name);
	public <T>T selectByName(String name, T defaultValue);
	
	
}
