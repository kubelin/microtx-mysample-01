package com.oracle.mtm.sample.data;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.oracle.mtm.sample.entity.Account;

@Mapper
public interface AccountMapper {
	@Select("SELECT * FROM accounts WHERE account_id = #{accountId}")
	Account getAccountById(String accountId);

	@Update("UPDATE accounts SET amount = amount - #{amount} WHERE account_id = #{accountId}")
	int withdraw(
		@Param("accountId")
		String accountId,
		@Param("amount")
		double amount);

	@Update("UPDATE accounts SET amount = amount + #{amount} WHERE account_id = #{accountId}")
	int deposit(
		@Param("accountId")
		String accountId,
		@Param("amount")
		double amount);

	@Insert("INSERT INTO transaction_history (account_id, transaction_type, amount, transaction_date, created_at) VALUES (#{accountId}, #{transactionType}, #{amount}, SYSDATE, SYSTIMESTAMP)")
	void saveTransactionHistory(
		@Param("accountId")
		String accountId,
		@Param("transactionType")
		String transactionType,
		@Param("amount")
		double amount);

	@Select("SELECT amount FROM accounts WHERE account_id = #{accountId}")
	Double getBalance(String accountId);
}
