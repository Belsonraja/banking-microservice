package com.oracle.payments.accounts.kafka;

import static com.oracle.payments.accounts.constants.AccountsConstant.ACCOUNT_NOT_CREATED;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.oracle.payments.accounts.entity.AccountEntity;
import com.oracle.payments.accounts.repository.AccountsRepository;
import com.oracle.payments.transaction.model.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

	@Autowired
	AccountsRepository accountsRepository;

	@KafkaListener(topics = "oracle_payments", groupId = "oracle_payemets_group_1", containerFactory = "userKafkaListenerFactory")
	@Override
	public void consume(Transaction message) {
		if (message != null && message.getAccountId() != null) {
			log.debug("Inside kafka consume : " + message.toString());
			log.info("consumed : " + message.toString());
			Optional<AccountEntity> accountEntityOptional = accountsRepository.findById(message.getAccountId());
			if (accountEntityOptional.isPresent()) {
				AccountEntity accountEntity = accountEntityOptional.get();
				accountEntity.setUpdateddate(new Date());
				accountEntity.setAccountBalance(message.getAmount());
				Integer accountId = accountsRepository.save(accountEntity).getId();
				if (accountId == null) {
					log.error("Error while persisting in to accounts table. " + ACCOUNT_NOT_CREATED);

				}
			}
		}

	}

}
