package com.example.mtm.sample.common.config;

import java.util.Enumeration;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * com.example.mtm.sample.common.config
 * <p>
 * HeaderLoginterceptor
 * for logging header's information
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
public class HeaderLoginterceptor implements HandlerInterceptor {

	/**
	 * check before, after http header
	 *
	 * @param request
	 * @param response
	 * @param handler
	 * @return
	 * @throws Exception
	 * @author kubel
	 * 2024. 8. 11.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		if ("POST".equalsIgnoreCase(request.getMethod())) {
			log.info("\nLogging headers for POST request to {}\n", request.getRequestURI());

			Enumeration<String> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String headerName = headerNames.nextElement();
				String headerValue = request.getHeader(headerName);
				log.info("header: {} = {}", headerName, headerValue);
			}
		}

		return true;
		//return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
