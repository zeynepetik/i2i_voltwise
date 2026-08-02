package com.i2iacademy.voltwise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VoltwiseApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoltwiseApplication.class, args);
	}

}
