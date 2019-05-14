/*
 * Copyright 2019 Firas Swidan†
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.servlet.ServletConfig;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
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
import com.google.inject.persist.PersistService;
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

  // TODO: remove ignore
  @Ignore
  @Test
  public void distpatchRequest_AsExpected() throws Exception {
    HttpServletRequest mockHttpServletRequest = mock(HttpServletRequest.class);
    HttpServletResponse mockHttpServletResponse = mock(HttpServletResponse.class);

    doReturn("/gwtRequest").when(mockHttpServletRequest).getRequestURI();
    doReturn("").when(mockHttpServletRequest).getContextPath();
    doReturn("POST").when(mockHttpServletRequest).getMethod();
    Cookie[] cookies = new Cookie[] {new Cookie("gwtLocale", "ar")};
    doReturn(cookies).when(mockHttpServletRequest).getCookies();
    doReturn("HTTP/1.1").when(mockHttpServletRequest).getProtocol();

    ServletConfig mockServletConfig = mock(ServletConfig.class);

    GeneWayRequestFactoryServlet geneWayRequestFactoryServlet =
        injector.getInstance(GeneWayRequestFactoryServlet.class);

    geneWayRequestFactoryServlet.init(mockServletConfig);
    geneWayRequestFactoryServlet.service(mockHttpServletRequest, mockHttpServletResponse);

    verify(mockHttpServletResponse, times(1)).setStatus(HttpServletResponse.SC_OK);

    //	  assertEquals(HttpServletResponse.SC_OK, httpServletResponse.gets);
    //	  GuiceFilter guiceFilter = injector.getInstance(GuiceFilter.class);

    //	  new GuiceFilter().doFilter(mockHttpServletRequest, mockHttpServletResponse,
    // PersistFilter.class);
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */