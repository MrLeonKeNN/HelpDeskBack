package com.epam.ilyankov.helpdesk.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDto {

	private Long ticketId;
	private String date;
	private String firstName;
	private String lastName;
	private String description;
	private String action;
}
