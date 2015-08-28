package com.rs.lottoweb.service;


import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rs.lottoweb.BootApplication;
import com.rs.lottoweb.domain.LottoAnalysis;
import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.mapper.LottoHistoryMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BootApplication.class})
public class LottoRecommendTest {

	
	@Autowired
	LottoHistoryMapper lottoHistoryMapper;
	
	@Autowired
	LottoService lottoService;
	
	@Test
	@Ignore
	public void testInsert(){
		LottoHistory lh = new LottoHistory();
		lh.setRound(661);
		lh.setDat("2015-07-25");
		lh.setChucheomgi(3);
		lh.setNum1_ord(1);
		lh.setNum2_ord(2);
		lh.setNum3_ord(3);
		lh.setNum4_ord(4);
		lh.setNum5_ord(5);
		lh.setNum6_ord(6);
		lh.setNum1(1);
		lh.setNum2(2);
		lh.setNum3(3);
		lh.setNum4(4);
		lh.setNum5(5);
		lh.setNum6(6);
		lh.setNum7(7);
		
		int count = lottoHistoryMapper.insert(lh);
		
		assertTrue(count == 1);
	}
	
	@Test
	@Ignore
	public void testSelectExclusion(){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("column", "num1_ord");
		param.put("round", 650);
		param.put("limit", 50);
		List<LottoAnalysis> list =lottoHistoryMapper.selectExclusion(param);
		
		assertTrue(list.size() > 0);
		
		for(LottoAnalysis anal : list){
			System.out.println("diff : " + anal.getDiff() + " cnt : " + anal.getCnt());
		}
		
	}
	
	@Test
	@Ignore
	public void testGetExclusionNumber(){
		int count = 0;
		int roundCount = 20;
		StringBuffer sb = new StringBuffer();
		for(int i = 0; i < roundCount; i++){
			int round = lottoService.getCurrentNumber() - i;
			List<Integer> nums = lottoService.getExclusionNumber(round, 115, 0);
			LottoHistory history = lottoService.selectByRound(round);
			
			System.out.print("nums : ");
			for(int num : nums){
				System.out.print(num + ", ") ;
			}
			System.out.println("\b\b");
			System.out.print(history.getNum1_ord() + ", ");
			System.out.print(history.getNum2_ord() + ", ");
			System.out.print(history.getNum3_ord() + ", ");
			System.out.print(history.getNum4_ord() + ", ");
			System.out.print(history.getNum5_ord() + ", ");
			System.out.println(history.getNum6_ord());
			
			if(nums.contains(history.getNum1_ord())
					|| nums.contains(history.getNum2_ord())
					|| nums.contains(history.getNum3_ord())
					|| nums.contains(history.getNum4_ord())
					|| nums.contains(history.getNum5_ord())
					|| nums.contains(history.getNum6_ord())){
				System.out.println("count : " + nums.size() + " false");
				continue;
			}
			sb.append("round : " + round + " count : " + nums.size() + "\n");
			count++;
		}
		System.out.println(sb);
		System.out.printf("%f.2\n", count*1.0 / roundCount * 100);
	}
	
	
	@Test
	public void testGetExclusionNumberOne(){
		int round = lottoService.getCurrentNumber();
		List<Integer> nums = lottoService.getExclusionNumber(round+1, 115, 0);
		System.out.println(round+1);
		for(int i : nums){
			System.out.println(i);
		}
	}
	
	@Test
	@Ignore
	public void testRecursive(){
//		List<Integer> list = lottoHistoryMapper.testRecursive();
		int i= lottoHistoryMapper.testRecursive();
		System.out.println(i);
		
//		for(Integer i : list){
//			System.out.println(i);
//		}
	}
}
