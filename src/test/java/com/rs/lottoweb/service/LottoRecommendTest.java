package com.rs.lottoweb.service;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
//	@Ignore
	public void testAnalysis(){

		lottoService.scheduleAnalysis();

		List<Integer> nums = lottoService.getAnalysedFrequentNumbers(lottoService.getCurrentNumber()+1);
//		List<Integer> nums = lottoService.getAnalysedFrequentNumbers(lottoService.getCurrentNumber()); // 저번주 회차 확인;
		
		Random rand = new Random(System.currentTimeMillis());

		for(int j = 0; j < 15; j++){
			List<Integer> numList = new ArrayList<Integer>();
			if(numList.size() >= nums.size())
				break;

			for(int i = 0; i < 6; i++){
				int index = rand.nextInt(nums.size());
				if(numList.contains(nums.get(index))){
					i--;
					continue;
				}
				
				numList.add(nums.get(index));
			}
			
			Collections.sort(numList);

			for(int num : numList){
				System.out.print(num + ", ");
			}
			System.out.println();
			if(j % 5 == 4)
				System.out.println();
		}
		
		for( int num : nums){
			System.out.print(num + ", ");
		}
		System.out.println();
		System.out.println(nums.size());
		
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
	@Ignore
	public void test(){
		List<Integer> list = Arrays.asList(1, 2, 4, 5, 6, 7, 9, 10, 12, 14, 15, 16, 18, 23, 25, 26, 29, 30, 31, 35, 38, 40, 41, 44, 45);
 		
		Random rand = new Random();

		for(int j = 0; j < 5; j++){
			List<Integer> numList = new ArrayList<Integer>();
			for(int i = 0; i < 6; i++){
				int index = rand.nextInt(list.size());
				if(numList.contains(list.get(index))){
					i--;
					continue;
				}
				
				numList.add(list.get(index));
			}
			
			Collections.sort(numList);

			for(int num : numList){
				System.out.print(num + ", ");
			}
			System.out.println();
		}
		
	}
}
