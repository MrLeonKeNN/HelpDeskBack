package com.epam.ilyankov.helpdesk.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CommentDto {

	private Long ticketId;
	@NotNull
	@NotEmpty
	private String text;
	private String date;
	private String firstName;
	private String lastName;
}
