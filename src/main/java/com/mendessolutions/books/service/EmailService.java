package com.mendessolutions.books.service;

import java.util.List;

public interface EmailService {

	void sendMails(String mensagem, List<String> mailList);

}
