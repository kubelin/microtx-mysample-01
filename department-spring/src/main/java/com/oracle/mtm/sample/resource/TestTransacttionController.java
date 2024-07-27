package com.oracle.mtm.sample.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.oracle.mtm.sample.data.IAccountService;
import com.oracle.mtm.sample.entity.Transfer;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/loadtest")
@OpenAPIDefinition(info = @Info(title = "my-load-test", version = "1.0"))
public class TestTransacttionController {

	@Autowired
	@Qualifier("MicroTxXaRestTemplate")
	RestTemplate restTemplate;

	@Autowired
	IAccountService accountService;

	@Value("${departmentOneEndpoint}")
	String departmentOneEndpoint;
	@Value("${departmentTwoEndpoint}")
	String departmentTwoEndpoint;

	@RequestMapping(value = "", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRED)
	public ResponseEntity<?> xaTransfer(@RequestBody
	Transfer transferInfo) throws Exception {
		boolean isWithdrawalAllowed = false;
		// withdraw account from local
		if (transferInfo.getAmount() == 0) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Amount must be greater than zero");
		}
		try {
			if (this.accountService.getBalance(transferInfo.getFrom()) < transferInfo.getAmount()) {
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Insufficient balance in the account");
			}
			if (this.accountService.withdraw(transferInfo.getFrom(), transferInfo.getAmount())) {
				log.info(transferInfo.getAmount() + " withdrawn from account: " + transferInfo.getFrom());
				isWithdrawalAllowed = true;
			}
		} catch (SQLException | IllegalArgumentException e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}

		// deposit account from target
		URI departmentUri = getDepartmentTwoTarget()
			.path("/accounts")
			.path("/" + transferInfo.getTo())
			.path("/deposit")
			.queryParam("amount", transferInfo.getAmount())
			.build()
			.toUri();

		if (isWithdrawalAllowed) {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(departmentUri, null, String.class);
			log.info("Deposit Response: \n" + responseEntity.getBody());

			if (!responseEntity.getStatusCode().is2xxSuccessful()) {
				log.error("Deposit failed: " + responseEntity.toString() + "Reason: " + responseEntity.getBody());
				throw new Exception(
					String.format("Deposit failed: %s Reason: %s ", transferInfo, responseEntity.getBody()));
			}
		}

		return ResponseEntity.ok("Transfer completed successfully");
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@Transactional(propagation = Propagation.REQUIRED)
	public ResponseEntity<?> nonXaTransfer(@RequestBody
	Transfer transferInfo) throws Exception {

		boolean isWithdrawalAllowed = false;
		// withdraw account from local
		if (transferInfo.getAmount() == 0) {
			return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Amount must be greater than zero");
		}
		try {
			if (this.accountService.getBalance(transferInfo.getFrom()) < transferInfo.getAmount()) {
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("Insufficient balance in the account");
			}
			if (this.accountService.withdraw(transferInfo.getFrom(), transferInfo.getAmount())) {
				log.info(transferInfo.getAmount() + " withdrawn from account: " + transferInfo.getFrom());
				isWithdrawalAllowed = true;
			}
		} catch (SQLException | IllegalArgumentException e) {
			log.error(e.getLocalizedMessage());
			return ResponseEntity.internalServerError().body(e.getLocalizedMessage());
		}
		// deposit account target

		if (isWithdrawalAllowed) {

		}

		return null;
	}

	/**
	 * Send an HTTP request to the service to withdraw amount from the provided account identity
	 * @param amount The amount to be withdrawn
	 * @param accountId The account Identity
	 * @return HTTP Response from the service
	 */
	private ResponseEntity<String> withdraw(double amount, String accountId) throws URISyntaxException {
		URI departmentUri = getDepartmetnOneTarget()
			.path("/accounts")
			.path("/" + accountId)
			.path("/withdraw")
			.queryParam("amount", amount)
			.build()
			.toUri();

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(departmentUri, null, String.class);
		log.info("Withdraw Response: \n" + responseEntity.getBody());
		return responseEntity;
	}

	/**
	 * Send an HTTP request to the service to deposit amount into the provided account identity
	 * @param amount The amount to be deposited
	 * @param accountId The account Identity
	 * @return HTTP Response from the service
	 */
	private ResponseEntity<String> deposit(double amount, String accountId) throws URISyntaxException {
		URI departmentUri = getDepartmentTwoTarget()
			.path("/accounts")
			.path("/" + accountId)
			.path("/deposit")
			.queryParam("amount", amount)
			.build()
			.toUri();

		ResponseEntity<String> responseEntity = restTemplate.postForEntity(departmentUri, null, String.class);
		log.info("Deposit Response: \n" + responseEntity.getBody());
		return responseEntity;
	}

	private UriComponentsBuilder getDepartmetnOneTarget() {
		return UriComponentsBuilder.fromUri(URI.create(departmentOneEndpoint));
	}

	private UriComponentsBuilder getDepartmentTwoTarget() {
		return UriComponentsBuilder.fromUri(URI.create(departmentTwoEndpoint));
	}

}
