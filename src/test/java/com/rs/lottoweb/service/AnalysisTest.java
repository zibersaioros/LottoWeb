package com.rs.lottoweb.service;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.domain.ExclusionAnalysis;
import com.rs.lottoweb.domain.LottoHistory;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LottoBootApplication.class})
public class AnalysisTest {

	
	@Autowired
	LottoService lottoService;
	
	@Test
	public void testAnalysis(){
		int analysisCount = 12;
		int minRange = 30;
		int maxRange = 150;
		int rangeIncrease = 3;
		int minSeq = 0;
		int maxSeq = 6;
		int count = 0;
		List<ExclusionAnalysis> analList = lottoService.analysisExclusion(analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < analysisCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<Integer> nums = new ArrayList<Integer>();
			for(ExclusionAnalysis anal : analList){
				nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
			}
			
			nums = lottoService.removeDuplicate(nums);
			LottoHistory history = lottoService.selectByRound(round);
			
			sb.append("round : " + round + " count : " + nums.size());
			
			if(nums.contains(history.getNum1_ord())
					|| nums.contains(history.getNum2_ord())
					|| nums.contains(history.getNum3_ord())
					|| nums.contains(history.getNum4_ord())
					|| nums.contains(history.getNum5_ord())
					|| nums.contains(history.getNum6_ord())){
				sb.append(" false\n");
				continue;
			}
			sb.append("\n");
			count++;
			
		}
		System.out.println(sb);
		System.out.printf("%f.2\n", count*1.0 / analysisCount * 100);
		
	}
	
}
