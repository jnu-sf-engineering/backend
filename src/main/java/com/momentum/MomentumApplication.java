package com.momentum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class MomentumApplication {

	public static void main(String[] args) {
		SpringApplication.run(MomentumApplication.class, args);
	}
}