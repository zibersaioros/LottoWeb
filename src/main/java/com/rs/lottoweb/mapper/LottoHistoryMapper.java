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
	
	/**
	 * 제외수 페어를 제외가 많이 된 순으로 리턴.
	 *
	 * @param params (start: 분석을 시작할 회차(높은 곳에서 아래로 시작함), analRange: 과거회차를 몇번 분석할 지, column: 제외수 분석할 컬럼명)
	 * @return
	 */
	public List<LottoAnalysis> selectExclusionPair(Map<String, Object> params);
	public List<Integer> selectDiff(Map<String, Object> params);
	public LottoHistory selectByRound(int round);
	
	public Integer testRecursive();

	/**
	 * 모든 회차를(회차만) 가져온다.
	 * @return
	 */
	public List<LottoHistory> selectAllRound();
	
	/**
	 *  로또 개수를 가져온다.
	 * @return
	 */
	public int getLottoCount();
	
	
	/**
	 * 자주나오는 번호 페어를 빈출한 순으로 리턴.
	 * @param params (start: 분석을 시작할 회차(높은 곳에서 아래로 시작함), analRange: 과거회차를 몇번 분석할 지, column: 제외수 분석할 컬럼명)
	 * @return
	 */
	public List<LottoAnalysis> selectFrequentPair(Map<String, Object> params);
}
