package com.oracle.payments.accounts.service;

import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_ALREADY_EXISTS;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_CREATED;
import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_NOT_CREATED;
import static com.oracle.payments.accounts.constants.AccountsConstant.NO_ACCOUNT_NAME_PROVIDED;
import static org.mockito.Mockito.doReturn;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import com.oracle.payments.accounts.constants.Status;
import com.oracle.payments.accounts.entity.AccountEntity;
import com.oracle.payments.accounts.model.AccountsRequest;
import com.oracle.payments.accounts.model.AccountsResponse;
import com.oracle.payments.accounts.repository.AccountsRepository;


@RunWith(MockitoJUnitRunner.class)
public class AccountsServiceTest {

	@InjectMocks
	private AccountsServiceImpl accountsService;
	
	@Mock
	private AccountsRepository accountsRepository;
	
	@Test
	public void createAccountNullCheckAccount() {
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.BAD_REQUEST, NO_ACCOUNT_NAME_PROVIDED);
		AccountsResponse serviceResponse = accountsService.createAccount(null);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountNullCheckName() {
		AccountsRequest account = new AccountsRequest();
		account.setName(null);
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.BAD_REQUEST, NO_ACCOUNT_NAME_PROVIDED);
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountDuplicateAccount() {
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.BAD_REQUEST, ACCOUNT_ALREADY_EXISTS);
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		AccountEntity accountEntity = new AccountEntity();
		accountEntity.setId(1);
		doReturn(accountEntity).when(accountsRepository).findByName(Mockito.anyString());
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountEmptyAccountNumber() {
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.BAD_REQUEST, NO_ACCOUNT_NAME_PROVIDED);
		AccountsRequest account = new AccountsRequest();
		account.setName(" ");
		AccountEntity accountEntity = null;
		doReturn(accountEntity).when(accountsRepository).findByName(Mockito.anyString());
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountNotCreated() {
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.INTERNAL_SERVER_ERROR, ACCOUNT_NOT_CREATED);
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		account.setPassword("123");
		AccountEntity accountEntity = null;
		AccountEntity saveResponse = new AccountEntity();
		doReturn(accountEntity).when(accountsRepository).findByName(Mockito.anyString());
		doReturn(saveResponse).when(accountsRepository).save(Mockito.any());
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountCreatedSuccess() {
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		account.setPassword("123");
		AccountEntity accountEntity = null;
		AccountEntity saveResponse = new AccountEntity();
		saveResponse.setStatus(Status.ACTIVE.name());
		saveResponse.setUpdateddate(new Date());
		saveResponse.setName(account.getName().toUpperCase());
		saveResponse.setId(1);
		AccountsResponse expectedResponse = new AccountsResponse(HttpStatus.CREATED,
				String.format(ACCOUNT_CREATED, saveResponse.getId(), account.getName().toUpperCase()));
		doReturn(accountEntity).when(accountsRepository).findByName(Mockito.anyString());
		doReturn(saveResponse).when(accountsRepository).save(Mockito.any());
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountResponseNoArgSetterCoverage() {
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		account.setPassword("124");
		AccountEntity accountEntity = null;
		AccountEntity saveResponse = new AccountEntity();
		saveResponse.setStatus(Status.ACTIVE.name());
		saveResponse.setUpdateddate(new Date());
		saveResponse.setName(account.getName().toUpperCase());
		saveResponse.setId(1);
		AccountsResponse expectedResponse = new AccountsResponse();
		expectedResponse.setStatus(HttpStatus.CREATED);
		expectedResponse.setResponse(String.format(ACCOUNT_CREATED, saveResponse.getId(), account.getName().toUpperCase()));
		doReturn(accountEntity).when(accountsRepository).findByName(Mockito.anyString());
		doReturn(saveResponse).when(accountsRepository).save(Mockito.any());
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
	
	@Test
	public void createAccountResponseAllArgCoverage() {
		AccountsRequest account = new AccountsRequest();
		account.setName("user1");
		account.setPassword("r+-454#dfdo90");
		AccountEntity accountEntity = null;
		AccountEntity saveResponse = new AccountEntity(1 ,account.getName().toUpperCase(), account.getPassword(), account.getEmail(), 0, new Date(), Status.ACTIVE.name());
		saveResponse.toString();
		AccountsResponse expectedResponse = new AccountsResponse();
		expectedResponse.setStatus(HttpStatus.CREATED);
		expectedResponse.setResponse(String.format(ACCOUNT_CREATED, saveResponse.getId(), account.getName().toUpperCase()));
		doReturn(accountEntity).when(accountsRepository).findByName(Mockito.anyString());
		doReturn(saveResponse).when(accountsRepository).save(Mockito.any());
		AccountsResponse serviceResponse = accountsService.createAccount(account);
		Assert.assertEquals(expectedResponse.getResponse(), serviceResponse.getResponse());
		Assert.assertEquals(expectedResponse.getStatus(), serviceResponse.getStatus());
	}
}
