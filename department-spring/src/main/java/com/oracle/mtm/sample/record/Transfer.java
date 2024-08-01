package com.oracle.mtm.sample.record;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Transfer")
public record Transfer(
	@Schema(description = "The remitter's account Identity", requiredMode = Schema.RequiredMode.REQUIRED)
	String from,

	@Schema(description = "The beneficiary's account Identity", requiredMode = Schema.RequiredMode.REQUIRED)
	String to,

	@Schema(description = "The transfer amount", requiredMode = Schema.RequiredMode.REQUIRED)
	double amount) {
	// 레코드는 기본적으로 toString, equals, hashCode 메서드를 자동으로 생성합니다.
	// 따라서 toString 메서드를 별도로 오버라이드할 필요가 없습니다.

	// 만약 추가적인 생성자나 메서드가 필요하다면 여기에 정의할 수 있습니다.
	public Transfer {
		// 여기에 유효성 검사 등의 로직을 추가할 수 있습니다.
	}
}