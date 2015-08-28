package com.rs.lottoweb.service;

import java.io.IOException;
import java.util.List;

import com.rs.lottoweb.domain.ExclusionAnalysis;
import com.rs.lottoweb.domain.LottoHistory;



public interface LottoService {
	public static final int min = 262; //2007-12-08;.

	public static final int year = 2007;
	public static final int month = 12;
	public static final int day = 8;
	
	static final String columns[] = {
		"num1_ord"
		, "num2_ord"
		, "num3_ord"
		, "num4_ord"
		, "num5_ord"
		, "num6_ord"
		, "num7"
	};
	
	public int insert(LottoHistory lottoHistory);
	
	/**
	 * @return 현재까지 진행된 회차 리턴
	 */
	public int getCurrentNumber();
	public List<Integer> getExclusionNumber(int lottoRound, int checkRound, int sequence);
	public LottoHistory selectByRound(int round);
	
	public ExclusionAnalysis analysisExclusion(int analysisCount, int minLimit, int maxLimit, int minSeq, int maxSeq);
	
	/**
	 * 주기적으로 데이터를 인서트
	 * @throws IOException
	 */
	public void scheduleInsert() throws IOException;
	
}
