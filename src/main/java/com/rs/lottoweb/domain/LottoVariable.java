package com.rs.lottoweb.domain;

public class LottoVariable {

	public static final String EX_ANAL_COUNT = "exclusionAnalysisCount";
	public static final String EX_MIN_RANGE = "exclusionMinRange";
	public static final String EX_MAX_RANGE = "exclusionMaxRange";
	public static final String EX_RANGE_INC = "exclusionRangeIncrease";
	public static final String EX_MIN_SEQUENCE = "exclusionMinSequence";
	public static final String EX_MAX_SEQUENCE = "exclusionMaxSequence";
	
	public static final String FR_ANAL_COUNT = "frequentAnalysisCount";
	public static final String FR_MIN_RANGE = "frequentMinRange";
	public static final String FR_MAX_RANGE = "frequentMaxRange";
	public static final String FR_RANGE_INC = "frequentRangeIncrease";
	public static final String FR_MIN_SEQUENCE = "frequentMinSequence";
	public static final String FR_MAX_SEQUENCE = "frequentMaxSequence";
	
	public static final String IV_ANAL_COUNT = "invertAnalysisCount";
	public static final String IV_MIN_RANGE = "invertMinRange";
	public static final String IV_MAX_RANGE = "invertMaxRange";
	public static final String IV_RANGE_INC = "invertRangeIncrease";
	public static final String IV_MIN_SEQUENCE = "invertMinSequence";
	public static final String IV_MAX_SEQUENCE = "invertMaxSequence";
	
	public static final int EX_ANAL_COUNT_VAL		= 10;
	public static final int EX_MIN_RANGE_VAL 		= 16;
	public static final int EX_MAX_RANGE_VAL 		= 87;
	public static final int EX_RANGE_INC_VAL 		= 11;
	public static final int EX_MIN_SEQUENCE_VAL 	= 7;
	public static final int EX_MAX_SEQUENCE_VAL	= 10;
	
	public static final int FR_ANAL_COUNT_VAL 		= 11;
	public static final int FR_MIN_RANGE_VAL 		= 34;
	public static final int FR_MAX_RANGE_VAL 		= 135;
	public static final int FR_RANGE_INC_VAL 		= 13;
	public static final int FR_MIN_SEQUENCE_VAL 	= 6;
	public static final int FR_MAX_SEQUENCE_VAL	= 11;
	
	public static final int IV_ANAL_COUNT_VAL 		= 13;
	public static final int IV_MIN_RANGE_VAL 		= 52;
	public static final int IV_MAX_RANGE_VAL 		= 151;
	public static final int IV_RANGE_INC_VAL 			= 15;
	public static final int IV_MIN_SEQUENCE_VAL 	= 5;
	public static final int IV_MAX_SEQUENCE_VAL	= 11;
	
	
	private String name;
	private String value;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
