package com.oracle.mtm.sample;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.XAConnection;

import io.helidon.microprofile.server.Server;
import oracle.ucp.jdbc.PoolDataSourceFactory;
import oracle.ucp.jdbc.PoolXADataSource;

/**
 * com.oracle.mtm.sample
 * <p>
 * Main
 * test connection
 *
 * @author 	kubel
 * @version
 * @since 	2024. 8. 13.
 *
 * <pre>
 * [ history of modify ]
 *      수정일        수정자           수정내용
 *  ----------    -----------    ---------------------------
 *  2024. 8. 13.     kubel 		  created
 * </pre>
 *
 */
public class Main {
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		try {

			PoolXADataSource pds = PoolDataSourceFactory.getPoolXADataSource();
			pds.setConnectionFactoryClassName("oracle.jdbc.xa.client.OracleXADataSource");
			pds.setURL("jdbc:oracle:thin:@//172.30.1.17:1521/TESTDB");
			pds.setUser("testuser");
			pds.setPassword("new1234!");

			XAConnection xaConn = pds.getXAConnection();
			Connection conn = xaConn.getConnection();
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM accounts where account_id='account1'");

			System.out.println("let's see accounts ");
			while(rs.next()){
				String accountId = rs.getString("ACCOUNT_ID");
				double balance = rs.getDouble("AMOUNT");
				System.out.println(" ID = " + accountId + " balance " + balance);
			}


			Server server = Server.create();
			server.start();

			long endTime = System.currentTimeMillis();
			long startupTime = endTime - startTime;

			System.out.println("Helidon MP server started in " + startupTime + " ms at: http://localhost:" + server.port());

			System.out.println("begin of helidon ");

			//			HelidonConfiguration config = new HelidonConfiguration();
			//			config.initialiseXaDataSource();

		}catch( Exception e) {
			e.printStackTrace();
		}
	}
}
