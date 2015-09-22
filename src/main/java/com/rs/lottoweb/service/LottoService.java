package com.rs.lottoweb.service;

import java.io.IOException;
import java.util.List;

import com.rs.lottoweb.domain.AnalysisResult;
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
	
	/**
	 * startRound(과거회차)를 시작으로 계속 제외수 분석함.
	 * @param startRound
	 * @param analysisCount
	 * @param minRange
	 * @param maxRange
	 * @param rangeIncrease
	 * @param minSeq
	 * @param maxSeq
	 * @return
	 */
	public List<AnalysisResult> analysisExclusion(int startRound, int analysisCount, int minRange, int maxRange, int rangeIncrease
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
	
	
	/**
	 * 주기적으로 제외수 정보를 인서트
	 */
	public void scheduleExclusion();
	
	/**
	 * 해당 회차의 제외수 리스트를 리턴.
	 * @param round
	 * @return
	 */
	public List<Integer> getAnalysedExclusionNumbers(int round);
	

	/**
	 * 주어진 조건으로 분석하여 해당 회차에 자주 나올 가능성이 있는 수 리턴.
	 * @param lottoRound
	 * @param analRange
	 * @param sequence
	 * @return
	 */
	public List<Integer> getFrequentNumber(int lottoRound, int analRange, int sequence);

	/**
	 * 주어진 조건 범위로 계속 분석하여 자주 나올 가능성이 가장 높은 분석 리턴.
	 * @param startRound
	 * @param analysisCount
	 * @param minRange
	 * @param maxRange
	 * @param rangeIncrease
	 * @param minSeq
	 * @param maxSeq
	 * @return
	 */
	public List<AnalysisResult> analysisFrequent(int startRound, int analysisCount, int minRange, int maxRange, int rangeIncrease
			, int minSeq, int maxSeq);
	
	/**
	 * 주어진 숫자 배열에 해당 회차의 당첨번호가 몇개가 포함되는지 리턴.
	 * @param nums
	 * @param round
	 * @return
	 */
	public int getHitCount(List<Integer> nums, int round);
	
	/**
	 * 주어진 숫자에 해당하는 확률을 분수 형태 문자열로 리턴.
	 * @param all
	 * @param hitCount
	 * @param expect
	 * @return
	 */
	public String getHitRate(int all, int hitCount, int expect);
	
	
	/**
	 * 모든 분석 캐시 삭제
	 */
	public void clearAllCache();
}
