package com.ilyankov.helpdesk.repository.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.ilyankov.helpdesk.domain.history.History;
import com.ilyankov.helpdesk.repository.api.HistoryRepository;

@Repository
public class HistoryRepositoryImpl extends CrudRepository<History> implements HistoryRepository {

	private static final String SELECT_HISTORY_BY_TICKET_ID_QUERY = "From History h where h.ticket.id =:ticketId ORDER BY h.date DESC";
	private static final String TICKET_ID = "ticketId";

	public HistoryRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, History.class);
	}

	@Override
	public List<History> getByTicketId(Long ticketId) {
		final Query<History> query = sessionFactory.getCurrentSession().createQuery(SELECT_HISTORY_BY_TICKET_ID_QUERY,
				History.class);

		query.setParameter(TICKET_ID, ticketId);

		return query.list();
	}

	@Override
	public List<History> getByTicketIdQuantity(Long ticketId, Integer firstResult, Integer maxResult) {
		final Query<History> query = sessionFactory.getCurrentSession().createQuery(SELECT_HISTORY_BY_TICKET_ID_QUERY,
				History.class);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		query.setParameter(TICKET_ID, ticketId);

		return query.list();
	}
}
