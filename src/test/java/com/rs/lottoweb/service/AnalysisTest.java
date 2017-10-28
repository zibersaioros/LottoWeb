package com.rs.lottoweb.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.domain.AnalysisResult;
import com.rs.lottoweb.domain.ExclusionAnalysisProperty;
import com.rs.lottoweb.domain.FrequentAnalysisProperty;
import com.rs.lottoweb.domain.InvertAnalysisProperty;
import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.domain.LottoVariable;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes={LottoBootApplication.class})
@RunWith(SpringRunner.class)
@SpringBootTest //can be used as an alternative to the standard spring-test @ContextConfiguration
public class AnalysisTest {
	public static final int MODE_EXCLUSION = 1;
	public static final int MODE_FREQUENT = 2;
	public static final int MODE_INVERT_EXCLUSION = 3;
	
	public boolean analysisMode = true;
	
	@Autowired
	LottoService lottoService;
	
	@Autowired
	FrequentAnalysisProperty frequentAnal;
	@Autowired
	ExclusionAnalysisProperty exclusionAnal;
	@Autowired
	InvertAnalysisProperty invertAnal;

	Random random = new Random(System.currentTimeMillis());
	
	double standard5;
	double standard4;
	double standard3;
	
	double standardDenominator5;
	double standardDenominator4;
	double standardDenominator3;
	
	Logger logger = LoggerFactory.getLogger("root");

	@Before
	public void begin(){
		random = new Random(System.currentTimeMillis());
		
		standard5 = lottoService.getHitRate(45, 6, 5);
		standard4 = lottoService.getHitRate(45, 6, 4);
		standard3 = lottoService.getHitRate(45, 6, 3);
		standardDenominator5 = lottoService.getHitRateDenominator(45, 6, 5);
		standardDenominator4 = lottoService.getHitRateDenominator(45, 6, 4);
		standardDenominator3 = lottoService.getHitRateDenominator(45, 6, 3);
	}
	

