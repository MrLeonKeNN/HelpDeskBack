package com.epam.ilyankov.helpdesk.repository.impl;

import java.util.Optional;

import com.epam.ilyankov.helpdesk.repository.api.CrudRepository;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.epam.ilyankov.helpdesk.domain.Feedback;
import com.epam.ilyankov.helpdesk.repository.api.FeedbackRepository;

@Repository
public class FeedbackRepositoryImpl extends CrudRepository<Feedback> implements FeedbackRepository {

	private static final String SELECT_BY_TICKET_ID_QUERY = "FROM Feedback f where f.ticket.id =:ticketId";
	private static final String TICKET_ID = "ticketId";

	public FeedbackRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Feedback.class);
	}

	@Override
	public Optional<Feedback> getByTicketId(Long ticketId) {
		final Query<Feedback> query = sessionFactory.getCurrentSession().createQuery(SELECT_BY_TICKET_ID_QUERY,
				Feedback.class);

		query.setParameter(TICKET_ID, ticketId);

		return query.getResultList().stream().findFirst();
	}
}
