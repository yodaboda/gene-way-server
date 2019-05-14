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

/*
 * †Dr Firas Swidan, PhD. frsswdn@gmail.com. firas.swidan@icloud.com.
 * https://www.linkedin.com/in/swidan
 * POBox  8125,  Nazareth  16480, Israel.
 * Public key: AAAAB3NzaC1yc2EAAAADAQABAAACAQD6Lt98LolwuA/aOcK0h91ECdeiyG3QKcUOT/CcMEPV64cpkv3jrLLGoag7YtzESZ3j7TLEd0WHZ/BZ9d+K2kRfzuuCdMMhrBwqP3YObbTbSIM6NjUNwbH403LLb3FuYApUt1EvC//w64UMm7h3fTo0vdyVuMuGnkRZuM6RRAXcODM4tni9ydd3ZQKN4inztkeH/sOoM77FStk8E2VYbljUQdY39zlRoZwUqNdKzwD3T2G00tmROlTZ6K5L8i68Zqt6s0XNS6XQvS3zXe0fI6UwuetnDrcVr1Yb8y2T8lfjMG9+9L2aKPoUOlOMMcyqM+oKVvRUOSdrzmtKOljnYC7TqzvsKrfXHvHlqHxxhPp1K7B/YWrHwCDbqp02dXdIaXkkHCIqKFNaY06HEWt4obDxppVhC8IabSb55LQVCCT7J4TFbwp6rID2+Y1L7NEvR3v3oaWSlQIZ+WSG04mwh9/7gRCt7XUoqmEXCCPoHqZXq5sWv193XA57pD5gKoX7Rf2i6UdbduNTMIhQMqcWIaPMBFwxUv/LRQCHnS+mlW2GnIHIHHGS/S46MurZ6BMvcb7fEz/NorVxvh3DbUaVTteMYcikH0y5sPmGECB1d99ENBBSEX6diI+PneFp2sOouQ6gOBWy6WAt3spGfLTOFMPo3bMV/UpktkQPpXkmfd1esQ==
 */