package com.example.mtm.sample.common.config;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * com.example.mtm.sample.common.config
 * <p>
 * WebConfig
 * about every web configuration.
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
public class WebConfig implements WebMvcConfigurer {

	/**
	 * registry headerlogginginterceptor
	 *
	 * @param registry
	 * @author kubel
	 * 2024. 8. 11.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new HeaderLoginterceptor());
	}


}
