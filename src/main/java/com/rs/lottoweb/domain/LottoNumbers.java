package com.rs.lottoweb.domain;

import java.util.List;

public class LottoNumbers {
	private int round;
	private List<Integer> nums;
	
	
	public int getRound() {
		return round;
	}
	public void setRound(int round) {
		this.round = round;
	}
	public List<Integer> getNums() {
		return nums;
	}
	public void setNums(List<Integer> nums) {
		this.nums = nums;
	}
}
