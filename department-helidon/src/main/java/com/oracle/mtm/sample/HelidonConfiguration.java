/*
Copyright (c) 2023, Oracle and/or its affiliates. **

The Universal Permissive License (UPL), Version 1.0 **

Subject to the condition set forth below, permission is hereby granted to any person obtaining a copy of this software, associated documentation and/or data
(collectively the "Software"), free of charge and under any and all copyright rights in the Software, and any and all patent rights owned or freely licensable by each
licensor hereunder covering either the unmodified Software as contributed to or provided by such licensor, or (ii) the Larger Works (as defined below), to deal in both **
(a) the Software, and (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if one is included with the Software (each a "Larger Work" to which the
Software is contributed by such licensors), **
without restriction, including without limitation the rights to copy, create derivative works of, display, perform, and distribute the Software and make, use, sell,
offer for sale, import, export, have made, and have sold the Software and the Larger Work(s), and to sublicense the foregoing rights on either these or other terms. **

This license is subject to the following condition: The above copyright notice and either this complete permission notice or at a minimum a reference to the UPL must be
included in all copies or substantial portions of the Software. **

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package com.oracle.mtm.sample;

import java.lang.invoke.MethodHandles;
import java.sql.SQLException;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import oracle.tmm.common.TrmConfig;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

@ApplicationScoped
public class HelidonConfiguration {

	private PoolXADataSource xaDataSource;

	private PoolDataSource dataSource;
	final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Inject
	@ConfigProperty(name = "departmentDataSource.url")
	String url;
	@Inject
	@ConfigProperty(name = "departmentDataSource.user")
	String user;
	@Inject
	@ConfigProperty(name = "departmentDataSource.password")
	String password;
	@Inject
	Config config;

	private void init(@Observes
	@Initialized(ApplicationScoped.class) Object event) {
		System.out.println("\n is it started ?!!!!!!!!!! ============= \n");

		initialiseXaDataSource();
		initialiseDataSource();
	}

	/**
	 * Initializes the datasource into the TMM library that manages the lifecycle of the XA transaction
	 *
	 */
	private void initialiseXaDataSource() {
		try {
			this.xaDataSource = PoolDataSourceFactory.getPoolXADataSource();

			// 기본 설정
			this.xaDataSource.setURL(config.getValue("departmentDataSource.url", String.class));
			this.xaDataSource.setUser(config.getValue("departmentDataSource.user", String.class));
			this.xaDataSource.setPassword(config.getValue("departmentDataSource.password", String.class));
			this.xaDataSource.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");

			// 추가 설정
			this.xaDataSource.setMinPoolSize(5);
			this.xaDataSource.setMaxPoolSize(100);
			this.xaDataSource.setInitialPoolSize(5);
			this.xaDataSource.setConnectionWaitTimeout(10); // 10sec
			this.xaDataSource.setInactiveConnectionTimeout(30); // 30sec
			this.xaDataSource.setValidateConnectionOnBorrow(true);
			this.xaDataSource.setFastConnectionFailoverEnabled(true);
			this.xaDataSource.setAbandonedConnectionTimeout(40); // 40sec

			// 연결 풀 초기화
			//			logger.info("Initializing XA connection pool...");
			//				this.xaDataSource.getConnection().close();

			logger.info("XA connection pool initialized successfully.");

			TrmConfig.initXaDataSource(this.xaDataSource);
		} catch (SQLException e) {
			logger.error("Failed to initialise database");
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the datasource for non xa operation
	 */
	private void initialiseDataSource() {
		try {
			this.dataSource = PoolDataSourceFactory.getPoolDataSource();
			this.dataSource.setURL(config.getValue("departmentDataSource.url", String.class));
			this.dataSource.setUser(config.getValue("departmentDataSource.user", String.class));
			this.dataSource.setPassword(config.getValue("departmentDataSource.password", String.class));
			this.dataSource.setConnectionFactoryClassName("oracle.jdbc.pool.OracleDataSource");
			this.dataSource.setMaxPoolSize(60);

			// 추가 설정
			//			this.xaDataSource.setMinPoolSize(5);
			//			this.xaDataSource.setMaxPoolSize(20);
			//			this.xaDataSource.setInitialPoolSize(5);
			//			this.xaDataSource.setConnectionWaitTimeout(10);
			//			this.xaDataSource.setInactiveConnectionTimeout(300);
			//			this.xaDataSource.setValidateConnectionOnBorrow(true);
			//			this.xaDataSource.setFastConnectionFailoverEnabled(true);
			//			this.xaDataSource.setAbandonedConnectionTimeout(300);

		} catch (SQLException e) {
			logger.error("Failed to initialise database");
			e.printStackTrace();
		}
	}

	public PoolDataSource getDatasource() {
		return dataSource;
	}

}