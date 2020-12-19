package com.oracle.payments.transaction.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.oracle.payments.transaction.constant.TransactionConstant;
import com.oracle.payments.transaction.model.Transaction;
import com.oracle.payments.transaction.model.TransactionResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {

	@Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplate;
	
	private static final String TOPIC = "oracle_payments";
	
	@Override
	public TransactionResponse send(Transaction message) {
		log.debug("Inside kafka send : "+message.toString());
		try {
			kafkaTemplate.send(TOPIC, message);
		}catch (Exception e) {
			log.error("Error while sending message to kafka. "+e);
			new TransactionResponse(HttpStatus.INTERNAL_SERVER_ERROR, TransactionConstant.FAILURE);
		}
		
		return new TransactionResponse(HttpStatus.OK, TransactionConstant.SUCESSS);
	}
}
