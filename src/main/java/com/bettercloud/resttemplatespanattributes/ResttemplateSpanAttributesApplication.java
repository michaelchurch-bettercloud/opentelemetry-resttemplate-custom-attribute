package com.bettercloud.resttemplatespanattributes;

import ch.qos.logback.core.net.server.Client;
import io.opentelemetry.api.trace.Span;
import java.io.IOException;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RestTemplateRequestCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ResttemplateSpanAttributesApplication {

	@Bean
	public TracingInterceptor tracingInterceptor() {
		return new TracingInterceptor();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, TracingInterceptor tracingInterceptor) {
		return restTemplateBuilder
				.additionalRequestCustomizers(tracingInterceptor)
//				.additionalInterceptors(tracingInterceptor)
				.build();

	}

	private static class TracingInterceptor implements RestTemplateRequestCustomizer<ClientHttpRequest>, ClientHttpRequestInterceptor {


		@Override
		public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
				throws IOException {
			Span.current().setAttribute("hello", "world");
			return execution.execute(request, body);
		}



		@Override
		public void customize(ClientHttpRequest request) {
			Span.current().setAttribute("hello", "world");
		}
	}

	@RestController
	class Controller {
		record Response(Map<String, String> args) {}

		private final RestTemplate restTemplate;
		Controller(RestTemplate restTemplate,
				TracingInterceptor tracingInterceptor) {
			this.restTemplate = restTemplate;
		}


		@GetMapping
		public Response get() {
			return restTemplate.getForObject("https://postman-echo.com/get?value=hello", Response.class);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(ResttemplateSpanAttributesApplication.class, args);
	}
}
