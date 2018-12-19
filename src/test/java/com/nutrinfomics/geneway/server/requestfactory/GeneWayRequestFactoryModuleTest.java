package com.nutrinfomics.geneway.server.requestfactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.security.SecureRandom;

import javax.inject.Inject;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.google.inject.servlet.RequestScoped;
import com.google.inject.testing.fieldbinder.Bind;
import com.google.inject.testing.fieldbinder.BoundFieldModule;
import com.google.inject.util.Modules;

public class GeneWayRequestFactoryModuleTest {

	@Bind
	@Mock
	private SecureRandom mockSecureRandom;

	@Inject
	private Injector injector;

	class TestGeneWayRequestFactoryModule extends GeneWayRequestFactoryModule {
		@Override
		protected void configure() {
			// TODO: Figure out a way to deal with
			// No scope is bound to com.google.inject.servlet.RequestScoped
			bindScope(RequestScoped.class, Scopes.SINGLETON);
		}
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);

		doAnswer(invocation -> {
			Object[] args = invocation.getArguments();
			byte[] bytes = ((byte[]) args[0]);
			for (int i = 0; i < bytes.length; ++i) {
				bytes[i] = 1;
			}
			return null;
		}).when(mockSecureRandom).nextBytes(any());

		injector = Guice.createInjector(
				Modules.override(new GeneWayRequestFactoryModule()).with(new TestGeneWayRequestFactoryModule()),
				BoundFieldModule.of(this));

		injector.injectMembers(this);
	}

	@Test
	public void provideCode_AsExpected() {
		String code = injector.getInstance(Key.get(String.class, Names.named("code")));
		assertEquals(6, code.length());
		assertEquals("81040g", code);
	}

	// TODO: add more tests
	@Test
	public void getValidatorFactory_AsExpected() {
		ValidatorFactory validatorFactory = injector.getInstance(ValidatorFactory.class);
		assertNotNull(validatorFactory);
	}

	// TODO: add more tests
	@Test
	public void getValidator_AsExpected() {
		Validator validator = injector.getInstance(Validator.class);
		assertNotNull(validator);
	}
}
