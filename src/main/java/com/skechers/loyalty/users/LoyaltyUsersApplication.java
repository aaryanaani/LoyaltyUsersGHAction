package com.skechers.loyalty.users;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.skechers.loyalty.users.config.PropertyInitializer;
import com.skechers.loyalty.users.exception.APIResponseErrorHandler;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;

@SpringBootApplication
@OpenAPIDefinition(info = @io.swagger.v3.oas.annotations.info.Info(title = "Loyalty Users API", version = "v1"))
@EnableScheduling
@EnableAsync
public class LoyaltyUsersApplication {

	public static void main(String[] args) {
		configureApplication(new SpringApplicationBuilder()).run(args);
	}
	
	
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return configureApplication(builder);
    }


    private static SpringApplicationBuilder configureApplication(SpringApplicationBuilder builder) {
        return builder.sources(LoyaltyUsersApplication.class).initializers(new PropertyInitializer());
    }

	@Bean
	public RestTemplate getRestTemplate() {
		RestTemplate restClient = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
		restClient.setErrorHandler(new APIResponseErrorHandler());
		return restClient;
	}

	@Bean
	public QueueMessagingTemplate getMessageTemplate(AmazonSQSAsync amazonSQS) {
		return new QueueMessagingTemplate(amazonSQS);
	}

	@Bean
	@Primary
	public AmazonSQSAsync amazonSQSClient(AWSCredentialsProvider credentialsProvider) {
		return AmazonSQSAsyncClientBuilder.standard().withCredentials(credentialsProvider).build();
	}

	@Bean
	public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(
			AsyncListenableTaskExecutor simpleAsyncTaskExecutor) {

		SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
		factory.setAutoStartup(true);
		factory.setTaskExecutor(simpleAsyncTaskExecutor);
		factory.setWaitTimeOut(20);
		factory.setMaxNumberOfMessages(10);

		return factory;
	}

	@Bean
	public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor() {
		SimpleAsyncTaskExecutor simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
		simpleAsyncTaskExecutor.setConcurrencyLimit(50);
		return simpleAsyncTaskExecutor;
	}
	
    @Bean
    public OpenAPI springUsersOpenAPI() {
        return new OpenAPI();
    }
    
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter
          = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }
}
