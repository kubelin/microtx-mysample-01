package com.example.mtm.sample.common.config;

import java.util.Collections;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * com.example.mtm.sample.common.config
 * <p>
 * HttpConfig
 * build template for http protocol
 *
 * @author 	kubel
 * @version
 * @since 	2024. 8. 11.
 *
 * <pre>
 * [ history of modify ]
 *      수정일        수정자           수정내용
 *  ----------    -----------    ---------------------------
 *  2024. 8. 11.     kubel 		 create
 * </pre>
 *
 */
@Slf4j
@Configuration
public class HttpConfig {
	// connection option
	@Value("${rest.connection.max-per-route:10}")
	private int maxConnPerRoute;

	@Value("${rest.connection.max-total:20}")
	private int maxConnTotal;

	@Value("${rest.connection.timeout:5000}")
	private int connectionTimeout;

	@Value("${rest.response.timeout:10000}")
	private int responseTimeout;

	@Bean
	public HttpClientConnectionManager httpClientConnectionManager() {
		return PoolingHttpClientConnectionManagerBuilder.create()
			.setMaxConnPerRoute(maxConnPerRoute)
			.setMaxConnTotal(maxConnTotal)
			.build();
	}

	@Bean
	public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(
		HttpClientConnectionManager connectionManager) {

		RequestConfig requestConfig = RequestConfig.custom()
			.setResponseTimeout(Timeout.ofMilliseconds(responseTimeout))
			.setConnectionRequestTimeout(Timeout.ofMilliseconds(connectionTimeout))
			.build();

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
			.setDefaultRequestConfig(requestConfig)
			.setConnectionManager(connectionManager);

		return new HttpComponentsClientHttpRequestFactory(httpClientBuilder.build());
	}


	@Bean
	@Primary
	public RestTemplate restTemplate(HttpComponentsClientHttpRequestFactory requestFactory) {
		RestTemplate restTemplate = new RestTemplate(requestFactory);
		restTemplate.setInterceptors(Collections.singletonList(new LoggingRequestInterceptor()));
		return restTemplate;
	}

}
