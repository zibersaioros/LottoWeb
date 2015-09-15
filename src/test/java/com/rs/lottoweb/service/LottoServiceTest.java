package com.rs.lottoweb.service;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import com.rs.lottoweb.LottoBootApplication;
import com.rs.lottoweb.config.AppConfig;
import com.rs.lottoweb.domain.LottoHistory;
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
	
	@Before
	public void setup(){
		
	}
	
	
	@Test
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
	
}