package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

import java.security.SecureRandom;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;
import com.nutrinfomics.geneway.server.RequestUtils;
import com.nutrinfomics.geneway.server.Utils;
import com.nutrinfomics.geneway.shared.testcategory.FastTest;

@Category(value = {FastTest.class})
public class GeneWayRequestFactoryModuleTest {

  @Bind @Mock private SecureRandom mockSecureRandom;
  @Mock private Utils mockUtils;
  @Mock private RequestUtils mockRequestUtils;

  private Injector injector;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    doAnswer(
            invocation -> {
              Object[] args = invocation.getArguments();
              byte[] bytes = ((byte[]) args[0]);
              for (int i = 0; i < bytes.length; ++i) {
                bytes[i] = 1;
              }
              return null;
            })
        .when(mockSecureRandom)
        .nextBytes(any());

    injector =
        Guice.createInjector(
            Modules.override(new GeneWayRequestFactoryModule())
                .with(new TestGeneWayRequestFactoryModule(mockUtils, mockRequestUtils)),
            BoundFieldModule.of(this));
  }

  @Test
  public void provideCode_AsExpected() {
    String code = injector.getInstance(Key.get(String.class, Names.named("code")));
    assertEquals(6, code.length());
    assertEquals("81040g", code);
  }

  @Test
  public void provideLocale_AsExpected() {
    Locale defaultLocale = Locale.getDefault();
    doReturn(defaultLocale).when(mockUtils).getLocale();

    Locale locale = injector.getInstance(Locale.class);
    assertEquals(defaultLocale, locale);
  }

  // TODO: Delete these tests
  //  // TODO: add more tests
  //  @Test
  //  public void getValidatorFactory_AsExpected() {
  //    ValidatorFactory validatorFactory = injector.getInstance(ValidatorFactory.class);
  //    assertNotNull(validatorFactory);
  //  }
  //
  //  // TODO: add more tests
  //  @Test
  //  public void getValidator_AsExpected() {
  //    Validator validator = injector.getInstance(Validator.class);
  //    assertNotNull(validator);
  //  }
}
