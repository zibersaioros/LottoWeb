package com.rs.lottoweb.domain;

public class ExclusionAnalysis {

	private int limit;
	private int sequence;
	
	public ExclusionAnalysis(int limit, int sequence){
		this.limit = limit;
		this.sequence = sequence;
	}
	
	
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
}
