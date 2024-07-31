package com.oracle.mtm.sample.resource;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.oracle.mtm.sample.data.IAccountService;
import com.oracle.mtm.sample.entity.Account;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/loadtest")
@OpenAPIDefinition(info = @Info(title = "my-load-test", version = "1.0"))
public class LoadTestTransacttionController {

	private final IAccountService accountService;

	@Autowired
	public LoadTestTransacttionController(
		@Qualifier("MyAccountServiceImpl")
		IAccountService accountService) {
		this.accountService = accountService;
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Account Details", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(ref = "Account"))),
		@ApiResponse(responseCode = "404", description = "No account found for the provided account Identity"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@RequestMapping(value = "/{accountId}", method = RequestMethod.GET)
	public ResponseEntity<?> getAccountDetails(
		@PathVariable("accountId")
		String accountId) {

		log.info("loadtest started accountId is : " + accountId);

		try {
			Account account = this.accountService.accountDetails(accountId);
			if (account == null) {
				log.error("Account not found: " + accountId);
				ResponseEntity.status(HttpStatus.NOT_FOUND).body("No account found for the provided account Identity");
			}
			return ResponseEntity.ok(account);
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Amount withdrawn from the account"),
		@ApiResponse(responseCode = "422", description = "Amount must be greater than zero"),
		@ApiResponse(responseCode = "422", description = "Insufficient balance in the account"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@RequestMapping(value = "/{accountId}/withdraw", method = RequestMethod.POST)
	public ResponseEntity<?> withdraw(
		@PathVariable("accountId")
		String accountId,
		@RequestParam("amount")
		double amount) {

		log.info("loadtest started withdraw amount is : " + amount);

		if (amount == 0) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Amount must be greater than zero");
		}
		try {
			if (this.accountService.getBalance(accountId) < amount) {
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Insufficient balance in the account");
			}
			if (this.accountService.withdraw(accountId, amount)) {
				log.info(amount + " withdrawn from account: " + accountId);
				return ResponseEntity.ok("Amount withdrawn from the account");
			}
		} catch (SQLException | IllegalArgumentException e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
		return ResponseEntity.internalServerError().body("Withdraw failed");
	}

	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "Amount deposited to the account"),
		@ApiResponse(responseCode = "422", description = "Amount must be greater than zero"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error")
	})
	@RequestMapping(value = "/{accountId}/deposit", method = RequestMethod.POST)
	public ResponseEntity<?> deposit(
		@PathVariable("accountId")
		String accountId,
		@RequestParam
		double amount) {
		if (amount <= 0) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Amount must be greater than zero");
		}
		log.info("show amount " + accountId + " : " + amount);
		try {
			if (this.accountService.deposit(accountId, amount)) {
				log.info(amount + " deposited to account: " + accountId);
				return ResponseEntity.ok("Amount deposited to the account");
			}
		} catch (SQLException e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
		return ResponseEntity.internalServerError().body("Deposit failed");
	}

}
