package com.oracle.payments.transaction.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.oracle.payments.transaction.entity.TransactionEntity;

@Repository
public interface TransactionRepository  extends CrudRepository<TransactionEntity, Integer>{

}
