package com.commercetools.pim.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Jacob
 *
 */
@Component
public class ImporterKafkaProducer {
	@Value("${commercetools.topic}")
    private String commerceToolstopic;
	
	private final KafkaTemplate<String, String> kafkaTemplate;

	Logger logger = LoggerFactory.getLogger(ImporterKafkaProducer.class);
	
	public ImporterKafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
	
	/**
	 * The request publishing method
	 * @param message
	 */
	public void send(String message) {
		logger.debug("Publishing message: {}",message );
        this.kafkaTemplate.send(commerceToolstopic, message);
    }

	/**
	 * @param commerceToolstopic the commerceToolstopic to set
	 */
	public void setCommerceToolstopic(String commerceToolstopic) {
		this.commerceToolstopic = commerceToolstopic;
	}
}
