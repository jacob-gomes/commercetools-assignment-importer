package com.commercetools.pim.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;

import javax.annotation.PostConstruct;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.commercetools.pim.exception.ImporterException;
import com.commercetools.pim.kafka.ImporterKafkaProducer;

/**
 * 
 * @author Jacob
 *
 */
@Component
public class ImporterService {

	public static final String UUID = "uuid";

	public static final String NAME = "name";

	public static final String DESCRIPTION	= "description";

	public static final String PROVIDER = "provider";

	public static final String AVAILABLE = "available";

	public static final String MEASUREMENT_UNIT = "measurementUnit";

	public static final String REQUEST_TIMESTAMP_IN_MS = "requestTimeStampInMS";

	Logger logger = LoggerFactory.getLogger(ImporterService.class);
	
	@Autowired
	private ImporterKafkaProducer importerKafkaProducer;

	@Value("${commercetools.csv.location}")
	private String commercetoolsCSVLocation;
	
	
	/**
	 * This is the method that coordinates the to functionality
	 *  1. Read csv file
	 *  2. Push the read Record to Kafka Topic 
	 * @throws IOException 
	 * @throws ImporterException 
	 * @throws Exception
	 */
	@PostConstruct
	public void executeImporterService() throws IOException, ImporterException {
		try(BufferedReader csvReader = 
				new BufferedReader(new FileReader(commercetoolsCSVLocation))) {
			String row;				
			while ((row = csvReader.readLine()) != null) {
				String[] entity = row.split(",");
				if(entity.length != 6) {
					logger.info("Exactly 6 fields required");
					continue;
				}

				if(!"UUID".equalsIgnoreCase(entity[0])) {
					JSONObject requestJSON = populateJSONObject(entity);
					importerKafkaProducer.send(requestJSON.toString());
				}
				Thread.sleep(1);// for stream lining
			}			
		}catch(Exception e) {
			logger.error("Exception occurred during transmitting data", e);
			throw new ImporterException(e.getMessage());
		}
	}


	private JSONObject populateJSONObject(String[] entity) {		

		JSONObject reqestJSON = new JSONObject();

		reqestJSON.put(UUID, entity[0]);
		reqestJSON.put(NAME, entity[1]);
		reqestJSON.put(DESCRIPTION, entity[2]);
		reqestJSON.put(PROVIDER, entity[3]);
		reqestJSON.put(AVAILABLE, Boolean.valueOf(entity[4]));
		reqestJSON.put(MEASUREMENT_UNIT, entity[5]);
		reqestJSON.put(REQUEST_TIMESTAMP_IN_MS, Calendar.getInstance().getTimeInMillis());
		
		return reqestJSON;
	}


	/**
	 * @param commercetoolsCSVLocation the commercetoolsCSVLocation to set
	 */
	public void setCommercetoolsCSVLocation(String commercetoolsCSVLocation) {
		this.commercetoolsCSVLocation = commercetoolsCSVLocation;
	}


	/**
	 * @param importerKafkaProducer the importerKafkaProducer to set
	 */
	public void setImporterKafkaProducer(ImporterKafkaProducer importerKafkaProducer) {
		this.importerKafkaProducer = importerKafkaProducer;
	}

}
