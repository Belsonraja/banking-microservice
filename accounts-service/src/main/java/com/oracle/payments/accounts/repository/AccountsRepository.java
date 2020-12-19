package com.oracle.payments.accounts.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.oracle.payments.accounts.entity.AccountEntity;

@Repository
public interface AccountsRepository extends CrudRepository<AccountEntity, Integer>{
	public AccountEntity findByName(String accountName);
	public AccountEntity findByNameAndPassword(String accountName, String password);
}
