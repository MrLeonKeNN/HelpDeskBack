package com.epam.ilyankov.helpdesk.service.api;

import java.util.List;

import org.thymeleaf.context.Context;

public interface EmailService {

	void sendMessage(List<String> emailList, Context context, String subject, String emailTemplateName);

	Context getContext(Long ticketId);

	Context getContext(Long ticketId, String firstName, String lastName);
}
