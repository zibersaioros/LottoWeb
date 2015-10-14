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
	public static final int MODE_CONFIRM = 1;
	public static final int MODE_ANALYSIS = 2;

	@Autowired
	LottoService lottoService;

	Random random = new Random(System.currentTimeMillis());


	@Before
	public void begin(){
		random = new Random(System.currentTimeMillis());
	}

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
		lottoService.clearAllCache();

		List<Integer> count1List = new ArrayList<Integer>();
		List<Integer> count2List = new ArrayList<Integer>();
		List<Integer> count3List = new ArrayList<Integer>();
		List<Integer> count4List = new ArrayList<Integer>();
		List<Integer> count5List = new ArrayList<Integer>();
		List<Integer> count6List = new ArrayList<Integer>();

		int[] range1 = {9, 18};
		int[] range2 = {8, 60};
		int[] range3 = {120, 180};
		int[] range4 = {2, 21};
		int[] range5 = {0, range1[1]};
		int[] range6 = {0, range1[1]};
		
//		int[] range1 = {LottoVariable.EX_ANAL_COUNT_VAL, LottoVariable.EX_ANAL_COUNT_VAL};
//		int[] range2 = {LottoVariable.EX_MIN_RANGE_VAL, LottoVariable.EX_MIN_RANGE_VAL};
//		int[] range3 = {LottoVariable.EX_MAX_RANGE_VAL, LottoVariable.EX_MAX_RANGE_VAL};
//		int[] range4 = {LottoVariable.EX_RANGE_INC_VAL, LottoVariable.EX_RANGE_INC_VAL};
//		int[] range5 = {LottoVariable.EX_MIN_SEQUENCE_VAL, LottoVariable.EX_MIN_SEQUENCE_VAL};
//		int[] range6 = {LottoVariable.EX_MAX_SEQUENCE_VAL, LottoVariable.EX_MAX_SEQUENCE_VAL};
		
		int successCount = 0;
		int executionCount = 0;
		int loopCount = 0;
		for(int count1 = 0 ; count1 < 6 && loopCount < 10 && successCount < 10; count1++){

			analysisCount = getAnalysisRandNum(range1);
			if(count1List.contains(analysisCount)){
				count1--;
				loopCount++;
				continue;
			}

			count1List.add(analysisCount);
			for(int count2 = 0; count2 < 6 && loopCount < 10 && successCount < 10; count2++){
				minRange = getAnalysisRandNum(range2);
				if(count2List.contains(minRange)){
					loopCount++;
					count2--; 
					continue;
				}
				count2List.add(minRange);
				
				for(int count3 = 0; count3 < 6 && loopCount < 10 && successCount < 10; count3++){
					maxRange = getAnalysisRandNum(range3);
					if(count3List.contains(maxRange) || maxRange <= minRange){
						count3--; 
						loopCount++;
						continue;
					}
					count3List.add(maxRange);
					for(int count4 = 0; count4 < 6 && loopCount < 10 && successCount < 10; count4++){
						rangeIncrease = getAnalysisRandNum(range4);
						if(count4List.contains(rangeIncrease)){
							loopCount++;
							count4--; 
							continue;
						}
						count4List.add(rangeIncrease);

						for(int count5 = 0; count5 < 6 && loopCount < 20 && successCount < 10; count5++){
							minSeq = getAnalysisRandNum(range5, analysisCount);
							if(count5List.contains(minSeq)){
								loopCount++;
								count5--;
								continue;
							}
							count5List.add(minSeq);
							for(int count6 = 0; count6 < 6 && loopCount < 20 && successCount < 10; count6++) {
								maxSeq = getAnalysisRandNum(range6, analysisCount);
								if(count6List.contains(maxSeq) || maxSeq < minSeq){
									loopCount++;
									count6--;
									continue;
								}
								executionCount++;
								
								count6List.add(maxSeq);
								
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
								if(realHitRate > averageRate * 3 || hitRate >= 40 || (realHitRate > averageRate * 2 && realHitRate >= 30)){
									sb.append(subBuffer);
									successCount++;
								}
							}
							loopCount = 0;
						}
						loopCount = 0;
					}
					loopCount = 0;
				}
				loopCount = 0;
			}
			loopCount = 0;
		}
		

