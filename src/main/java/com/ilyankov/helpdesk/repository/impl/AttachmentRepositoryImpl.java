package com.ilyankov.helpdesk.repository.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.ilyankov.helpdesk.domain.Attachment;
import com.ilyankov.helpdesk.repository.api.AttachmentRepository;

@Repository
public class AttachmentRepositoryImpl extends CrudRepository<Attachment> implements AttachmentRepository {

	private static final String SELECT_BY_TICKET_ID_AND_NAME_QUERY = "FROM Attachment a where a.ticket.id =:ticketId and a.name =:name";
	private static final String SELECT_BY_TICKET_ID = "FROM Attachment a where a.ticket.id =:ticketId";
	private static final String NAME = "name";
	private static final String TICKET_ID = "ticketId";

	public AttachmentRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Attachment.class);
	}

	@Override
	public Optional<Attachment> getByTicketIdAndName(Long ticketId, String name) {
		final Query<Attachment> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_BY_TICKET_ID_AND_NAME_QUERY, Attachment.class);

		query.setParameter(TICKET_ID, ticketId);
		query.setParameter(NAME, name);

		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<Attachment> getNamesByTicketId(Long ticketId) {
		final Query<Attachment> query = sessionFactory.getCurrentSession().createQuery(SELECT_BY_TICKET_ID,
				Attachment.class);

		query.setParameter(TICKET_ID, ticketId);

		return query.list();
	}

	@Override
	public void remove(Attachment attachment) {
		sessionFactory.getCurrentSession().remove(attachment);
	}
}
