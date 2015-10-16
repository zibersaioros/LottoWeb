package com.rs.lottoweb.service;

import java.lang.reflect.Method;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rs.lottoweb.domain.LottoVariable;
import com.rs.lottoweb.mapper.LottoVariableMapper;

@Service
public class LottoVariableServiceImpl implements LottoVariableService{
	
	@Autowired
	LottoVariableMapper lottoVariableMapper;

	@Override
	public int insertLottoVariable(LottoVariable lottoVariable) {
		return lottoVariableMapper.insert(lottoVariable);
	}

	@Override
	public int updateLottoVariable(LottoVariable lottoVariable) {
		return lottoVariableMapper.update(lottoVariable); 
	}

	@Override
	public String selectByName(String name) {
		return lottoVariableMapper.selectByName(name);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T selectByName(String name, T defaultValue) {
		String value = lottoVariableMapper.selectByName(name);
		
		if(value == null || value.equals("")){
			return defaultValue;
		} else {
			try {
				Method method = defaultValue.getClass().getMethod("valueOf", String.class);
				return (T)method.invoke(defaultValue, value);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
}
