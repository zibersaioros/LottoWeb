package com.rs.lottoweb.service;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.config.AppConfig;
import com.rs.lottoweb.domain.AnalysisResult;
import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.mapper.LottoExclusionMapper;
import com.rs.lottoweb.mapper.LottoHistoryMapper;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={LottoBootApplication.class, AppConfig.class})
@TransactionConfiguration(defaultRollback=true, transactionManager="transactionManager")
@Transactional // 트랜잭션을 일으키려면 이 어노테이션이 필요함
public class LottoServiceTest {
	
	@Autowired
	LottoService lottoService;
	
	@Autowired
	LottoHistoryMapper lottoHistoryMapper;
	
	@Autowired
	LottoExclusionMapper lottoExclusionMapper;
	
	@Before
	public void setup(){
		
	}
	
	
	@Test
	@Rollback
	public void testInsert(){
		
		LottoHistory lh = new LottoHistory();
		lh.setRound(lottoService.getCurrentNumber() +1 );
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
		
		int count = lottoService.insert(lh);
		assertTrue(count == 1);
	}

	@Test
	public void testSelectByRound(){
		LottoHistory lh = lottoService.selectByRound(lottoService.getCurrentNumber()+1);
		assertThat(lh, is(nullValue()));
		
		lh = lottoService.selectByRound(lottoService.getCurrentNumber()-10);
		assertThat(lh, is(notNullValue()));
	}
	
	@Test
	public void testScheduledInsert() throws Exception{
		int old = lottoHistoryMapper.getLottoCount();
		lottoService.scheduleInsert();
		int nw = lottoHistoryMapper.getLottoCount();
		
		assertThat(nw, is(not(old)));
	}
	
	
	@Test
	public void testExclusionRate(){
		double rate = lottoService.getExclusionRate(1);
		assertThat(rate, greaterThan(0.866));
		assertThat(rate, lessThan(0.867));
	}
	
	
	@Test
	@Rollback
	@Ignore
	public void testInsertExclusion(){
		
		List<Integer> nums = new ArrayList<Integer>();
		for(int i = 1; i<= 7; i++){
			nums.add(i);
		}
		
		int round = lottoService.getCurrentNumber() + 1;
		
		//nums를 돌아가면서 insert  db 커넥션을 줄이기 위해 한번에 삽입
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("round", lottoService.getCurrentNumber()+1);
		params.put("list", nums);
		lottoExclusionMapper.insert(params);
		
		List<Integer> exclusionNums = lottoService.getAnalysedExclusionNumbers(round);
		assertThat(exclusionNums, notNullValue());
		assertThat(exclusionNums.size(), greaterThan(0));
	}
	
	
	@Test
	@Ignore
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
				sb.append(expect + " : " + lottoService.getHitRateString(nums.size(), hitCount, expect) + "\n");
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
	
	@Test
	public void testHitCount(){
		int hitCount = lottoService.getHitCount(Arrays.asList(12, 14, 15, 24, 27, 32), 668);
		assertThat(hitCount, is(6));
	}
}