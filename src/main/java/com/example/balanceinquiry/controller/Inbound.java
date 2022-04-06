package com.example.balanceinquiry.controller;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.example.balanceinquiry.model.InsufficientAccountBalance;
import com.example.balanceinquiry.model.Model;
import com.example.balanceinquiry.service.BalanceInquiryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@EnableKafka
public class Inbound {
	
	private static final Logger logger = LoggerFactory.getLogger(Inbound.class);
	
	boolean sufficient_amt;
	private final Outbound outbound;
	private final BalanceInquiryService balanceInquiryService;
	
	public Inbound(BalanceInquiryService balanceInquiryService, Outbound outbound) {
		this.balanceInquiryService = balanceInquiryService;
		this.outbound = outbound;
	}
	
    @Autowired
    ObjectMapper objectMapper;
    
    @KafkaListener(topics = {"truist-account"})
    public void deposit(ConsumerRecord<Integer,String> consumerRecord) throws InsufficientAccountBalance, JsonMappingException, JsonProcessingException  {
    	
    	Model model=objectMapper.readValue(consumerRecord.value(), Model.class);
		try {
			sufficient_amt = balanceInquiryService.doInquiry(model);
			logger.info(String.format("#### -> Consumed message -> %s", consumerRecord.toString()));
			this.outbound.sendMessage(model);
		}catch (InsufficientAccountBalance e) {
			throw e;
		}
    }
}
