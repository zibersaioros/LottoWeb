package com.rs.lottoweb.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.rs.lottoweb.BootApplication;
import com.rs.lottoweb.domain.ExclusionAnalysis;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={BootApplication.class})
public class AnalysisTest {

	
	@Autowired
	LottoService lottoService;
	
	@Test
	public void testAnalysis(){
		ExclusionAnalysis anal = lottoService.analysisExclusion(20, 40, 120, 0, 10);
		System.out.println("limit : " + anal.getLimit() + " seq : " + anal.getSequence());
	}
	
}
