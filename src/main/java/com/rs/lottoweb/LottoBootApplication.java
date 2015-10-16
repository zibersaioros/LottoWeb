package com.rs.lottoweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottoBootApplication{
	public static void main(String[] args) {
		SpringApplication.run(LottoBootApplication.class, args);
	}
}
