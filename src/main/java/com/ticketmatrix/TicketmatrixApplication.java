package com.ticketmatrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TicketmatrixApplication {

	static void main(String[] args) {
		SpringApplication.run(TicketmatrixApplication.class, args);

		System.out.println(" 🚀 TicketMatrix Engine Started Successfully!");
	}
}
