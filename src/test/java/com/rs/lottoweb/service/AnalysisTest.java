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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.domain.AnalysisResult;
import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.domain.LottoVariable;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LottoBootApplication.class})
public class AnalysisTest {
	public static final int MODE_EXCLUSION = 1;
	public static final int MODE_FREQUENT = 2;

	@Autowired
	LottoService lottoService;

	Random random = new Random(System.currentTimeMillis());
	
	double standard4;
	double max4;
	double standard3;
	double max3;


	@Before
	public void begin(){
		random = new Random(System.currentTimeMillis());
		standard4 = lottoService.getHitRateDenominator(45, 6, 4);
		max4 = lottoService.getHitRateDenominator(43, 4, 4);
		standard3 = lottoService.getHitRateDenominator(45, 6, 3);
		max3 = lottoService.getHitRateDenominator(42, 3, 3);
	}

	@Test
	public void testAnalysisExclusion(){
		int testCount = 20;

		lottoService.clearAllCache();

		int[] analysisCountArr = {9, 18};
		int[] minRangeArr = {8, 60};
		int[] maxRangeArr = {80, 180};
		int[] rangeIncreaseArr = {2, 21};
		int[] minSeqArr = {0, analysisCountArr[1]};
		int[] maxSeqArr = {0, analysisCountArr[1]};
		
//		int[] analysisCountArr = {LottoVariable.EX_ANAL_COUNT_VAL, LottoVariable.EX_ANAL_COUNT_VAL};
//		int[] minRangeArr = {LottoVariable.EX_MIN_RANGE_VAL, LottoVariable.EX_MIN_RANGE_VAL};
//		int[] maxRangeArr = {LottoVariable.EX_MAX_RANGE_VAL, LottoVariable.EX_MAX_RANGE_VAL};
//		int[] rangeIncreaseArr = {LottoVariable.EX_RANGE_INC_VAL, LottoVariable.EX_RANGE_INC_VAL};
//		int[] minSeqArr = {LottoVariable.EX_MIN_SEQUENCE_VAL, LottoVariable.EX_MIN_SEQUENCE_VAL};
//		int[] maxSeqArr = {LottoVariable.EX_MAX_SEQUENCE_VAL, LottoVariable.EX_MAX_SEQUENCE_VAL};
		
		int[][] varArr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
		int[] loopCount = {7, 7, 7, 7, 7, 4};
		int[] analysisVars = {0, 0, 0, 0, 0, 0};
		List<List<Integer>> duplicateList = new ArrayList<List<Integer>>();
		for(int i = 0; i < varArr.length; i++)
			duplicateList.add(new ArrayList<Integer>());
		AppendCounterBuffer acb = new AppendCounterBuffer();
		analysis(testCount, 0, loopCount, varArr, duplicateList, analysisVars, acb, MODE_EXCLUSION);
		
		System.out.println(acb.sb.toString());
		System.out.println(acb.log.toString());
		System.out.println(acb.loopCount);
		System.out.println(acb.count);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data", "analysisResult.txt")));
			bw.write(acb.sb.toString());
			bw.flush();
			bw.close();
		} catch (Exception e) {}


	}


	@Test
	@Ignore
	public void testAnalysisFrequent(){
		int testCount = 20;

		lottoService.clearAllCache();

//		int[] analysisCountArr = {9, 18};
//		int[] minRangeArr = {8, 60};
//		int[] maxRangeArr = {80, 180};
//		int[] rangeIncreaseArr = {2, 21};
//		int[] minSeqArr = {0, analysisCountArr[1]};
//		int[] maxSeqArr = {0, analysisCountArr[1]};
		
		int[] analysisCountArr = {LottoVariable.FR_ANAL_COUNT_VAL, LottoVariable.FR_ANAL_COUNT_VAL};
		int[] minRangeArr = {LottoVariable.FR_MIN_RANGE_VAL, LottoVariable.FR_MIN_RANGE_VAL};
		int[] maxRangeArr = {LottoVariable.FR_MAX_RANGE_VAL, LottoVariable.FR_MAX_RANGE_VAL};
		int[] rangeIncreaseArr = {LottoVariable.FR_RANGE_INC_VAL, LottoVariable.FR_RANGE_INC_VAL};
		int[] minSeqArr = {LottoVariable.FR_MIN_SEQUENCE_VAL, LottoVariable.FR_MIN_SEQUENCE_VAL};
		int[] maxSeqArr = {LottoVariable.FR_MAX_SEQUENCE_VAL, LottoVariable.FR_MAX_SEQUENCE_VAL};
		
		int[][] varArr = {analysisCountArr, minRangeArr, maxRangeArr, rangeIncreaseArr, minSeqArr, maxSeqArr};
		int[] loopCount = {7, 7, 7, 7, 7, 4};
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

	private int getAnalysisRandNum(int[] range){
		return getAnalysisRandNum(range, range[1]);
	}

	private int getAnalysisRandNum(int[] range, int limit){
		int max = Math.min(limit, range[1]);
		int min = range[0];
		return random.nextInt(max - min + 1) + min;
	}
	
	
	private void analysis(int testCount, int depth, int[] loopCount, int[][] varArr, List<List<Integer>> duplicateList, int[] analysisVars, AppendCounterBuffer acb, int mode){
		int limit = 8;
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
					result = executeAnalysisExclusion(testCount, analysisVars);
					break;
				case MODE_FREQUENT:
					result = executeAnalysisFrequent(testCount, analysisVars[0], analysisVars[1], analysisVars[2], analysisVars[3], analysisVars[4], analysisVars[5]);
					break;
				}
				if(result != null)
					acb.append(result);
			}
		}
	}
	
	private String executeAnalysisExclusion(int testCount, int[] analysisVars){
		int count = 0;	
		double numsCount = 0;
		int hitCount = 0;

		StringBuffer subBuffer = new StringBuffer();
		subBuffer.append("testCount = " + testCount + "\n");
		subBuffer.append("analysisCount = " + analysisVars[0] + "\n");
		subBuffer.append("minRange = " + analysisVars[1] + "\n");
		subBuffer.append("maxRange = " + analysisVars[2] + "\n");
		subBuffer.append("rangeIncrease = " + analysisVars[3] + "\n");
		subBuffer.append("minSeq = " + analysisVars[4] + "\n");
		subBuffer.append("maxSeq = " + analysisVars[5] + "\n");

		for(int i = 0; i < testCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisVars[0], analysisVars[1], analysisVars[2], analysisVars[3], analysisVars[4], analysisVars[5]);
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
			return null;

		double hitRate =  count*1.0 / testCount * 100;
		double realHitRate = hitCount / numsCount * 100;

		double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testCount))*100;
		subBuffer.append(String.format("hitRate = %f.2\n", hitRate));
		subBuffer.append(String.format("realHitRate = %f.2\n", realHitRate));
		subBuffer.append(String.format("averageRate = %f.2\n", averageRate));
		subBuffer.append("=====================================\n");
		
