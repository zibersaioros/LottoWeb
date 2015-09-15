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
	
	/**
	 * analRange 범위 만큼 분석해서 해당 라운드의 sequence 번째의 제외수목록을 반환.
	 * @param lottoRound
	 * @param analRange
	 * @param sequence
	 * @return
	 */
	public List<Integer> getExclusionNumber(int lottoRound, int analRange, int sequence);
	public LottoHistory selectByRound(int round);
	
	public List<ExclusionAnalysis> analysisExclusion(int startRound, int analysisCount, int minRange, int maxRange, int rangeIncrease
			, int minSeq, int maxSeq);
	
	/**
	 * 숫자를 정렬한 후 중복을 제거함
	 * @param list
	 * @return
	 */
	public List<Integer> removeDuplicate(List<Integer> list);
	
	/**
	 * 주기적으로 데이터를 인서트
	 * @throws IOException
	 */
	public void scheduleInsert() throws IOException;
	
	
	/**
	 * 제외수의 개수에 따라 제외될 확률을 구함.
	 * @param count
	 * @return
	 */
	public double getExclusionRate(int count);
	
}
