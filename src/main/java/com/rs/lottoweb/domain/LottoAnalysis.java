package com.rs.lottoweb.domain;

public class LottoAnalysis {
	private String column;
	private int diff;
	private int cnt;
	
	public int getDiff() {
		return diff;
	}
	public void setDiff(int diff) {
		this.diff = diff;
	}
	public int getCnt() {
		return cnt;
	}
	public void setCnt(int cnt) {
		this.cnt = cnt;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
}
