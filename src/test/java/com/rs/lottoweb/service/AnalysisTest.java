package com.rs.lottoweb.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

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
		int testCount = 10;
		int analysisCount = 20;
		int minRange = 16;
		int maxRange = 120;
		int rangeIncrease = 2;
		int minSeq = 0;
		int maxSeq = analysisCount / 3;
		int count = 0;
		double numsCount = 0;
		
		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<ExclusionAnalysis> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(ExclusionAnalysis anal : analList){
				nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
			}
			
			nums = lottoService.removeDuplicate(nums);
			LottoHistory history = lottoService.selectByRound(round);
			
			sb.append("round : " + round + " count : " + nums.size());
			numsCount += nums.size();
			
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
		double hitRate =  count*1.0 / testCount * 100;
		double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testCount))*100;
		System.out.println(sb);
		System.out.printf("hitRate = %f.2\n", hitRate);
		System.out.printf("averageRate = %f.2\n", averageRate);
		assertThat(hitRate, greaterThan(averageRate));
	}
	
}
