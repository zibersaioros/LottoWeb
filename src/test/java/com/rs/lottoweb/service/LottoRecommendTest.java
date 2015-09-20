package com.rs.lottoweb.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.domain.AnalysisResult;
import com.rs.lottoweb.mapper.LottoHistoryMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LottoBootApplication.class})
public class LottoRecommendTest {


	@Autowired
	LottoHistoryMapper lottoHistoryMapper;

	@Autowired
	LottoService lottoService;


	@Test
	@Ignore
	public void testGetExclusionNumber(){

		lottoService.scheduleExclusion();

		List<Integer> nums = lottoService.getAnalysedExclusionNumbers(lottoService.getCurrentNumber()+1);

		for(int num : nums){
			System.out.println(num);
		}
	}	



	@Test
	@Ignore
	public void testGetFrequentNumber(){
		int analysisCount = 12;
		int minRange = 10;
		int maxRange = 120;
		int rangeIncrease = 5;
		int minSeq = 0;
		int maxSeq = 0; //analysisCount / 2;

		int round = lottoService.getCurrentNumber() + 1;
		List<AnalysisResult> analList = lottoService.analysisFrequent(round-1, analysisCount, minRange, maxRange, rangeIncrease, minSeq, maxSeq);
		List<Integer> nums = new ArrayList<Integer>();
		for(AnalysisResult anal : analList){
			nums.addAll(lottoService.getFrequentNumber(round, anal.getRange(), anal.getSequence()));
		}

		nums = lottoService.removeDuplicate(nums);

		for(int num : nums){
			System.out.print(num + ", ");
		}
	}
	
	
	@Test
	public void test(){
		List<Integer> list = Arrays.asList(5, 11, 15, 16, 20, 21, 26, 32, 39, 43);
 		List<Integer> exclusion = Arrays.asList(1, 4, 7, 8, 12, 19, 23, 24, 34, 36, 37, 45);
 		
// 		list.removeAll(exclusion);
 		
// 		5, 11, 15, 16, 20, 21, 26, 32, 39, 43
 		
		Random rand = new Random();

		for(int j = 0; j < 10; j++){
			List<Integer> numList = new ArrayList<Integer>();
			for(int i = 0; i < 6; i++){
				int index = rand.nextInt(list.size());
				if(numList.contains(list.get(index))){
					i--;
					continue;
				}
				
				numList.add(list.get(index));
			}

			for(int num : numList){
				System.out.print(num + ", ");
			}
			System.out.println();
		}
		
	}
}
