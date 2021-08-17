package com.mendessolutions.books;

import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.mendessolutions.books.service.EmailService;

@SpringBootApplication
@EnableScheduling
public class BookspringApplication implements CommandLineRunner{
	
	@Autowired
	private EmailService emailService;

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(BookspringApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		List<String> emails = Arrays.asList("c1dc15f2cf-5c1fd8@inbox.mailtrap.io");
		emailService.sendMails("Testando envio de emails", emails);
		System.out.println("Emails enviados");
	}

}
