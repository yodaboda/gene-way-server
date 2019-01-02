package com.nutrinfomics.geneway.server.requestfactory;

import com.google.inject.persist.PersistFilter;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.servlet.ServletModule;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.google.web.bindery.requestfactory.server.ServiceLayerDecorator;
import com.nutrinfomics.geneway.server.alerts.Alert;
import com.nutrinfomics.geneway.server.alerts.EmailAlert;

public class GeneWayServletModule extends ServletModule {

  @Override
  protected void configureServlets() {
    install(new GeneWayJPAModule());
    filter("/*").through(PersistFilter.class);

    install(new GeneWayAlertsModule());

    install(new GeneWayRequestFactoryModule());
    serve("/gwtRequest").with(GeneWayRequestFactoryServlet.class);

    bind(Alert.class).to(EmailAlert.class).in(RequestScoped.class);
    bind(ExceptionHandler.class).to(DefaultExceptionHandler.class);
    bind(ServiceLayerDecorator.class).to(GuiceServiceLayerDecorator.class);
  }
}
