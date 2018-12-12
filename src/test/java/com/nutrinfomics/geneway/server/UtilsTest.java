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