	@Test
//	@Ignore
	public void testAnalysisExclusion(){
		int testCount = 10;

		lottoService.clearAllCache();

		int[][] varArr = null;
		
		if(analysisMode){
			int[] analysisCountArr = {9, 18};
			int[] minRangeArr = {8, 60};
			int[] maxRangeArr = {80, 180};
			int[] rangeIncreaseArr = {2, 21};
			int[] minSeqArr = {0, analysisCountArr[1]};
			int[] maxSeqArr = {0, analysisCountArr[1]};
			
			int[][] arr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
			varArr = arr;
		} else {
			int[] analysisCountArr = {exclusionAnal.getAnalysisCount(), exclusionAnal.getAnalysisCount()};
			int[] minRangeArr = {exclusionAnal.getMinRange(), exclusionAnal.getMinRange()};
			int[] maxRangeArr = {exclusionAnal.getMaxRange(), exclusionAnal.getMaxRange()};
			int[] rangeIncreaseArr = {exclusionAnal.getRangeIncrease(), exclusionAnal.getRangeIncrease()};
			int[] minSeqArr = {exclusionAnal.getMinSeq(), exclusionAnal.getMinSeq()};
			int[] maxSeqArr = {exclusionAnal.getMaxSeq(), exclusionAnal.getMaxSeq()};
			
			int[][] arr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
			varArr = arr;
		}
		
//		int[][] varArr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
		int[] loopCount = {5, 8, 12, 5, 2, 2};
		int[] analysisVars = {0, 0, 0, 0, 0, 0};
		List<List<Integer>> duplicateList = new ArrayList<List<Integer>>();
		for(int i = 0; i < varArr.length; i++)
			duplicateList.add(new ArrayList<Integer>());
		AppendCounterBuffer acb = new AppendCounterBuffer();
		analysis(testCount, 0, loopCount, varArr, duplicateList, analysisVars, acb, MODE_EXCLUSION);
//		analysis(testCount, 0, loopCount, varArr, duplicateList, analysisVars, acb, MODE_INVERT_EXCLUSION);
		
		System.out.println(acb.sb.toString());
//		System.out.println(acb.log.toString());
//		System.out.println(acb.loopCount);
//		System.out.println(acb.count);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data", "analysisResult.txt")));
			bw.write(acb.sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {}


	}


	@Test
//	@Ignore
	public void testAnalysisFrequent(){
		int testCount = 10;

		lottoService.clearAllCache();
		
		int[][] varArr = null;
		
		if(analysisMode){
			int[] analysisCountArr = {9, 18};
			int[] minRangeArr = {8, 60};
			int[] maxRangeArr = {80, 180};
			int[] rangeIncreaseArr = {2, 21};
			int[] minSeqArr = {0, analysisCountArr[1]};
			int[] maxSeqArr = {0, analysisCountArr[1]};
			
			int[][] arr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
			varArr = arr;
		} else {
			int[] analysisCountArr = {frequentAnal.getAnalysisCount(), frequentAnal.getAnalysisCount()};
			int[] minRangeArr = {frequentAnal.getMinRange(), frequentAnal.getMinRange()};
			int[] maxRangeArr = {frequentAnal.getMaxRange(), frequentAnal.getMaxRange()};
			int[] rangeIncreaseArr = {frequentAnal.getRangeIncrease(), frequentAnal.getRangeIncrease()};
			int[] minSeqArr = {frequentAnal.getMinSeq(), frequentAnal.getMinSeq()};
			int[] maxSeqArr = {frequentAnal.getMaxSeq(), frequentAnal.getMaxSeq()};
			
			int[][] arr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
			varArr = arr;
		}

//		int[][] varArr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
		int[] loopCount = {5, 8, 12, 5, 2, 2};
		int[] analysisVars = {0, 0, 0, 0, 0, 0};
		List<List<Integer>> duplicateList = new ArrayList<List<Integer>>();
		for(int i = 0; i < varArr.length; i++)
			duplicateList.add(new ArrayList<Integer>());
		AppendCounterBuffer acb = new AppendCounterBuffer();
		analysis(testCount, 0, loopCount, varArr, duplicateList, analysisVars, acb, MODE_FREQUENT);
		
		System.out.println(acb.sb.toString());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data", "analysisFrequentResult.txt")));
			bw.write(acb.sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {}
	}
	
	
	@Test
//	@Ignore
	public void testAnalysisInvertExclusion(){
		int testCount = 10;

		lottoService.clearAllCache();

		int[][] varArr = null;
		
		if(analysisMode){
			int[] analysisCountArr = {9, 18};
			int[] minRangeArr = {8, 60};
			int[] maxRangeArr = {80, 180};
			int[] rangeIncreaseArr = {2, 21};
			int[] minSeqArr = {0, analysisCountArr[1]};
			int[] maxSeqArr = {0, analysisCountArr[1]};
			
			int[][] arr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
			varArr = arr;
		} else {
			int[] analysisCountArr = {invertAnal.getAnalysisCount(), invertAnal.getAnalysisCount()};
			int[] minRangeArr = {invertAnal.getMinRange(), invertAnal.getMinRange()};
			int[] maxRangeArr = {invertAnal.getMaxRange(), invertAnal.getMaxRange()};
			int[] rangeIncreaseArr = {invertAnal.getRangeIncrease(), invertAnal.getRangeIncrease()};
			int[] minSeqArr = {invertAnal.getMinSeq(), invertAnal.getMinSeq()};
			int[] maxSeqArr = {invertAnal.getMaxSeq(), invertAnal.getMaxSeq()};
			
			int[][] arr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
			varArr = arr;
		}
		
//		int[][] varArr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
		int[] loopCount = {5, 8, 12, 5, 2, 2};
		int[] analysisVars = {0, 0, 0, 0, 0, 0};
		List<List<Integer>> duplicateList = new ArrayList<List<Integer>>();
		for(int i = 0; i < varArr.length; i++)
			duplicateList.add(new ArrayList<Integer>());
		AppendCounterBuffer acb = new AppendCounterBuffer();
		analysis(testCount, 0, loopCount, varArr, duplicateList, analysisVars, acb, MODE_INVERT_EXCLUSION);
		
		System.out.println(acb.sb.toString());
		
		try {
			File target = new File("data", "analysisInvertResult.txt");
			if(!target.exists())
				target.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(target));
			bw.write(acb.sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {}
	}

	
	private int getAnalysisRandNum(int[] range){
		return getAnalysisRandNum(range, range[1]);
	}

	private int getAnalysisRandNum(int[] range, int limit){
		int max = Math.min(limit, range[1]);
		int min = range[0];
		return random.nextInt(max - min + 1) + min;
	}
	
	
	private void analysis(int testCount, int depth, int[] loopCount, int[][] varArr, List<List<Integer>> duplicateList, int[] analysisVars, AppendCounterBuffer acb, int mode){
		int limit = 1;
		int containCount = 0;
		for(int count = 0 ; count < loopCount[depth] && containCount < 20 && acb.count < limit; count++){
			if(depth == 4 || depth == 5)
				analysisVars[depth] = getAnalysisRandNum(varArr[depth], analysisVars[0]);
			else
				analysisVars[depth] = getAnalysisRandNum(varArr[depth]);
			
			if( duplicateList.get(depth).contains(analysisVars[depth]) || (depth==5 && analysisVars[depth] < analysisVars[depth-1])){
				acb.appendToLog("depth = " + depth + " containtCount = " + containCount + "\n");
				count--;
				containCount++;
				continue;
			}
			containCount = 0;
			duplicateList.get(depth).add(analysisVars[depth]);
			
			if(depth < varArr.length-1)
				analysis(testCount, depth+1, loopCount, varArr, duplicateList, analysisVars, acb, mode);
			else {
				acb.loopCount++;
				String result = null;
				switch (mode) {
				case MODE_EXCLUSION:
//					result = executeAnalysisExclusion(testCount, analysisVars);
					result = executeAnalysisExclusion(testCount, analysisVars[0], analysisVars[1], analysisVars[2], analysisVars[3], analysisVars[4], analysisVars[5]);
					break;
				case MODE_FREQUENT:
					result = executeAnalysisFrequent(testCount, analysisVars[0], analysisVars[1], analysisVars[2], analysisVars[3], analysisVars[4], analysisVars[5]);
					break;
				case MODE_INVERT_EXCLUSION:
					result = executeAnalysisInvert(testCount, analysisVars[0], analysisVars[1], analysisVars[2], analysisVars[3], analysisVars[4], analysisVars[5]);
					break;
				}
				if(result != null)
					acb.append(result);
			}
		}
		duplicateList.get(depth).clear();
	}
	
//	private String executeAnalysisExclusion(int testCount, int[] analysisVars){
//		int count = 0;	
//		double numsCount = 0;
//		int hitCount = 0;
//
//		StringBuffer subBuffer = new StringBuffer();
//		subBuffer.append("testCount = " + testCount + "\n");
//		subBuffer.append("analysisCount = " + analysisVars[0] + "\n");
//		subBuffer.append("minRange = " + analysisVars[1] + "\n");
//		subBuffer.append("maxRange = " + analysisVars[2] + "\n");
//		subBuffer.append("rangeIncrease = " + analysisVars[3] + "\n");
//		subBuffer.append("minSeq = " + analysisVars[4] + "\n");
//		subBuffer.append("maxSeq = " + analysisVars[5] + "\n");
//
//		int testedCount = 0;
//		for(int i = 0; i < testCount; i++){
//			int round = lottoService.getCurrentNumber() - i;
//			List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisVars[0], analysisVars[1], analysisVars[2], analysisVars[3], analysisVars[4], analysisVars[5]);
//			List<Integer> nums = new ArrayList<Integer>();
//			for(AnalysisResult anal : analList){
//				nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
//			}
//
//			nums = lottoService.removeDuplicate(nums);
//			LottoHistory history = lottoService.selectByRound(round);
//			
//			subBuffer.append("round : " + round + " count : " + nums.size());
//			if(nums.size() >= 39){
//				subBuffer.append("\n");
//				continue;
//			}
//			testedCount++;
//			
//			numsCount += nums.size();
//
//			if(nums.contains(history.getNum1_ord())
//					|| nums.contains(history.getNum2_ord())
//					|| nums.contains(history.getNum3_ord())
//					|| nums.contains(history.getNum4_ord())
//					|| nums.contains(history.getNum5_ord())
//					|| nums.contains(history.getNum6_ord())){
//				subBuffer.append(" false\n");
//				continue;
//			}
//
//			hitCount += nums.size();
//			subBuffer.append("\n");
//			count++;
//		}
//		
//
//		if(Math.ceil(numsCount / testedCount) > 38)
//			return null;
//
//		double hitRate =  count*1.0 / testedCount * 100;
//		double realHitRate = hitCount / numsCount * 100;
//		double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testedCount))*100;
//		subBuffer.append(String.format("hitRate = %f.2\n", hitRate));
//		subBuffer.append(String.format("realHitRate = %f.2\n", realHitRate));
//		subBuffer.append(String.format("averageRate = %f.2\n", averageRate));
//		subBuffer.append("=====================================\n");
//		
////		System.out.println(subBuffer.toString());
//		
//		if(realHitRate >= averageRate * 3 && averageRate >= 7 
//				|| hitRate >= 40 
//				|| (realHitRate > averageRate * 2 && hitRate >= 30))
//			return subBuffer.toString();
//		else 
//			return null;
//	}
	
private String executeAnalysisExclusion(int testCount, int analysisCount, int minRange, int maxRange, int rangeIncrease, int minSeq, int maxSeq){
		
		StringBuffer subBuffer = new StringBuffer();
		subBuffer.append("exclusion.testCount=" + testCount + "\n");
		subBuffer.append("exclusion.analysisCount=" + analysisCount + "\n");
		subBuffer.append("exclusion.minRange=" + minRange + "\n");
		subBuffer.append("exclusion.maxRange=" + maxRange + "\n");
		subBuffer.append("exclusion.rangeIncrease=" + rangeIncrease + "\n");
		subBuffer.append("exclusion.minSeq=" + minSeq + "\n");
		subBuffer.append("exclusion.maxSeq=" + maxSeq + "\n");
		int hit4Count = 0;
		double forthSum = 0;
		int hit3Count = 0;
		double tripleSum = 0;
		int hit5Count = 0;
		double fifthSum = 0;
		
		int testedCount = 0;
		int invalidCount = 0;
		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
				nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
			}

			nums = lottoService.invert(lottoService.removeDuplicate(nums));
			
			//각각 확률 계산.
			int hitCount = lottoService.getHitCount(nums, round) ;
			subBuffer.append("round : " + round + " count : " + nums.size());
			subBuffer.append(" hit : " + hitCount +"\n");
			if(nums.size() >= 45 || nums.size() < 4) {
				//가장 최근 회차는 옳아야 함
				if(i == 0)
					return null;
				
				if(++invalidCount > testCount * 0.2)
					break;
				continue;
			}
			testedCount++;
			for(int expect = 6 ; expect > 3; expect--){
				subBuffer.append(expect + " : " + lottoService.getHitRateString(nums.size(), hitCount, expect) + "\n");
			}
			subBuffer.append("\n");
			
			//5개 맞을 확률 계산.
			double fifthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 5);
			if(fifthRate < standardDenominator5 && fifthRate > 0){
				hit5Count++;
				fifthSum += fifthRate;
			}

			//4개 맞을 확률 계산.
			double forthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 4);
			if(forthRate < standardDenominator4 && forthRate > 0){
				hit4Count++;
				forthSum += forthRate;
			} else {
				//가장 최근 회차는 옳아야 함
				if(i == 0)
					return null;
			}

			//3개 맞을 확률 계산
			double tripleRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 3);
			if(tripleRate < standardDenominator3 && tripleRate > 0) {
				hit3Count++;
				tripleSum += tripleRate;	
			}
		}
		
		double average5 = fifthSum / hit5Count;
		double average4 = forthSum / hit4Count;
		double average3 = tripleSum / hit3Count;
		
		double hit5Rate = hit5Count * 1.0 / testedCount * 100;
		double hit4Rate = hit4Count * 1.0 / testedCount * 100;
		double hit3Rate = hit3Count * 1.0 / testedCount * 100;
		
		
		subBuffer.append(String.format("hit5Rate = %f.2\n", hit5Rate));
		subBuffer.append(String.format("real5Rate = 1/%f.2\n", average5));
		subBuffer.append(String.format("average5Rate = 1/%f.2\n", standardDenominator5));
		subBuffer.append(String.format("hit4Rate = %f.2\n", hit4Rate));
		subBuffer.append(String.format("real4Rate = 1/%f.2\n", average4));
		subBuffer.append(String.format("average4Rate = 1/%f.2\n", standardDenominator4));
		subBuffer.append(String.format("hit3Rate = %f.2\n", hit3Rate));
		subBuffer.append(String.format("real3Rate = 1/%f.2\n", average3));
		subBuffer.append(String.format("average3Rate = 1/%f.2\n", standardDenominator3));
		subBuffer.append("=====================================\n");
		System.out.println(subBuffer.toString());
		if( (average3 < standardDenominator3 * 0.65 && average4 < standardDenominator4 * 0.55 && average5 < standardDenominator5 * 0.45)
				&& hit3Rate >= 70 && invalidCount < testCount * 0.3 && hit4Rate >= 60 && hit5Rate >= 50 )
			return subBuffer.toString();
		else
			return null;
		
	}
	
	private String executeAnalysisFrequent(int testCount, int analysisCount, int minRange, int maxRange, int rangeIncrease, int minSeq, int maxSeq){
		
		StringBuffer subBuffer = new StringBuffer();
		subBuffer.append("frequent.testCount=" + testCount + "\n");
		subBuffer.append("frequent.analysisCount=" + analysisCount + "\n");
		subBuffer.append("frequent.minRange=" + minRange + "\n");
		subBuffer.append("frequent.maxRange=" + maxRange + "\n");
		subBuffer.append("frequent.rangeIncrease=" + rangeIncrease + "\n");
		subBuffer.append("frequent.minSeq=" + minSeq + "\n");
		subBuffer.append("frequent.maxSeq=" + maxSeq + "\n");
		int hit4Count = 0;
		double forthSum = 0;
		int hit3Count = 0;
		double tripleSum = 0;
		int hit5Count = 0;
		double fifthSum = 0;

		int testedCount = 0;
		int invalidCount = 0;
		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisFrequent(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
				nums.addAll(lottoService.getFrequentNumber(round, anal.getRange(), anal.getSequence()));
			}

			nums = lottoService.removeDuplicate(nums);
			
			
			//각각 확률 계산.
			int hitCount = lottoService.getHitCount(nums, round) ;
			subBuffer.append("round : " + round + " count : " + nums.size());
			subBuffer.append(" hit : " + hitCount +"\n");
			if(nums.size() >= 45 || nums.size() < 4){
				//가장 최근 회차는 옳아야 함
				if(i == 0)
					return null;
				
				if(++invalidCount > testCount * 0.2)
					break;
				continue;
			}
			testedCount++;
			for(int expect = 6 ; expect > 3; expect--){
				subBuffer.append(expect + " : " + lottoService.getHitRateString(nums.size(), hitCount, expect) + "\n");
			}
			subBuffer.append("\n");
			
			//5개 맞을 확률 계산.
			double fifthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 5);
			if(fifthRate < standardDenominator5 && fifthRate > 0){
				hit5Count++;
				fifthSum += fifthRate;
			}

			//4개 맞을 확률 계산.
			double forthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 4);
			if(forthRate < standardDenominator4 && forthRate > 0){
				hit4Count++;
				forthSum += forthRate;
			} else {
				//가장 최근 회차는 옳아야 함
				if(i == 0)
					return null;
			}

			//3개 맞을 확률 계산
			double tripleRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 3);
			if(tripleRate < standardDenominator3 && tripleRate > 0) {
				hit3Count++;
				tripleSum += tripleRate;	
			}
		}

		double average5 = fifthSum / hit5Count;
		double average4 = forthSum / hit4Count;
		double average3 = tripleSum / hit3Count;
		
		double hit5Rate = hit5Count * 1.0 / testedCount * 100;
		double hit4Rate = hit4Count * 1.0 / testedCount * 100;
		double hit3Rate = hit3Count * 1.0 / testedCount * 100;
		
		subBuffer.append(String.format("hit5Rate = %f.2\n", hit5Rate));
		subBuffer.append(String.format("real5Rate = 1/%f.2\n", average5));
		subBuffer.append(String.format("average5Rate = 1/%f.2\n", standardDenominator5));
		subBuffer.append(String.format("hit4Rate = %f.2\n", hit4Rate));
		subBuffer.append(String.format("real4Rate = 1/%f.2\n", average4));
		subBuffer.append(String.format("average4Rate = 1/%f.2\n", standardDenominator4));
		subBuffer.append(String.format("hit3Rate = %f.2\n", hit3Rate));
		subBuffer.append(String.format("real3Rate = 1/%f.2\n", average3));
		subBuffer.append(String.format("average3Rate = 1/%f.2\n", standardDenominator3));
		subBuffer.append("=====================================\n");
		System.out.println(subBuffer.toString());
		if( (average5 < standardDenominator5 * 0.8)
				&& hit5Rate >= 100 && invalidCount < testCount * 0.3)
			return subBuffer.toString();
		else
			return null;
	}
	
	private String executeAnalysisInvert(int testCount, int analysisCount, int minRange, int maxRange, int rangeIncrease, int minSeq, int maxSeq){
		
		StringBuffer subBuffer = new StringBuffer();
		subBuffer.append("invert.testCount=" + testCount + "\n");
		subBuffer.append("invert.analysisCount=" + analysisCount + "\n");
		subBuffer.append("invert.minRange=" + minRange + "\n");
		subBuffer.append("invert.maxRange=" + maxRange + "\n");
		subBuffer.append("invert.rangeIncrease=" + rangeIncrease + "\n");
		subBuffer.append("invert.minSeq=" + minSeq + "\n");
		subBuffer.append("invert.maxSeq=" + maxSeq + "\n");
		int hit4Count = 0;
		double forthSum = 0;
		int hit3Count = 0;
		double tripleSum = 0;
		int hit5Count = 0;
		double fifthSum = 0;
		
		int testedCount = 0;
		int invalidCount = 0;
		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
			List<Integer> nums = new ArrayList<Integer>();
			for(AnalysisResult anal : analList){
				nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
			}

			nums = lottoService.invert(lottoService.removeDuplicate(nums));
			
			//각각 확률 계산.
			int hitCount = lottoService.getHitCount(nums, round) ;
			subBuffer.append("round : " + round + " count : " + nums.size());
			subBuffer.append(" hit : " + hitCount +"\n");
			if(nums.size() >= 45 || nums.size() < 4) {
				//가장 최근 회차는 옳아야 함
				if(i == 0)
					return null;
				
				if(++invalidCount > testCount * 0.2)
					break;
				continue;
			}
			testedCount++;
			for(int expect = 6 ; expect > 3; expect--){
				subBuffer.append(expect + " : " + lottoService.getHitRateString(nums.size(), hitCount, expect) + "\n");
			}
			subBuffer.append("\n");
			
			//5개 맞을 확률 계산.
			double fifthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 5);
			if(fifthRate < standardDenominator5 && fifthRate > 0){
				hit5Count++;
				fifthSum += fifthRate;
			}

			//4개 맞을 확률 계산.
			double forthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 4);
			if(forthRate < standardDenominator4 && forthRate > 0){
				hit4Count++;
				forthSum += forthRate;
			} else {
				//가장 최근 회차는 옳아야 함
				if(i == 0)
					return null;
			}

			//3개 맞을 확률 계산
			double tripleRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 3);
			if(tripleRate < standardDenominator3 && tripleRate > 0) {
				hit3Count++;
				tripleSum += tripleRate;	
			}
		}
		
		double average5 = fifthSum / hit5Count;
		double average4 = forthSum / hit4Count;
		double average3 = tripleSum / hit3Count;
		
		double hit5Rate = hit5Count * 1.0 / testedCount * 100;
		double hit4Rate = hit4Count * 1.0 / testedCount * 100;
		double hit3Rate = hit3Count * 1.0 / testedCount * 100;
		
		
		subBuffer.append(String.format("hit5Rate = %f.2\n", hit5Rate));
		subBuffer.append(String.format("real5Rate = 1/%f.2\n", average5));
		subBuffer.append(String.format("average5Rate = 1/%f.2\n", standardDenominator5));
		subBuffer.append(String.format("hit4Rate = %f.2\n", hit4Rate));
		subBuffer.append(String.format("real4Rate = 1/%f.2\n", average4));
		subBuffer.append(String.format("average4Rate = 1/%f.2\n", standardDenominator4));
		subBuffer.append(String.format("hit3Rate = %f.2\n", hit3Rate));
		subBuffer.append(String.format("real3Rate = 1/%f.2\n", average3));
		subBuffer.append(String.format("average3Rate = 1/%f.2\n", standardDenominator3));
		subBuffer.append("=====================================\n");
		System.out.println(subBuffer.toString());
		if( (average4 < standardDenominator4 * 0.45)
				&& hit4Rate >= 70 && invalidCount < testCount * 0.3)
			return subBuffer.toString();
		else
			return null;
		
	}
	
	class AppendCounterBuffer{
		int count = 0;
		int loopCount = 0;
		StringBuffer sb = new StringBuffer();
		StringBuffer log = new StringBuffer();
		
		public int append(String s){
			count++;
			sb.append(s);
			return count;
		}
		
		public void appendToLog(String s){
			log.append(s);
		}
	}
}