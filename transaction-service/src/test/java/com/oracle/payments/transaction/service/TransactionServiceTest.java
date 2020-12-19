package com.oracle.payments.transaction.service;

import static com.oracle.payments.transaction.constant.TransactionConstant.ACCOUNT_ID_ISNULL;
import static com.oracle.payments.transaction.constant.TransactionConstant.CREDIT_SUCCESS;
import static com.oracle.payments.transaction.constant.TransactionConstant.DEBIT_SUCCESS;
import static com.oracle.payments.transaction.constant.TransactionConstant.INVALID_ACCOUNT;
import static com.oracle.payments.transaction.constant.TransactionConstant.INVALID_AMOUNT;
import static com.oracle.payments.transaction.constant.TransactionConstant.TRANSACTION_FAILED;
import static com.oracle.payments.transaction.constant.TransactionConstant.TXN_REQUEST_ISNULL;
import static com.oracle.payments.transaction.constant.TransactionConstant.UNABLE_TO_GET_ACC_BALANCE;
import static org.mockito.Mockito.doReturn;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import com.oracle.payments.transaction.entity.TransactionEntity;
import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;
import com.oracle.payments.transaction.repository.TransactionRepository;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceTest {

	@InjectMocks
	@Spy
	private TransactionServiceImpl transactionService;
	
	@Mock
	private TransactionRepository transactionRepository;
	
	@Test
	public void creditAmountNullRequest() {
		Transaction transaction = null;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, TXN_REQUEST_ISNULL);
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void creditAmountNullAccountId() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(null);
		transaction.setAmount(1000);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, ACCOUNT_ID_ISNULL);
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void creditAmountInvalidAmount() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(0);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, INVALID_AMOUNT);
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void creditAmountInvalidAccount() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST,INVALID_ACCOUNT);
		doReturn(false).when(transactionService).checkAccount(1);
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void creditAmountTxnTablePersistanceFailed() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setId(null);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
		doReturn(true).when(transactionService).checkAccount(1);
		doReturn(transactionEntity).when(transactionRepository).save(Mockito.any());
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void creditAmountAccountEntityAccountIdNull() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setId(1);
		transactionEntity.setAccountId(1);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
		doReturn(true).when(transactionService).checkAccount(1);
		doReturn(transactionEntity).when(transactionRepository).save(Mockito.any());
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void creditAmountSuccess() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setId(1);
		transactionEntity.setAccountId(1);
		TransactionResponse getBalResponse = new TransactionResponse(HttpStatus.OK,"1000");
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.OK,
				String.format(CREDIT_SUCCESS, transaction.getAmount(),
						transaction.getAccountId(), (1000 + transaction.getAmount())));
		doReturn(true).when(transactionService).checkAccount(1);
		doReturn(transactionEntity).when(transactionRepository).save(Mockito.any());
		doReturn(expectedResponse).when(transactionService).kafkaProducerCall(Mockito.any());
		doReturn(getBalResponse).when(transactionService).getBalance(Mockito.any());
		TransactionResponse serviceResponse = transactionService.creditAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void getBalanceAccountIdInvalid() {
		Integer accountId = 0;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, INVALID_AMOUNT);
		TransactionResponse serviceResponse = transactionService.getBalance(accountId);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void getBalanceAccountBalanceInvalid() {
		Integer accountId = 1;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.OK, "0");
		doReturn(expectedResponse).when(transactionService).getBalanceRestCall(Mockito.any());
		TransactionResponse serviceResponse = transactionService.getBalance(accountId);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void getBalanceAccountBalanceRetrivalFailed() {
		Integer accountId = 1;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, UNABLE_TO_GET_ACC_BALANCE);
		TransactionResponse serviceResponse = transactionService.getBalance(accountId);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void getBalanceAccountBalanceAvailable() {
		Integer accountId = 1;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.OK, String.valueOf(10000));
		doReturn(expectedResponse).when(transactionService).getBalanceRestCall(Mockito.any());
		TransactionResponse serviceResponse = transactionService.getBalance(accountId);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountNullRequest() {
		Transaction transaction = null;
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, TXN_REQUEST_ISNULL);
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountNullAccountId() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(null);
		transaction.setAmount(1000);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, ACCOUNT_ID_ISNULL);
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountInvalidAmount() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(0);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST, INVALID_AMOUNT);
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountInvalidAccount() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.BAD_REQUEST,INVALID_ACCOUNT);
		doReturn(false).when(transactionService).checkAccount(1);
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountTxnTablePersistanceFailed() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setId(null);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
		doReturn(true).when(transactionService).checkAccount(1);
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountAccountEntityAccountIdNull() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setId(1);
		transactionEntity.setAccountId(1);
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TRANSACTION_FAILED);
		doReturn(true).when(transactionService).checkAccount(1);
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void debitAmountSuccess() {
		Transaction transaction = new Transaction();
		transaction.setAccountId(1);
		transaction.setAmount(1000);
		TransactionEntity transactionEntity = new TransactionEntity();
		transactionEntity.setId(1);
		transactionEntity.setAccountId(1);
		TransactionResponse getBalResponse = new TransactionResponse(HttpStatus.OK,"1000");
		TransactionResponse expectedResponse = new TransactionResponse(HttpStatus.OK,
				String.format(DEBIT_SUCCESS, transaction.getAmount(),
						transaction.getAccountId(), (1000 - transaction.getAmount())));
		doReturn(true).when(transactionService).checkAccount(1);
		doReturn(transactionEntity).when(transactionRepository).save(Mockito.any());
		doReturn(getBalResponse).when(transactionService).getBalance(Mockito.any());
		doReturn(expectedResponse).when(transactionService).kafkaProducerCall(Mockito.any());
		TransactionResponse serviceResponse = transactionService.debitAmount(transaction);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
}
