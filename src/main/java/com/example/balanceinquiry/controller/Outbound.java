package com.example.balanceinquiry.controller;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.example.balanceinquiry.model.Model;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@EnableKafka
public class Outbound {
	    private static final Logger logger = LoggerFactory.getLogger(Outbound.class);
	    private static final String TOPIC = "truist-account-balance";

	    @Autowired
	    private KafkaTemplate<Integer, String> kafkaTemplate;
	    
	    @Autowired
	    ObjectMapper objectMapper;

	    public void sendMessage(Model transactionRequest) throws JsonProcessingException {
	    	Integer key = 1;
	    	String value = objectMapper.writeValueAsString(transactionRequest);
	        logger.info(String.format("#### -> Producing message -> %s on topic : %s", transactionRequest.toString(), TOPIC));
	        
	        ProducerRecord<Integer,String> producerRecord = buildProducerRecord(key, value, TOPIC);
	        
	        ListenableFuture<SendResult<Integer,String>> listenableFuture =  kafkaTemplate.send(producerRecord);
	        
	        listenableFuture.addCallback(new ListenableFutureCallback<SendResult<Integer, String>>() {
	            @Override
	            public void onFailure(Throwable ex) {
	                handleFailure(key, value, ex);
	            }

	            @Override
	            public void onSuccess(SendResult<Integer, String> result) {
	                handleSuccess(key, value, result);
	            }
	        });
	    }
	    
	    private ProducerRecord<Integer, String> buildProducerRecord(Integer key, String value, String topic) {

	       // List<Header> recordHeaders = List.of(new RecordHeader("event-source", "scanner".getBytes()));

	        return new ProducerRecord<>(topic, null, key, value, null);
	    }
	    
	    private void handleFailure(Integer key, String value, Throwable ex) {
	    	logger.error("Error Sending the Message and the exception is {}", ex.getMessage());
	        try {
	            throw ex;
	        } catch (Throwable throwable) {
	        	logger.error("Error in OnFailure: {}", throwable.getMessage());
	        }


	    }

	    private void handleSuccess(Integer key, String value, SendResult<Integer, String> result) {
	    	logger.info("Message Sent SuccessFully for the key : {} and the value is {} , partition is {}", key, value, result.getRecordMetadata().partition());
	    }
}
