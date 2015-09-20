package com.rs.lottoweb.domain;

public class AnalysisResult {

	private int range;
	private int sequence;
	
	public AnalysisResult(int range, int sequence){
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
