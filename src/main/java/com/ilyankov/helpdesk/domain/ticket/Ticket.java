package com.ilyankov.helpdesk.domain.ticket;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.ilyankov.helpdesk.domain.Attachment;
import org.hibernate.Hibernate;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class Ticket {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "CREATED_ON")
	private Timestamp createdOn;

	@Column(name = "DESIRED_RESOLUTION_DATE")
	private Timestamp desiredResolutionDate;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "ASSIGNEE_ID")
	@ToString.Exclude
	private User assignee;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "OWNER_ID")
	@ToString.Exclude
	private User owner;

	@Enumerated(EnumType.ORDINAL)
	@Column(name = "STATE_ID")
	private State state;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "CATEGORY_ID")
	@Fetch(FetchMode.SELECT)
	@BatchSize(size = 5)
	@ToString.Exclude
	private Category category;

	@Column(name = "URGENCY_ID")
		private Urgency urgency;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "APPROVER_ID")
	@ToString.Exclude
	private User approver;

	@OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.MERGE)
	@JoinColumn(name = "TICKET_ID")
	@Fetch(FetchMode.SELECT)
	@BatchSize(size = 5)
	private List<Attachment> attachments;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
			return false;
		Ticket ticket = (Ticket) o;
		return id != null && Objects.equals(id, ticket.id);
	}

	@Override
	public int hashCode() {
		return getClass().hashCode();
	}
}
