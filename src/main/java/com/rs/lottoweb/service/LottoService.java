package com.rs.lottoweb.service;

import java.util.List;

import com.rs.lottoweb.domain.ExclusionAnalysis;
import com.rs.lottoweb.domain.LottoHistory;



public interface LottoService {
	public int insert(LottoHistory lottoHistory);
	public int getCurrentNumber();
	public List<Integer> getExclusionNumber(int lottoRound, int checkRound, int sequence);
	public LottoHistory selectByRound(int round);
	
	public ExclusionAnalysis analysisExclusion(int analysisCount, int minLimit, int maxLimit, int minSeq, int maxSeq);
}