//		System.out.println(subBuffer.toString());
		
		if(realHitRate > averageRate * 3 || hitRate >= 40 || (realHitRate > averageRate * 2 && hitRate >= 30))
			return subBuffer.toString();
		else 
			return null;
	}
	
	private String executeAnalysisFrequent(int testCount, int analysisCount, int minRange, int maxRange, int rangeIncrease, int minSeq, int maxSeq){
		
		StringBuffer subBuffer = new StringBuffer();
		subBuffer.append("testCount = " + testCount + "\n");
		subBuffer.append("analysisCount = " + analysisCount + "\n");
		subBuffer.append("minRange = " + minRange + "\n");
		subBuffer.append("maxRange = " + maxRange + "\n");
		subBuffer.append("rangeIncrease = " + rangeIncrease + "\n");
		subBuffer.append("minSeq = " + minSeq + "\n");
		subBuffer.append("maxSeq = " + maxSeq + "\n");
		int hit4Count = 0;
		double forthSum = 0;
		int hit3Count = 0;
		double tripleSum = 0;

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
			for(int expect = 6 ; expect > 3; expect--){
				subBuffer.append(expect + " : " + lottoService.getHitRateString(nums.size(), hitCount, expect) + "\n");
			}
			subBuffer.append("\n");

			//4개 맞을 확률 계산.
			double forthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 4);
			forthRate = (forthRate <= 0) ? max4 : forthRate;
			if(forthRate < standard4)
				hit4Count++;
			forthSum += forthRate;

			//3개 맞을 확률 계산
			double tripleRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 3);
			tripleRate = (tripleRate <= 0) ? max3 : tripleRate;
			if(tripleRate < standard3)
				hit3Count++;

			tripleSum += tripleRate;
		}

		subBuffer.append(String.format("hit4Rate = %f.2\n", hit4Count * 1.0 / testCount * 100));
		subBuffer.append(String.format("real4Rate = 1/%f.2\n", forthSum / testCount));
		subBuffer.append(String.format("average4Rate = 1/%f.2\n", standard4));
		subBuffer.append(String.format("hit3Rate = %f.2\n", hit3Count * 1.0 / testCount * 100));
		subBuffer.append(String.format("real3Rate = 1/%f.2\n", tripleSum / testCount));
		subBuffer.append(String.format("average3Rate = 1/%f.2\n", standard3));
		subBuffer.append("=====================================\n");
		if(tripleSum / testCount < standard3 || hit4Count * 1.0 / testCount * 100 >=50)
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