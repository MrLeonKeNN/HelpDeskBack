package com.ilyankov.helpdesk.repository.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ilyankov.helpdesk.domain.user.Role;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.repository.api.UserRepository;

@Repository
public class UserRepositoryImpl extends CrudRepository<User> implements UserRepository {

	private static final String SELECT_BY_EMAIL_QUERY = "From User where email =:email";
	private static final String SELECT_EMAIL_BY_ROLE_QUERY = "select u.email from User u where u.role =:role";
	private static final String EMAIL = "email";
	private static final String ROLE = "role";

	public UserRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, User.class);
	}

	@Override
	@Cacheable("user")
	@Transactional
	public Optional<User> getByEmail(String email) {
		final Query<User> query = sessionFactory.getCurrentSession().createQuery(SELECT_BY_EMAIL_QUERY, User.class);

		query.setParameter(EMAIL, email);

		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<String> getEmailsByRole(Role role) {
		final Query<String> query = sessionFactory.getCurrentSession().createQuery(SELECT_EMAIL_BY_ROLE_QUERY,
				String.class);

		query.setParameter(ROLE, role);

		return query.list();
	}
}
