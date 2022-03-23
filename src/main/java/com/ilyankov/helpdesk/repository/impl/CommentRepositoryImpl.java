package com.ilyankov.helpdesk.repository.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.ilyankov.helpdesk.domain.Comment;
import com.ilyankov.helpdesk.repository.api.CommentRepository;

@Repository
public class CommentRepositoryImpl extends CrudRepository<Comment> implements CommentRepository {

	private static final String SELECT_COMMENT_BY_TICKET_ID = "FROM Comment c where c.ticket.id =:ticketId ORDER BY c.id DESC";
	private static final String TICKET_ID = "ticketId";

	public CommentRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Comment.class);
	}

	@Override
	public List<Comment> getByTicketId(Long ticketId) {
		final Query<Comment> query = sessionFactory.getCurrentSession().createQuery(SELECT_COMMENT_BY_TICKET_ID,
				Comment.class);
		query.setParameter(TICKET_ID, ticketId);

		return query.list();
	}

	@Override
	public List<Comment> getByTicketIdQuantity(Long ticketId, Integer firstResult, Integer maxResult) {
		final Query<Comment> query = sessionFactory.getCurrentSession().createQuery(SELECT_COMMENT_BY_TICKET_ID,
				Comment.class);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		query.setParameter(TICKET_ID, ticketId);

		return query.list();
	}
}