//		for(analysisCount = 11 ; analysisCount <= 13; analysisCount++){
//			for(minRange = 12 ; minRange <= 20; minRange += 4){
//				for(maxRange = 52; maxRange <= 60; maxRange += 4){
//					for(rangeIncrease = 2; rangeIncrease <= 3; rangeIncrease++){
//						for(minSeq = 0; minSeq <= 2;  minSeq++){
//							for(maxSeq = 4; maxSeq <= 6; maxSeq++){
//								int count = 0;	
//								double numsCount = 0;
//								int hitCount = 0;
//
//								StringBuffer subBuffer = new StringBuffer();
//								subBuffer.append("testCount = " + testCount + "\n");
//								subBuffer.append("analysisCount = " + analysisCount + "\n");
//								subBuffer.append("minRange = " + minRange + "\n");
//								subBuffer.append("maxRange = " + maxRange + "\n");
//								subBuffer.append("rangeIncrease = " + rangeIncrease + "\n");
//								subBuffer.append("minSeq = " + minSeq + "\n");
//								subBuffer.append("maxSeq = " + maxSeq + "\n");
//
//								for(int i = 0; i < testCount; i++){
//									int round = lottoService.getCurrentNumber() - i;
//									List<AnalysisResult> analList = lottoService.analysisExclusion(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
//									List<Integer> nums = new ArrayList<Integer>();
//									for(AnalysisResult anal : analList){
//										nums.addAll(lottoService.getExclusionNumber(round, anal.getRange(), anal.getSequence()));
//									}
//
//									nums = lottoService.removeDuplicate(nums);
//									LottoHistory history = lottoService.selectByRound(round);
//
//									subBuffer.append("round : " + round + " count : " + nums.size());
//									numsCount += nums.size();
//
//									if(nums.contains(history.getNum1_ord())
//											|| nums.contains(history.getNum2_ord())
//											|| nums.contains(history.getNum3_ord())
//											|| nums.contains(history.getNum4_ord())
//											|| nums.contains(history.getNum5_ord())
//											|| nums.contains(history.getNum6_ord())){
//										subBuffer.append(" false\n");
//										continue;
//									}
//
//									hitCount += nums.size();
//									subBuffer.append("\n");
//									count++;
//								}
//
//								if(Math.ceil(numsCount / testCount) > 38)
//									continue;
//
//								double hitRate =  count*1.0 / testCount * 100;
//								double realHitRate = hitCount / numsCount * 100;
//
//								double averageRate = lottoService.getExclusionRate((int)Math.ceil(numsCount / testCount))*100;
//								subBuffer.append(String.format("hitRate = %f.2\n", hitRate));
//								subBuffer.append(String.format("realHitRate = %f.2\n", realHitRate));
//								subBuffer.append(String.format("averageRate = %f.2\n", averageRate));
//								subBuffer.append("=====================================\n");
//								if(realHitRate > averageRate * 2)
//									sb.append(subBuffer);
//							}
//						}
//					}
//				}
//			}
//		}
		System.out.println(sb.toString());
		System.out.println("execute : " +  executionCount);
		System.out.println("loop : " + loopCount);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data", "analysisResult.txt")));
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
		int testCount = 20;
		int analysisCount = 20;
		int minRange = 10;
		int maxRange = 48;
		int rangeIncrease = 10;
		int minSeq = 0;
		int maxSeq = 1; //analysisCount / 2;

		StringBuffer sb = new StringBuffer();
		lottoService.clearAllCache();

		double standard4 = lottoService.getHitRateDenominator(45, 6, 4);
		double max4 = lottoService.getHitRateDenominator(43, 4, 4);
		double standard3 = lottoService.getHitRateDenominator(45, 6, 3);
		double max3 = lottoService.getHitRateDenominator(42, 3, 3);
		
		List<Integer> count1List = new ArrayList<Integer>();
		List<Integer> count2List = new ArrayList<Integer>();
		List<Integer> count3List = new ArrayList<Integer>();
		List<Integer> count4List = new ArrayList<Integer>();
		List<Integer> count5List = new ArrayList<Integer>();
		List<Integer> count6List = new ArrayList<Integer>();

		int[] range1 = {9, 18};
		int[] range2 = {8, 30};
		int[] range3 = {80, 160};
		int[] range4 = {2, 21};
		int[] range5 = {0, range1[1]};
		int[] range6 = {0, range1[1]};
		int loopCount = 0;
		
		for(int count1 = 0; count1 < 2 && loopCount < 5; count1++, loopCount++){

			analysisCount = getAnalysisRandNum(range1);
			if(count1List.contains(analysisCount)){
				count1--;
				continue;
			}

			count1List.add(analysisCount);
			for(int count2 = 0; count2 < 3 && loopCount < 5; count2++, loopCount++){
				minRange = getAnalysisRandNum(range2);
				if(count2List.contains(minRange)){
					count2--; 
					continue;
				}
				count2List.add(minRange);
				for(int count3 = 0; count3 < 3 && loopCount < 5; count3++, loopCount++){
					maxRange = getAnalysisRandNum(range3);
					if(count3List.contains(maxRange) || maxRange <= minRange){
						count3--; 
						continue;
					}
					count3List.add(maxRange);

					for(int count4 = 0; count4 < 3 && loopCount < 5; count4++, loopCount++){
						rangeIncrease = getAnalysisRandNum(range4);
						if(count4List.contains(rangeIncrease)){
							count4--; 
							continue;
						}
						count4List.add(rangeIncrease);

						for(int count5 = 0; count5 < 3 && loopCount < 5; count5++, loopCount++){
							minSeq = getAnalysisRandNum(range5);
							if(count5List.contains(minSeq)){
								count5--;
								continue;
							}
							count5List.add(minSeq);

							for(int count6 = 0; count6 < 2 && loopCount < 5; count6++, loopCount++){
								maxSeq = getAnalysisRandNum(range6);
								if(count6List.contains(maxSeq) || maxSeq < minSeq){
									count6--;
									continue;
								}
								count6List.add(maxSeq);
								
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
									sb.append(subBuffer);
							}
							loopCount = 0;
						}
						loopCount = 0;
					}
					loopCount = 0;
				}
				loopCount = 0;
			}
			loopCount = 0;
		}
		
