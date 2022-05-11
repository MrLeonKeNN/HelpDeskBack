package com.epam.ilyankov.helpdesk.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackDto {

	@NotNull
	private Integer rate;
	private String description;
	@NotNull
	private Long ticketId;
}
