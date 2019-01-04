package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.geneway.alerts.injection.AlertsModule;
import com.geneway.alerts.injection.testing.TestAlertsModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.persist.PersistFilter;
import com.google.inject.persist.PersistService;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.google.web.bindery.requestfactory.server.DefaultExceptionHandler;
import com.google.web.bindery.requestfactory.server.ExceptionHandler;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.server.data.HibernateUtil;
import com.nutrinfomics.geneway.server.domain.device.Session;
import com.nutrinfomics.geneway.server.requestfactory.request.TestGeneWayAlertsModule;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;
import com.nutrinfomics.geneway.shared.testcategory.SlowTest;

@Category(value = {SlowTest.class})
public class GeneWayServletModuleTest {

  private final String SID = "Smith Pt PF AB";

  @Mock private HibernateUtil mockHibernateUtil;
  @Mock private Utils mockUtils;
  @Mock private RequestUtils mockRequestUtils;

  @Mock private Session mockDbSession;
  @Bind @Mock private Session mockClientSession;
  
  @Bind private ExceptionHandler exceptionHandler = new DefaultExceptionHandler();

  @Inject private PersistService service;
  @Inject Provider<EntityManager> entityManagerProvider;

  private Injector injector;

  public class TestGeneWayServletModule extends GeneWayServletModule {
    @Override
    protected void configureServlets() {
      // TODO: Figure out a way to deal with
      // No scope is bound to com.google.inject.servlet.RequestScoped
      bindScope(RequestScoped.class, Scopes.SINGLETON);
    }
  }

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    injector =
        Guice.createInjector(
            Modules.override(
                    new GeneWayJPAModule(),
                    new GeneWayAlertsModule(),
                    new AlertsModule(),
                    new GeneWayRequestFactoryModule())
                .with(
                    new TestGeneWayJPAModule(mockHibernateUtil),
                    new TestGeneWayAlertsModule(),
                    new TestAlertsModule(),
                    new TestGeneWayRequestFactoryModule(mockUtils, mockRequestUtils)),
            BoundFieldModule.of(this));
    injector.injectMembers(this);
    service.start();
  }

  @Test
  public void provideDbSession_AsExpected() {
    doReturn(SID).when(mockClientSession).getSid();
    when(mockHibernateUtil.selectSession(SID)).thenReturn(mockDbSession);

    Session dbSession = injector.getInstance(Key.get(Session.class, Names.named("dbSession")));
    assertEquals(mockDbSession, dbSession);
  }

  @Test
  public void provideLocale_AsExpected() {
    Locale defaultLocale = Locale.getDefault();
    doReturn(defaultLocale).when(mockUtils).getLocale();

    Locale locale = injector.getInstance(Locale.class);
    assertEquals(defaultLocale, locale);
  }
  
  @Test
  public void distpatchRequest_AsExpected() throws Exception {
	  HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
	  HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);

	  doReturn("/gwtRequest").when(mockHttpServletRequest).getRequestURI();
	  doReturn("").when(mockHttpServletRequest).getContextPath();
	  doReturn("GET").when(mockHttpServletRequest).getMethod();
	  Cookie[] cookies = new Cookie[] {new Cookie("gwtLocale", "ar")};
	  doReturn(cookies).when(mockHttpServletRequest).getCookies();
	  doReturn("HTTP/1.1").when(mockHttpServletRequest).getProtocol();
	  
	  GeneWayRequestFactoryServlet geneWayRequestFactoryServlet = injector.getInstance(GeneWayRequestFactoryServlet.class);
	  geneWayRequestFactoryServlet.service(mockHttpServletRequest, mockHttpServletResponse);

//	  assertEquals(HttpServletResponse.SC_OK, httpServletResponse.gets);
	  //	  GuiceFilter guiceFilter = injector.getInstance(GuiceFilter.class);
	  
//	  new GuiceFilter().doFilter(mockHttpServletRequest, mockHttpServletResponse, PersistFilter.class);
  }
}
