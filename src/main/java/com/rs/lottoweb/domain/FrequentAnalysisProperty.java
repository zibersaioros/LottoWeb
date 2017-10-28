package com.rs.lottoweb.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("frequent")
public class FrequentAnalysisProperty extends AnalysisProperty {

}
