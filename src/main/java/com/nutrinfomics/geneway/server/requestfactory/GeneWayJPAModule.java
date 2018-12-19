package com.nutrinfomics.geneway.server.requestfactory;

import java.util.Locale;

import javax.inject.Named;
import javax.inject.Provider;
import javax.persistence.EntityManager;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.RequestScoped;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;

public class GeneWayJPAModule extends AbstractModule {

	@Override
	protected void configure() {
		requireBinding(HibernateUtil.class);
		requireBinding(Utils.class);
		requireBinding(RequestUtils.class);

		install(new JpaPersistModule("domainPersistence"));
	}

	@Provides
	@RequestScoped
	public @Named("dbSession") Session provideDbSession(Session clientSession, Provider<EntityManager> entityManager,
			HibernateUtil hibernateUtil) {
		return hibernateUtil.selectSession(clientSession.getSid(), entityManager);
	}

	@Provides
	@RequestScoped
	public Locale provideLocale(Utils utils) {
		return utils.getLocale();
	}

}
