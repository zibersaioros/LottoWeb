package com.rs.lottoweb.domain;

public class ExclusionAnalysis {

	private int range;
	private int sequence;
	
	public ExclusionAnalysis(int range, int sequence){
		this.range = range;
		this.sequence = sequence;
	}
	
	
	public int getRange() {
		return range;
	}
	public void setRange(int range) {
		this.range = range;
	}
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
}
