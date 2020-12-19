package com.oracle.payments.accounts.service;

import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_ALREADY_EXISTS;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_CREATED;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_NOT_CREATED;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_VALIDATION_FAILURE;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_VALIDATION_SUCESSS;
import static com.oracle.payments.accounts.constants.AccountsConstant.NO_ACCOUNT_NAME_PROVIDED;

import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.oracle.payments.accounts.constants.AccountsConstant;
import com.oracle.payments.accounts.constants.Status;
import com.oracle.payments.accounts.entity.AccountEntity;
import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;
import com.oracle.payments.accounts.repository.AccountsRepository;
import com.oracle.payments.accounts.util.PasswordUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountsServiceImpl implements AccountsService {

	@Autowired
	AccountsRepository accountsRepository;

	@Override
	public boolean checkAccount(String accountName) {
		return accountsRepository.findByName(accountName.toUpperCase()) != null;
	}

	@Override
	public AccountsResponse createAccount(AccountsRequest account) {

		if (account == null || account.getName() == null) {
			log.error("Error while checking request - null check - 1. " + NO_ACCOUNT_NAME_PROVIDED);
			return new AccountsResponse(HttpStatus.BAD_REQUEST, NO_ACCOUNT_NAME_PROVIDED);
		}

		if (checkAccount(account.getName())) {
			log.error("Duplicate account check. " + ACCOUNT_ALREADY_EXISTS);
			return new AccountsResponse(HttpStatus.BAD_REQUEST, ACCOUNT_ALREADY_EXISTS);
		} else {

			if (account.getName() != null && StringUtils.isNotEmpty(account.getName().trim())) {
				AccountEntity accountEntity = new AccountEntity();
				accountEntity.setStatus(Status.ACTIVE.name());
				accountEntity.setUpdateddate(new Date());
				accountEntity.setName(account.getName().toUpperCase());
				accountEntity.setAccountBalance(0);
				try {
					accountEntity.setPassword(PasswordUtil.generateHash(account.getPassword()));
				} catch (NoSuchAlgorithmException e) {
					log.error("Error while processing password. " + ACCOUNT_NOT_CREATED+" .Exception is : "+e);
					return new AccountsResponse(HttpStatus.INTERNAL_SERVER_ERROR, ACCOUNT_NOT_CREATED);
				}
				accountEntity.setEmail(account.getEmail());

				Integer accountId = accountsRepository.save(accountEntity).getId();

				if (accountId != null) {
					return new AccountsResponse(HttpStatus.CREATED,
							String.format(ACCOUNT_CREATED, accountId, account.getName().toUpperCase()));
				} else {
					log.error("Error while persisting in to accounts table. " + ACCOUNT_NOT_CREATED);
					return new AccountsResponse(HttpStatus.INTERNAL_SERVER_ERROR, ACCOUNT_NOT_CREATED);
				}
			} else {
				log.error("Error while checking request - null check - 2. " + NO_ACCOUNT_NAME_PROVIDED);
				return new AccountsResponse(HttpStatus.BAD_REQUEST, NO_ACCOUNT_NAME_PROVIDED);
			}
		}
	}

	@Override
	public AccountsResponse validateAccount(AccountsRequest account) {
		log.debug("account : "+account.getPassword()+"-"+account.getName());
		AccountEntity accountEntity = accountsRepository.findByNameAndPassword(account.getName().toUpperCase(), account.getPassword());
		if(accountEntity!=null) {
			return new AccountsResponse(HttpStatus.OK, ACCOUNT_VALIDATION_SUCESSS);
		}
		return new AccountsResponse(HttpStatus.BAD_REQUEST, ACCOUNT_VALIDATION_FAILURE);
	}

	@Override
	public AccountsResponse checkAccountById(Integer accountId) {
		return new AccountsResponse(HttpStatus.OK, accountsRepository.findById(accountId).isPresent() ? ACCOUNT_VALIDATION_SUCESSS : ACCOUNT_VALIDATION_FAILURE);
	}

	@Override
	public AccountsResponse getAccountBalance(Integer accountId) {
		Optional<AccountEntity> accountEntity = accountsRepository.findById(accountId);
		if(accountEntity.isPresent()) {
			double accountBalance = accountEntity.get().getAccountBalance();
			return new AccountsResponse(HttpStatus.OK, Double.toString(accountBalance));
		}
		return new AccountsResponse(HttpStatus.BAD_REQUEST, AccountsConstant.INVALID_ACCOUNT);
	}

}
