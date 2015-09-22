package com.rs.lottoweb.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
	public void testAnalysisExclusion(){
		int testCount = 20;
		int analysisCount = 13;
		int minRange = 12;
		int maxRange = 60;
		int rangeIncrease = 2;
		int minSeq = 0;
		int maxSeq = 6; //analysisCount / 2;
		
		StringBuffer sb = new StringBuffer();
		lottoService.clearAllCache();;

		for(analysisCount = 11 ; analysisCount <= 13; analysisCount++){
			for(minRange = 12 ; minRange <= 20; minRange += 4){
				for(maxRange = 52; maxRange <= 60; maxRange += 4){
					for(rangeIncrease = 2; rangeIncrease <= 3; rangeIncrease++){
						for(minSeq = 0; minSeq <= 2;  minSeq++){
							for(maxSeq = 4; maxSeq <= 6; maxSeq++){
								int count = 0;	
								double numsCount = 0;
								int hitCount = 0;
								
								StringBuffer subBuffer = new StringBuffer();
								subBuffer.append("testCount = " + testCount + "\n");
								subBuffer.append("analysisCount = " + analysisCount + "\n");
								subBuffer.append("minRange = " + minRange + "\n");
								subBuffer.append("maxRange = " + maxRange + "\n");
								subBuffer.append("rangeIncrease = " + rangeIncrease + "\n");
								subBuffer.append("minSeq = " + minSeq + "\n");
								subBuffer.append("maxSeq = " + maxSeq + "\n");
								
								for(int i = 0; i < testCount; i++){
									int round = lottoService.getCurrentNumber() - i;
									List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
									List<Integer> nums = new ArrayList<Integer>();
									for(AnalysisResult anal : analList){
										nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
									}

									nums = lottoService.removeDuplicate(nums);
									LottoHistory history = lottoService.selectByRound(round);

									subBuffer.append("round : " + round + " count : " + nums.size());
									numsCount += nums.size();

									if(nums.contains(history.getNum1_ord())
											|| nums.contains(history.getNum2_ord())
											|| nums.contains(history.getNum3_ord())
											|| nums.contains(history.getNum4_ord())
											|| nums.contains(history.getNum5_ord())
											|| nums.contains(history.getNum6_ord())){
										subBuffer.append(" false\n");
										continue;
									}

									hitCount += nums.size();
									subBuffer.append("\n");
									count++;
								}
								
								if(Math.ceil(numsCount / testCount) > 38)
									continue;
								
								double hitRate =  count*1.0 / testCount * 100;
								double realHitRate = hitCount / numsCount * 100;
								
								double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testCount))*100;
								subBuffer.append(String.format("hitRate = %f.2\n", hitRate));
								subBuffer.append(String.format("realHitRate = %f.2\n", realHitRate));
								subBuffer.append(String.format("averageRate = %f.2\n", averageRate));
								subBuffer.append("=====================================\n");
								if(realHitRate > averageRate * 2)
									sb.append(subBuffer);
							}
						}
					}
				}
			}
		}

		System.out.println(sb.toString());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("target", "analysisResult.txt")));
			bw.write(sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {}
		

//		for(int i = 0; i < testCount; i++){
//			int round = lottoService.getCurrentNumber() - i;
//			List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
//			List<Integer> nums = new ArrayList<Integer>();
//			for(AnalysisResult anal : analList){
//				nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
//			}
//
//			nums = lottoService.removeDuplicate(nums);
//			LottoHistory history = lottoService.selectByRound(round);
//
//			sb.append("round : " + round + " count : " + nums.size());
//			numsCount += nums.size();
//
//			if(nums.contains(history.getNum1_ord())
//					|| nums.contains(history.getNum2_ord())
//					|| nums.contains(history.getNum3_ord())
//					|| nums.contains(history.getNum4_ord())
//					|| nums.contains(history.getNum5_ord())
//					|| nums.contains(history.getNum6_ord())){
//				sb.append(" false\n");
//				continue;
//			}
//
//			hitCount += nums.size();
//			sb.append("\n");
//			count++;
//		}
//		double hitRate =  count*1.0 / testCount * 100;
//		double realHitRate = hitCount / numsCount * 100;
//		double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testCount))*100;
//		System.out.println(sb);
//		System.out.printf("hitRate = %f.2\n", hitRate);
//		System.out.printf("realHitRate = %f.2\n", realHitRate);
//		System.out.printf("averageRate = %f.2\n", averageRate);
//		assertThat(realHitRate, greaterThan(averageRate * 2));
	}


	@Test
	@Ignore
	public void testAnalysisFrequent(){
		int testCount = 10;
		int analysisCount = 12;
		int minRange = 10;
		int maxRange = 120;
		int rangeIncrease = 10;
		int minSeq = 0;
		int maxSeq = 1; //analysisCount / 2;

		int numSum = 0;
		int hitSum = 0;

		StringBuffer sb = new StringBuffer();

		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisFrequent(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
				nums.addAll(lottoService.getFrequentNumber(round, anal.getRange(), anal.getSequence()));
			}

			nums = lottoService.removeDuplicate(nums);

			int hitCount = lottoService.getHitCount(nums, round) ;

			sb.append("round : " + round + " count : " + nums.size());
			sb.append(" hit : " + hitCount +"\n");

			for(int expect = 6 ; expect > 2; expect--){
				sb.append(expect + " : " + lottoService.getHitRate(nums.size(), hitCount, expect) + "\n");
			}

			sb.append("\n");

			numSum += nums.size();
			hitSum += hitCount;
		}

		System.out.println(sb);
		System.out.printf("hitRate = %f.2\n", hitSum*1.0 / numSum * 100);
	}
}