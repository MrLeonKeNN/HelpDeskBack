package com.epam.ilyankov.helpdesk.repository.api;

import java.util.Optional;

import org.hibernate.SessionFactory;

public class CrudRepository<E> {

	public final SessionFactory sessionFactory;
	private final Class<E> type;

	public CrudRepository(SessionFactory sessionFactory, Class<E> type) {
		this.sessionFactory = sessionFactory;
		this.type = type;
	}

	public void save(E e) {
		sessionFactory.getCurrentSession().save(e);
	}

	public void remove(E e) {
		sessionFactory.getCurrentSession().remove(e);
	}

	public void update(E e) {
		sessionFactory.getCurrentSession().update(e);
	}

	public Optional<E> getById(Long id) {
		return Optional.ofNullable(sessionFactory.getCurrentSession().get(type, id));
	}
}
