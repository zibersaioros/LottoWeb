package com.rs.lottoweb.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.domain.AnalysisResult;
import com.rs.lottoweb.domain.LottoHistory;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LottoBootApplication.class})
public class AnalysisTest {


	@Autowired
	LottoService lottoService;

	@Test
	@Ignore
	public void testAnalysisExclusion(){
		int testCount = 10;
		int analysisCount = 12;
		int minRange = 10;
		int maxRange = 120;
		int rangeIncrease = 5;
		int minSeq = 4;
		int maxSeq = 6; //analysisCount / 2;
		int count = 0;

		double numsCount = 0;
		int hitCount = 0;

		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
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

			hitCount += nums.size();
			sb.append("\n");
			count++;
		}
		double hitRate =  count*1.0 / testCount * 100;
		double realHitRate = hitCount / numsCount * 100;
		double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testCount))*100;
		System.out.println(sb);
		System.out.printf("hitRate = %f.2\n", hitRate);
		System.out.printf("realHitRate = %f.2\n", realHitRate);
		System.out.printf("averageRate = %f.2\n", averageRate);
		assertThat(realHitRate, greaterThan(averageRate * 2));
	}


	@Test
	public void testAnalysisFrequent(){
		int testCount = 10;
		int analysisCount = 12;
		int minRange = 10;
		int maxRange = 120;
		int rangeIncrease = 5;
		int minSeq = 0;
		int maxSeq = 0; //analysisCount / 2;
		int count = 0;

		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisFrequent(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
				nums.addAll(lottoService.getFrequentNumber(round, anal.getRange(), anal.getSequence()));
			}

			nums = lottoService.removeDuplicate(nums);
			LottoHistory history = lottoService.selectByRound(round);

			int hitCount = lottoService.getHitCount(nums, round) ;
			
			sb.append("round : " + round + " count : " + nums.size());
			sb.append(" hit : " + hitCount +"\n");
			
			for(int expect = 6 ; expect >2; expect--){
				sb.append(expect + " : " + lottoService.getHitRate(nums.size(), hitCount, expect) + "\n");
			}
			 
			// 1등 당첨 확률 : hitCount C 6 * all - hitCount C 0 /  all C 6 
			// 3등 당첨 확률 : hitCount C 5 * all-hitcount C 1     / all C 6
			// 4등 당첨 확률 : hitCount C 4 * all - hitCount C 2 / allC6
			// 5등 당첨 확률 : hitCount C 3 * all - hitCount C 3 / allC6
			
			
			if(hitCount == 6)
				count++;

			sb.append("\n");
		}
		double hitRate =  count * 1.0 / testCount * 100;
		System.out.println(sb);
		System.out.printf("hitRate = %f.2\n", hitRate);
	}
}