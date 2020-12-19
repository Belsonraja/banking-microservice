package com.oracle.payments.transaction.service;

import static com.oracle.payments.transaction.constant.TransactionConstant.ACCOUNT_ID_ISNULL;
import static com.oracle.payments.transaction.constant.TransactionConstant.ACCOUNT_VALIDATION_SUCESSS;
import static com.oracle.payments.transaction.constant.TransactionConstant.CREDIT_SUCCESS;
import static com.oracle.payments.transaction.constant.TransactionConstant.INSUFFICIENT_BALANCE;
import static com.oracle.payments.transaction.constant.TransactionConstant.INVALID_ACCOUNT;
import static com.oracle.payments.transaction.constant.TransactionConstant.INVALID_AMOUNT;
import static com.oracle.payments.transaction.constant.TransactionConstant.SUCESSS;
import static com.oracle.payments.transaction.constant.TransactionConstant.TRANSACTION_FAILED;
import static com.oracle.payments.transaction.constant.TransactionConstant.TXN_REQUEST_ISNULL;
import static com.oracle.payments.transaction.constant.TransactionConstant.UNABLE_TO_GET_ACC_BALANCE;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.oracle.payments.accounts.model.AccountsResponse;
import com.oracle.payments.transaction.constant.Status;
import com.oracle.payments.transaction.constant.TransactionConstant;
import com.oracle.payments.transaction.constant.TransactionType;
import com.oracle.payments.transaction.entity.TransactionEntity;
import com.oracle.payments.transaction.feign.FeignRestClient;
import com.oracle.payments.transaction.kafka.KafkaService;
import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;
import com.oracle.payments.transaction.repository.TransactionRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransactionServiceImpl implements TransactionService {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private KafkaService kafkaService;
	
	@Autowired
	private FeignRestClient feignClient;

	@Value(value = "${url.accounts-service.check-account}")
	private String checkAccountUrl;

	@Value(value = "${url.accounts-service.get-balance}")
	private String getAccountBalanceUrl;

	@Override
	public TransactionResponse creditAmount(Transaction transaction) {
		if (transaction != null) {

			String requestValidaion = validateRequest(transaction);
			if (!SUCESSS.equals(requestValidaion)) {
				return new TransactionResponse(HttpStatus.BAD_REQUEST, requestValidaion);
			}

			TransactionEntity transactionEntity = new TransactionEntity();
			transactionEntity.setDate(new Date());
			transactionEntity.setStatus(Status.SUCCESS.name());
			transactionEntity.setType(TransactionType.CREDIT.name());
			transactionEntity.setAmount(transaction.getAmount());
			transactionEntity.setAccountId(transaction.getAccountId());

			if (transactionRepository.save(transactionEntity).getId() != null) {

				Double currentBalance;

				try {
					currentBalance = Double.valueOf(getBalance(transaction.getAccountId()).getResponse());
				} catch (Exception e) {
					log.error("Error while checking current balance." + e);
					return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
				}
				
				double inputAmount = transaction.getAmount();
				transaction.setAmount(currentBalance + inputAmount);
				TransactionResponse kafkaResponse = kafkaProducerCall(transaction);
				
				if (TransactionConstant.FAILURE.equals(kafkaResponse.getResponse())) {
					transactionEntity.setStatus(Status.FAILURE.name());
					transactionRepository.save(transactionEntity);
					log.error("Error while sending transaction to kafka - creditAmount.");
					return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
				}

				return new TransactionResponse(HttpStatus.OK, String.format(CREDIT_SUCCESS, inputAmount,
						transaction.getAccountId(), (transaction.getAmount())));
			} else {
				log.error("Error while persisting into transaction table.");
				return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
			}
		}

		return new TransactionResponse(HttpStatus.BAD_REQUEST, TXN_REQUEST_ISNULL);
	}

	@Override
	public TransactionResponse debitAmount(Transaction transaction) {
		if (transaction != null) {

			String requestValidaion = validateRequest(transaction);
			if (!SUCESSS.equals(requestValidaion)) {
				return new TransactionResponse(HttpStatus.BAD_REQUEST, requestValidaion);
			}

			Double currentBalance;

			try {
				currentBalance = Double.valueOf(getBalance(transaction.getAccountId()).getResponse());
			} catch (Exception e) {
				return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
			}

			if (currentBalance < transaction.getAmount()) {
				return new TransactionResponse(HttpStatus.BAD_REQUEST,
						String.format(INSUFFICIENT_BALANCE, currentBalance));
			}

			TransactionEntity transactionEntity = new TransactionEntity();
			transactionEntity.setDate(new Date());
			transactionEntity.setStatus(Status.SUCCESS.name());
			transactionEntity.setType(TransactionType.DEBIT.name());
			transactionEntity.setAmount(transaction.getAmount());
			transactionEntity.setAccountId(transaction.getAccountId());

			if (transactionRepository.save(transactionEntity).getId() != null) {
				
				double inputAmount = transaction.getAmount();
				transaction.setAmount(currentBalance - inputAmount);
				TransactionResponse kafkaResponse = kafkaProducerCall(transaction);
				
				if (TransactionConstant.FAILURE.equals(kafkaResponse.getResponse())) {
					transactionEntity.setStatus(Status.FAILURE.name());
					transactionRepository.save(transactionEntity);
					log.error("Error while sending transaction to kafka - debitAmount.");
					return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
				}
				return new TransactionResponse(HttpStatus.OK, String.format(TransactionConstant.DEBIT_SUCCESS,
						inputAmount, transaction.getAccountId(), (transaction.getAmount())));
			} else {
				log.error("Error while persisting into transaction table-1");
				return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
			}
		}

		return new TransactionResponse(HttpStatus.BAD_REQUEST, TXN_REQUEST_ISNULL);
	}

	@Override
	public TransactionResponse getBalance(Integer accountId) {
		if (accountId <= 0) {
			return new TransactionResponse(HttpStatus.BAD_REQUEST, INVALID_AMOUNT);
		} else {
			return getBalanceRestCall(accountId);
		}
	}

	public TransactionResponse getBalanceRestCall(Integer accountId) {
		try {
			//ResponseEntity<AccountsResponse> result = restTemplate.postForEntity(getAccountBalanceUrl, accountId, AccountsResponse.class);
			ResponseEntity<AccountsResponse> result = feignClient.getAccountBalance(accountId);
			
			if (result.hasBody()) {
				return new TransactionResponse(result.getBody().getStatus(), result.getBody().getResponse());
			}
		} catch (Exception e) {
			log.error("Error while calling account service. " + e);
			return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNABLE_TO_GET_ACC_BALANCE);
		}

		return new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNABLE_TO_GET_ACC_BALANCE);
	}

	private String validateRequest(Transaction transaction) {
		if (transaction.getAccountId() == null) {
			log.error("AccountId is null.");
			return ACCOUNT_ID_ISNULL;
		}
		if (transaction.getAmount() <= 0) {
			log.error("Amound is less than or equal to zero. It should be possitive");
			return INVALID_AMOUNT;
		}
		if (!checkAccount(transaction.getAccountId())) {
			log.error("Invalid Account Id");
			return INVALID_ACCOUNT;
		}
		return SUCESSS;
	}

	public boolean checkAccount(Integer accountId) {
		try {
			//ResponseEntity<AccountsResponse> result = restTemplate.postForEntity(checkAccountUrl, accountId, AccountsResponse.class);
			ResponseEntity<AccountsResponse> result = feignClient.checkAccountById(accountId);
			
			if (result.hasBody()) {
				return ACCOUNT_VALIDATION_SUCESSS.equals(result.getBody().getResponse());
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}
	
	public TransactionResponse kafkaProducerCall(Transaction transaction) {
		TransactionResponse kafkaResponse = kafkaService.send(transaction);
		return kafkaResponse;
	}

}
