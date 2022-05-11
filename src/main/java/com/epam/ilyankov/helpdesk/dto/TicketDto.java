package com.epam.ilyankov.helpdesk.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.format.annotation.DateTimeFormat;

import com.epam.ilyankov.helpdesk.domain.ticket.Urgency;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class TicketDto {

	private Long id;

	@Pattern(regexp = "[a-zA-Z0-9~.\"(),:;< >@\\[\\]!#$%&'*+-\\/=?^_`{|}]{0,100}")
	private String name;
	@NotNull
	@NotEmpty
	private String category;
	@Pattern(regexp = "[a-zA-Z0-9~.\"(),:;< >@\\[\\]!#$%&'*+-\\/=?^_`{|}]{0,500}")
	private String description;
	@NotNull
	private Urgency urgency;
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private Date desiredResolutionDate;
	private String createOn;
	private String comment;
	@NotNull
	private String state;
	private String ownerFirstName;
	private String ownerLastName;
	private String approverFirstName;
	private String approverLastName;
	private String assignerFirstName;
	private String assignerLastName;
	private List<String> attachmentName;
	private List<String> actions;
}
