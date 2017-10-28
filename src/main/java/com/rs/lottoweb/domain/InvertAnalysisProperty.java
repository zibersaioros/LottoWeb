package com.rs.lottoweb.domain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("invert")
public class InvertAnalysisProperty extends AnalysisProperty {

}