//		for(analysisCount = 12 ; analysisCount <= 12; analysisCount++){
//			for(minRange = 8; minRange <= 8; minRange += 4){
//				for(maxRange = 120; maxRange <= 120; maxRange += 12){
//					for(rangeIncrease = 3; rangeIncrease <= 5; rangeIncrease++){
//						for(minSeq = 0; minSeq <= 1;  minSeq++){
//							for(maxSeq = 0; maxSeq <= 4; maxSeq++){
//
//								if(maxSeq < minSeq)
//									continue;
//
//								StringBuffer subBuffer = new StringBuffer();
//								subBuffer.append("testCount = " + testCount + "\n");
//								subBuffer.append("analysisCount = " + analysisCount + "\n");
//								subBuffer.append("minRange = " + minRange + "\n");
//								subBuffer.append("maxRange = " + maxRange + "\n");
//								subBuffer.append("rangeIncrease = " + rangeIncrease + "\n");
//								subBuffer.append("minSeq = " + minSeq + "\n");
//								subBuffer.append("maxSeq = " + maxSeq + "\n");
//								int hit4Count = 0;
//								double forthSum = 0;
//								int hit3Count = 0;
//								double tripleSum = 0;
//
//								for(int i = 0; i < testCount; i++){
//									int round = lottoService.getCurrentNumber() - i;
//									List<AnalysisResult> analList = lottoService.analysisFrequent(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
//									List<Integer> nums = new ArrayList<Integer>();
//									for(AnalysisResult anal : analList){
//										nums.addAll(lottoService.getFrequentNumber(round, anal.getRange(), anal.getSequence()));
//									}
//
//									nums = lottoService.removeDuplicate(nums);
//
//									//각각 확률 계산.
//									int hitCount = lottoService.getHitCount(nums, round) ;
//									subBuffer.append("round : " + round + " count : " + nums.size());
//									subBuffer.append(" hit : " + hitCount +"\n");
//									for(int expect = 6 ; expect > 3; expect--){
//										subBuffer.append(expect + " : " + lottoService.getHitRateString(nums.size(), hitCount, expect) + "\n");
//									}
//									subBuffer.append("\n");
//
//									//4개 맞을 확률 계산.
//									double forthRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 4);
//									forthRate = (forthRate <= 0) ? max4 : forthRate;
//									if(forthRate < standard4)
//										hit4Count++;
//									forthSum += forthRate;
//
//									//3개 맞을 확률 계산
//									double tripleRate = lottoService.getHitRateDenominator(nums.size(), hitCount, 3);
//									tripleRate = (tripleRate <= 0) ? max3 : tripleRate;
//									if(tripleRate < standard3)
//										hit3Count++;
//
//									tripleSum += tripleRate;
//								}
//
//								subBuffer.append(String.format("hit4Rate = %f.2\n", hit4Count * 1.0 / testCount * 100));
//								subBuffer.append(String.format("real4Rate = 1/%f.2\n", forthSum / testCount));
//								subBuffer.append(String.format("average4Rate = 1/%f.2\n", standard4));
//								subBuffer.append(String.format("hit3Rate = %f.2\n", hit3Count * 1.0 / testCount * 100));
//								subBuffer.append(String.format("real3Rate = 1/%f.2\n", tripleSum / testCount));
//								subBuffer.append(String.format("average3Rate = 1/%f.2\n", standard3));
//								subBuffer.append("=====================================\n");
//								if(tripleSum / testCount < standard3 || hit4Count * 1.0 / testCount * 100 >=50)
//									sb.append(subBuffer);
//							}
//						}
//					}
//				}
//			}
//		}

		System.out.println(sb.toString());
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("data", "analysisFrequentResult.txt")));
			bw.write(sb.toString());
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
}