package com.example.mtm.sample.common.config;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * com.example.mtm.sample.common.config
 * <p>
 * LoggingRequestInterceptor
 * do something while sending request
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
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

		log.info("Request URI : {}", request.getURI());
		log.info("Request Method : {}", request.getMethod());
		log.info("Request Headers: {}", request.getHeaders());

		return execution.execute(request, body);
	}

}
