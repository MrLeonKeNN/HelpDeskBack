package com.ilyankov.helpdesk.repository.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.ilyankov.helpdesk.domain.Category;
import com.ilyankov.helpdesk.repository.api.CategoryRepository;

@Repository
public class CategoryRepositoryImpl extends CrudRepository<Category> implements CategoryRepository {

	public static final String SELECT_ALL_QUERY = "From Category";
	private static final String SELECT_BY_NAME_QUERY = "FROM Category c where c.name =:name";
	private static final String NAME = "name";

	public CategoryRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Category.class);
	}

	@Override
	public Optional<Category> getByName(String name) {
		final Query<Category> query = sessionFactory.getCurrentSession().createQuery(SELECT_BY_NAME_QUERY,
				Category.class);

		query.setParameter(NAME, name);

		return query.getResultList().stream().findFirst();
	}

	@Override
	public List<Category> getAll() {
		final Query<Category> query = sessionFactory.getCurrentSession().createQuery(SELECT_ALL_QUERY, Category.class);

		return query.list();
	}
}
