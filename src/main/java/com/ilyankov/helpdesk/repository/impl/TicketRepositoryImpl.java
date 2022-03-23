package com.ilyankov.helpdesk.repository.impl;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import com.ilyankov.helpdesk.domain.ticket.State;
import com.ilyankov.helpdesk.domain.ticket.Ticket;
import com.ilyankov.helpdesk.domain.user.User;
import com.ilyankov.helpdesk.repository.api.TicketRepository;

@Repository
public class TicketRepositoryImpl extends CrudRepository<Ticket> implements TicketRepository {

	public static final String SELECT_MY_TICKET_BY_OWNER_MANAGER
			= "FROM Ticket t WHERE t.owner =: manager or t.approver =:approver "
			+ "ORDER BY t.urgency ASC,t.desiredResolutionDate DESC, t.id ASC";
	public static final String SELECT_MY_TICKET_BY_OWNER_ENGINEER = "FROM Ticket t Where  t.assignee =:engineer "
			+ "ORDER BY t.urgency ASC , t.desiredResolutionDate DESC, t.id ASC";

	public static final String COUNT_MY_TICKET_BY_OWNER_MANAGER =
			"SELECT count(*) from Ticket t WHERE t.owner =: manager or t.approver =:approver and t.state =:state";
	public static final String COUNT_MY_TICKET_BY_OWNER_ENGINEER =
			"SELECT count(*) from Ticket t Where  t.assignee =:engineer";

	private static final String SELECT_ALL_BY_EMPLOYEE_QUERY = "FROM Ticket t  WHERE t.owner =:owner "
			+ "ORDER BY t.urgency ASC , t.desiredResolutionDate DESC";
	private static final String SELECT_ALL_BY_OWNER_MANAGER_OR_STATE_OR_APPROVER_QUERY =
			"FROM Ticket t WHERE t.owner =: manager or t.state =:state or t.approver =:approver "
			+ "ORDER BY t.urgency ASC,t.desiredResolutionDate DESC, t.id ASC";
	private static final String SELECT_ALL_BY_ENGINEER_STATE_OR_ASSIGNER_QUERY =
			"FROM Ticket t Where t.state =:state or t.assignee =:engineer "
			+ "ORDER BY t.urgency ASC, t.desiredResolutionDate DESC ,t.id ASC";

	private static final String COUNT_ALL_TICKET_BY_EMPLOYEE =
			"SELECT count(*) from Ticket t WHERE t.owner =:owner";
	private static final String COUNT_ALL_TICKET_BY_MANAGER =
			"SELECT count(*) from Ticket t WHERE t.owner =: manager or t.state =:state or t.approver =:approver";
	private static final String COUNT_ALL_TICKET_BY_ENGINEER =
			"SELECT count(*) from Ticket t WHERE t.state =:state or t.assignee =:engineer";

	private static final String OWNER = "owner";
	private static final String MANAGER = "manager";
	private static final String STATE = "state";
	private static final String APPROVER = "approver";
	private static final String ENGINEER = "engineer";

	public TicketRepositoryImpl(SessionFactory sessionFactory) {
		super(sessionFactory, Ticket.class);
	}

	@Override
	public Optional<Ticket> getById(Long id) {
		return super.getById(id);
	}

