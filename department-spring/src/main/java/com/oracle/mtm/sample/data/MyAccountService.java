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
package com.oracle.mtm.sample.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oracle.mtm.sample.entity.Account;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

/**
 * Service that connects to the accounts database and provides methods to interact with the account
 */
@Slf4j
//@Component
//@RequestScope
@Service("MyAccountService")
public class MyAccountService implements IAccountService {
	@Autowired
	private AccountMapper accountMapper;

	@Override
	public Account accountDetails(String accountId) {
		return accountMapper.getAccountById(accountId);
	}

	@Override
	public boolean withdraw(String accountId, double amount) {
		return accountMapper.withdraw(accountId, amount) > 0;
	}

	@Override
	@Transactional
	public boolean deposit(String accountId, double amount) {
		try {
			int affectedRows = accountMapper.deposit(accountId, amount);
			if (affectedRows > 0) {
				log.info("deposit Response save history : \n");
				accountMapper.saveTransactionHistory(accountId, "deposit", amount);
				log.info("deposit Response commit : \n");
				return true;
			} else {
				log.info("deposit Response rollback : \n");
				return false;
			}
		} catch (Exception e) {
			log.error("Error during deposit", e);
			throw e;
		}
	}

	@Override
	public double getBalance(String accountId) {
		Double balance = accountMapper.getBalance(accountId);
		if (balance == null) {
			throw new IllegalArgumentException("Account not found");
		}
		return balance;
	}
}