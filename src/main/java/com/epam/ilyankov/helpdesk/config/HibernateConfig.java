package com.epam.ilyankov.helpdesk.config;

import static org.hibernate.cfg.AvailableSettings.CACHE_REGION_FACTORY;
import static org.hibernate.cfg.AvailableSettings.DIALECT;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_AUTO;
import static org.hibernate.cfg.AvailableSettings.HBM2DDL_IMPORT_FILES;
import static org.hibernate.cfg.AvailableSettings.SHOW_SQL;
import static org.hibernate.cfg.AvailableSettings.USE_SECOND_LEVEL_CACHE;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

@Configuration
@PropertySource("classpath:application.properties")
public class HibernateConfig {

	private static final String SCAN_TO = "com.epam.ilyankov.helpDesk.domain";

	@Value("${jdbc.driverClassName}")
	private String driverClass;

	@Value("${db.url}")
	private String url;

	@Value("${db.login}")
	private String login;

	@Value("${db.password}")
	private String password;

	@Value("${hibernate.dialect}")
	private String dialect;

	@Value("${hibernate.show_sql}")
	private String showSql;

	@Value("${hibernate.hbm2ddl.auto}")
	private String hbm2ddlAuto;

	@Value("${hibernate.cache.region.factory_class}")
	private String cacheFactory;

	@Value("${hibernate.cache.use_second_level_cache}")
	private String userCacheSecondLevel;

	@Value("${hibernate.hbm2ddl.import_files}")
	private String autoImport;

	@Bean
	public DataSource dataSource() {
		BasicDataSource dataSource = new BasicDataSource();

		dataSource.setDriverClassName(driverClass);
		dataSource.setUrl(url);
		dataSource.setUsername(login);
		dataSource.setPassword(password);

		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean getSessionFactory() {

		LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
		factoryBean.setPackagesToScan(SCAN_TO);
		factoryBean.setHibernateProperties(hibernateProperties());
		factoryBean.setDataSource(dataSource());

		return factoryBean;
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();

		transactionManager.setSessionFactory(getSessionFactory().getObject());

		return transactionManager;
	}

	private Properties hibernateProperties() {
		Properties properties = new Properties();

		properties.setProperty(DIALECT, dialect);
		properties.setProperty(HBM2DDL_AUTO, hbm2ddlAuto);
		properties.setProperty(HBM2DDL_IMPORT_FILES,autoImport);
		properties.setProperty(SHOW_SQL, showSql);
		properties.setProperty(CACHE_REGION_FACTORY, cacheFactory);
		properties.setProperty(USE_SECOND_LEVEL_CACHE, userCacheSecondLevel);

		return properties;
	}
}
