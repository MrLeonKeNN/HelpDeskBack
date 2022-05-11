package com.epam.ilyankov.helpdesk.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableAsync
@PropertySource("classpath:mail.properties")
@ComponentScan("epam.ilyankov")
public class MailConfig {

	private static final String EMAIL_TEMPLATE_ENCODING = "UTF-8";
	@Value("${login}")
	private String userName;
	@Value("${password}")
	private String password;
	@Value("${port}")
	private int port;
	@Value("${host}")
	private String host;

	@Bean
	public JavaMailSenderImpl javaMailSenderImpl() {
		JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
		javaMailSender.setHost(host);
		javaMailSender.setPort(port);

		javaMailSender.setUsername(userName);
		javaMailSender.setPassword(password);

		Properties props = javaMailSender.getJavaMailProperties();

		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.debug", "true");

		return javaMailSender;
	}

	@Bean
	public TemplateEngine springTemplateEngine() {
		final SpringTemplateEngine templateEngine = new SpringTemplateEngine();

		templateEngine.addTemplateResolver(getTemplate());

		return templateEngine;
	}

	private ITemplateResolver getTemplate() {
		final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

		templateResolver.setOrder(2);
		templateResolver.setPrefix("/mail/html/");
		templateResolver.setSuffix(".html");
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCharacterEncoding(EMAIL_TEMPLATE_ENCODING);
		templateResolver.setCacheable(false);

		return templateResolver;
	}
}
