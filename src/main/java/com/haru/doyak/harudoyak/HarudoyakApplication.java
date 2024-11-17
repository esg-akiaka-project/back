package com.haru.doyak.harudoyak;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class HarudoyakApplication {

	public static void main(String[] args) {
		SpringApplication.run(HarudoyakApplication.class, args);
	}

}
