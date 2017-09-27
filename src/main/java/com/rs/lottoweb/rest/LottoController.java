package com.rs.lottoweb.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rs.lottoweb.domain.LottoHistory;
import com.rs.lottoweb.domain.LottoNumbers;
import com.rs.lottoweb.service.LottoService;


@RestController
@RequestMapping("/anal")
public class LottoController {
	
	@Autowired
	LottoService lottoService;

	@RequestMapping(value="/{round}", method=RequestMethod.GET)
	public LottoNumbers recommend(@PathVariable(value="round") int round) throws Exception{
		
		List<Integer> nums = lottoService.getAnalysedFrequentNumbers(round);
		
		if(nums == null || nums.size() < 1)
			throw new Exception("not analysed");
		LottoNumbers lottoNums = new LottoNumbers();
		lottoNums.setRound(round);
		lottoNums.setNums(nums);
		
		return lottoNums;
	}
	
	@GetMapping("")
	public List<LottoHistory> select() throws Exception{
		return lottoService.getAllRound();
	}
}
