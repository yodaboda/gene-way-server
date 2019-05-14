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

package com.nutrinfomics.geneway.server;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Locale;
import java.util.Random;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.junit.LoggerContextRule;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class UtilsTest {

  private static final String CONFIG = "log4j-appender.xml";

  @ClassRule public static LoggerContextRule loggerContextRule = new LoggerContextRule(CONFIG);

  private ListAppender listAppender;

  private Utils utils;
  private RequestUtils mockedRequestUtils;

  @Before
  public void setupLoggingForTests() {
    mockedRequestUtils = mock(RequestUtils.class);
    utils = new Utils(mockedRequestUtils);
    listAppender = loggerContextRule.getListAppender("List").clear();
  }

  /**
   * Build random number of cookies.
   *
   * @param withGwtLocale if true one of the cookies will have name "gwtLocale"
   * @param withGwtLocaleValue if true the cookie with name with "gwtLocale" will have a legal
   *     locale string value
   * @return a random number of cookies with one that might have <code> gwtLocale </code> as its
   *     name
   */
  private Cookie[] buildCookies(boolean withGwtLocale, String gwtLocaleValue) {
    Random rand = new Random();
    int cookieNum = rand.nextInt(10);
    Cookie[] cookies = new Cookie[cookieNum + (withGwtLocale ? 1 : 0)];

    for (int i = 0; i < cookieNum; ++i) {
      Cookie mockedCookie = buildCookie("name" + i, "val" + i);
      cookies[i] = mockedCookie;
    }

    if (withGwtLocale) {
      cookies[cookieNum] = buildCookie("gwtLocale", gwtLocaleValue);
    }

    return cookies;
  }

  /**
   * builds a mock cookie with specified name and value
   *
   * @param name of the cookie
   * @param value of the cookie
   * @return a cookie with specified name and value
   */
  private Cookie buildCookie(String name, String value) {
    Cookie mockedCookie = mock(Cookie.class);
    when(mockedCookie.getName()).thenReturn(name);
    when(mockedCookie.getValue()).thenReturn(value);
    return mockedCookie;
  }

  @Test
  public void testGetLocale() {
    HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    Cookie[] cookies = buildCookies(true, "ar");
    when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);

    assertEquals(Locale.forLanguageTag("ar"), utils.getLocale());
    List<LogEvent> events = listAppender.getEvents();
    assertEquals(0, events.size());
  }

  @Test
  public void testGetLocaleNoGwtCookie() {
    HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    Cookie[] cookies = buildCookies(false, null);
    when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);

    assertEquals(Locale.ENGLISH, utils.getLocale());
    List<LogEvent> events = listAppender.getEvents();
    assertEquals(1, events.size());
    LogEvent logEvent = events.get(0);
    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals("Http Request has no gwtLocale cookie!", logEvent.getMessage().toString());
  }

  @Test
  public void testGetLocaleNullCookies() {
    HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    when(mockedHttpServletRequest.getCookies()).thenReturn(null);
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);

    assertEquals(Locale.ENGLISH, utils.getLocale());
    List<LogEvent> events = listAppender.getEvents();
    LogEvent logEvent = events.get(0);
    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals("Http Request has null cookies!", logEvent.getMessage().toString());
  }

  @Test
  public void testGetLocaleNullHttpServerRequest() {
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(null);

    assertEquals(Locale.ENGLISH, utils.getLocale());
    List<LogEvent> events = listAppender.getEvents();
    LogEvent logEvent = events.get(0);
    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals("Null Http Request!", logEvent.getMessage().toString());
  }

  @Test
  public void testGetLocaleNullGwtLocaleValue() {
    HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    Cookie[] cookies = buildCookies(true, null);
    when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);

    assertEquals(Locale.ENGLISH, utils.getLocale());

    List<LogEvent> events = listAppender.getEvents();
    LogEvent logEvent = events.get(0);
    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals("Null gwtLocale cookie value!", logEvent.getMessage().toString());
  }

  @Test
  public void testGetLocaleIllegalGwtLocaleValue() {
    HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    Cookie[] cookies = buildCookies(true, "arrrr");
    when(mockedHttpServletRequest.getCookies()).thenReturn(cookies);
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);

    assertEquals(Locale.ENGLISH, utils.getLocale());

    List<LogEvent> events = listAppender.getEvents();
    LogEvent logEvent = events.get(0);
    assertEquals(Level.FATAL, logEvent.getLevel());
    assertEquals(IllegalArgumentException.class, logEvent.getThrown().getClass());
  }

  @Test
  public void testGetIP() {
    String IP = "127.0.0.1";
    HttpServletRequest mockedHttpServletRequest = mock(HttpServletRequest.class);
    when(mockedHttpServletRequest.getRemoteAddr()).thenReturn(IP);
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(mockedHttpServletRequest);

    assertEquals(IP, utils.getIP());
  }

  @Test
  public void testGetIPNullHttpRequest() {
    when(mockedRequestUtils.getHttpServletRequest()).thenReturn(null);

    assertEquals(null, utils.getIP());
    List<LogEvent> events = listAppender.getEvents();
    LogEvent logEvent = events.get(0);
    assertEquals(Level.WARN, logEvent.getLevel());
    assertEquals("Null Http Request!", logEvent.getMessage().toString());
  }
}

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */
