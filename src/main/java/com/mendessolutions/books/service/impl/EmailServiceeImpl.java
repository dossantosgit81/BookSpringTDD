package com.mendessolutions.books.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.mendessolutions.books.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceeImpl implements EmailService{
	
	@Value("${application.mail.default-remetent}")
	private String remetent;
	
	private final JavaMailSender javaMailSender;

	@Override
	public void sendMails(String mensagem, List<String> mailList) {
		String[] mails = mailList.toArray(new String[mailList.size()]);
		
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(remetent);
		mailMessage.setSubject("Livro com emprestimo atrasado");
		mailMessage.setText(mensagem);
		mailMessage.setTo(mails);
		
		javaMailSender.send(mailMessage);		
	}

}
