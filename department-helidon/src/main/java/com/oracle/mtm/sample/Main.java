package com.oracle.mtm.sample;

import io.helidon.microprofile.server.Server;

public class Main {
	// startup time check
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();

		Server server = Server.create();
		server.start();

		long endTime = System.currentTimeMillis();
		long startupTime = endTime - startTime;

		System.out.println("Helidon MP server started in " + startupTime + " ms at: http://localhost:" + server.port());

	}
}