	@Override
	public List<Ticket> getAllByEmployee(User owner, Integer firstResult, Integer maxResult) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_ALL_BY_EMPLOYEE_QUERY, Ticket.class).setFirstResult(firstResult)
				.setMaxResults(maxResult);

		query.setParameter(OWNER, owner);

		return query.list();
	}

	@Override
	public List<Ticket> getAllByEmployee(User owner) {
		final Query<Ticket> query = sessionFactory.getCurrentSession().createQuery(SELECT_ALL_BY_EMPLOYEE_QUERY,
				Ticket.class);

		query.setParameter(OWNER, owner);

		return query.list();
	}

	@Override
	public List<Ticket> getAllByManager(User manager, Integer firstResult, Integer maxResult) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_ALL_BY_OWNER_MANAGER_OR_STATE_OR_APPROVER_QUERY, Ticket.class)
				.setFirstResult(firstResult).setMaxResults(maxResult);

		query.setParameter(MANAGER, manager);
		query.setParameter(STATE, State.New);
		query.setParameter(APPROVER, manager);

		return query.list();
	}

	@Override
	public List<Ticket> getAllByManager(User manager) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_ALL_BY_OWNER_MANAGER_OR_STATE_OR_APPROVER_QUERY, Ticket.class);

		query.setParameter(MANAGER, manager);
		query.setParameter(STATE, State.New);
		query.setParameter(APPROVER, manager);

		return query.list();
	}

	@Override
	public List<Ticket> getAllByEngineer(User engineer, Integer firstResult, Integer maxResult) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_ALL_BY_ENGINEER_STATE_OR_ASSIGNER_QUERY, Ticket.class).setFirstResult(firstResult)
				.setMaxResults(maxResult);

		query.setParameter(STATE, State.Approved);
		query.setParameter(ENGINEER, engineer);

		return query.list();
	}

	@Override
	public List<Ticket> getAllByEngineer(User engineer) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_ALL_BY_ENGINEER_STATE_OR_ASSIGNER_QUERY, Ticket.class);

		query.setParameter(STATE, State.Approved);
		query.setParameter(ENGINEER, engineer);

		return query.list();
	}

	@Override
	public Long getCountAllByEmployee(User owner) {
		final Query<Long> query = sessionFactory.getCurrentSession().createQuery(COUNT_ALL_TICKET_BY_EMPLOYEE, Long.class);

		query.setParameter(OWNER, owner);

		return query.uniqueResult();
	}

	@Override
	public Long getCountAllByManager(User manager) {
		final Query<Long> query = sessionFactory.getCurrentSession().createQuery(COUNT_ALL_TICKET_BY_MANAGER, Long.class);

		query.setParameter(MANAGER, manager);
		query.setParameter(STATE, State.New);
		query.setParameter(APPROVER, manager);

		return query.uniqueResult();
	}

	@Override
	public Long getCountAllByEngineer(User engineer) {
		final Query<Long> query = sessionFactory.getCurrentSession().createQuery(COUNT_ALL_TICKET_BY_ENGINEER, Long.class);

		query.setParameter(STATE, State.Approved);
		query.setParameter(ENGINEER, engineer);

		return query.uniqueResult();
	}

	@Override
	public List<Ticket> getMyTicketByManager(User manager, Integer firstResult, Integer maxResult) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_MY_TICKET_BY_OWNER_MANAGER, Ticket.class).setFirstResult(firstResult)
				.setMaxResults(maxResult);

		query.setParameter(MANAGER, manager);
		query.setParameter(APPROVER, manager);

		return query.list();
	}

	@Override
	public List<Ticket> getMyTicketByManager(User manager) {
		final Query<Ticket> query = sessionFactory.getCurrentSession().createQuery(SELECT_MY_TICKET_BY_OWNER_MANAGER,
				Ticket.class);

		query.setParameter(MANAGER, manager);
		query.setParameter(APPROVER, manager);

		return query.list();
	}

	@Override
	public List<Ticket> getMyTicketByEngineer(User engineer, Integer firstResult, Integer maxResult) {
		final Query<Ticket> query = sessionFactory.getCurrentSession()
				.createQuery(SELECT_MY_TICKET_BY_OWNER_ENGINEER, Ticket.class).setFirstResult(firstResult)
				.setMaxResults(maxResult);

		query.setParameter(ENGINEER, engineer);

		return query.list();
	}

	@Override
	public List<Ticket> getMyTicketByEngineer(User engineer) {
		final Query<Ticket> query = sessionFactory.getCurrentSession().createQuery(SELECT_MY_TICKET_BY_OWNER_ENGINEER,
				Ticket.class);

		query.setParameter(ENGINEER, engineer);

		return query.list();
	}

	@Override
	public Long getCountMyTicketByManager(User manager) {
		final Query<Long> query = sessionFactory.getCurrentSession().createQuery(COUNT_MY_TICKET_BY_OWNER_MANAGER,
				Long.class);

		query.setParameter(MANAGER, manager);
		query.setParameter(APPROVER, manager);
		query.setParameter(STATE, State.Approved);

		return query.uniqueResult();
	}

	@Override
	public Long getCountMyTicketByEngineer(User engineer) {
		final Query<Long> query = sessionFactory.getCurrentSession().createQuery(COUNT_MY_TICKET_BY_OWNER_ENGINEER,
				Long.class);

		query.setParameter(ENGINEER, engineer);

		return query.uniqueResult();
	}
}