package com.skechers.loyalty.users.config;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.model.GetParametersByPathResult;
import com.amazonaws.services.simplesystemsmanagement.model.Parameter;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class PropertyInitializerTest {
	@InjectMocks
	PropertyInitializer initializer;
	
	@Mock
	AWSSimpleSystemsManagement ssmClient;

	@Test
	void initializerTest() {
		ConfigurableApplicationContext applicationContext = Mockito.mock(ConfigurableApplicationContext.class);
		MockEnvironment env = new MockEnvironment();
		env.setActiveProfiles("unit");
		env.setProperty("loyalty.region", "nora");
		env.setProperty("aws.paramstore.prefix", "/config");
		env.setProperty("aws.paramstore.name", "loaylty");
		env.setProperty("aws.paramstore.name", "_");
		Mockito.when(applicationContext.getEnvironment()).thenReturn(env);
		List<Parameter> params = new ArrayList<>();
		Parameter p1 = new Parameter();
		p1.setName("/config/loyaltyAPITests_unit/nora/param");
		p1.setValue("default value");
		params.add(p1);
		Parameter p2 = new Parameter();
		p1.setName("/config/loyaltyAPITests_unit/nora/param");
		p1.setValue("some value");
		params.add(p2);
		Parameter p3 = new Parameter();
		p2.setName("/config/loyaltyAPITests_unit/nora/us/__param");
		p2.setValue("some other value");
		params.add(p3);
		GetParametersByPathResult result = new GetParametersByPathResult();
		result.setParameters(params);
		Mockito.when(ssmClient.getParametersByPath(Mockito.any())).thenReturn(result);
		ConfigurableListableBeanFactory configBeanFactory = Mockito.mock(ConfigurableListableBeanFactory.class);
		Mockito.when(applicationContext.getBeanFactory())
				.thenReturn(configBeanFactory);

		initializer.initialize(applicationContext);
		assertEquals("some value", env.getProperty("param"));

	}

}
