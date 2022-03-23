package com.ilyankov.helpdesk.service.impl;

import java.util.List;
import java.util.Locale;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.ilyankov.helpdesk.service.api.EmailService;

@Service
@PropertySource("classpath:application.properties")
public class EmailServiceImpl implements EmailService {

	public static final String LINK = "Link";
	public static final String LAST_NAME = "LName";
	public static final String FIRST_NAME = "FName";
	private static final String ID = "id";
	private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);
	private final JavaMailSenderImpl mailSender;
	private final TemplateEngine htmlTemplateEngine;
	@Value("${linkToTicket}")
	private String linkToTicket;

	public EmailServiceImpl(JavaMailSenderImpl mailSender,
			@Qualifier("springTemplateEngine") TemplateEngine htmlTemplateEngine) {
		this.mailSender = mailSender;
		this.htmlTemplateEngine = htmlTemplateEngine;
	}

	@Override
	@Async
	public void sendMessage(List<String> emailList, Context context, String subject, String emailTemplateName) {

		Session session = mailSender.createMimeMessage().getSession();
		final MimeMessage mimeMessage = new MimeMessage(session);
		final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");

		Transport transport;
		try {
			transport = session.getTransport();
		} catch (NoSuchProviderException e) {
			logger.error(e.getMessage());
			return;
		}

		try {
			transport.connect(mailSender.getHost(), mailSender.getPort(), mailSender.getUsername(),
					mailSender.getPassword());
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}

		Address[] addresses = new Address[emailList.size()];
		for (int i = 0; i < emailList.size(); i++) {
			try {
				addresses[i] = new InternetAddress(emailList.get(i));
			} catch (AddressException e) {
				logger.error(e.getMessage());
			}
		}
		try {
			message.setSubject(subject);
			message.setFrom("evgeninyrov@gmail.com");
			String htmlContent = this.htmlTemplateEngine.process(emailTemplateName, context);
			message.setText(htmlContent, true);
			transport.sendMessage(mimeMessage, addresses);
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}

		try {
			transport.close();
		} catch (MessagingException e) {
			logger.error(e.getMessage());
		}
	}

	@Override
	public Context getContext(Long ticketId) {
		Context context = new Context();

		context.setVariable(LINK, String.format(linkToTicket, ticketId));
		context.setVariable(ID, ticketId);

		return context;
	}

	@Override
	public Context getContext(Long ticketId, String firstName, String lastName) {
		Context context = new Context(Locale.ENGLISH);

		context.setVariable(ID, ticketId);
		context.setVariable(LINK, String.format(linkToTicket, ticketId));
		context.setVariable(LAST_NAME, lastName);
		context.setVariable(FIRST_NAME, firstName);

		return context;
	}
}
