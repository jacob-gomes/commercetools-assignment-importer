package com.commercetools.pim.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.kafka.core.KafkaTemplate;

import com.commercetools.pim.exception.ImporterException;
import com.commercetools.pim.kafka.ImporterKafkaProducer;


@RunWith(MockitoJUnitRunner.class)
public class ImporterServiceTest {
	@Mock
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@InjectMocks
    private ImporterService importerService;
	
	@Test
	public void testPositiveConditionForDataImport() throws IOException, ImporterException, URISyntaxException {
		ImporterKafkaProducer importerKafkaProducer = new ImporterKafkaProducer(kafkaTemplate);
		importerKafkaProducer.setCommerceToolstopic("commerceToolstopic");
		importerService.setCommercetoolsCSVLocation("./src/test/resources/csv/data_positive_condition.csv");
		importerService.setImporterKafkaProducer(importerKafkaProducer);
		
		doAnswer(new Answer<Void>() {
			 public Void answer(InvocationOnMock invocation) {
				 Object[] args = invocation.getArguments();
				 String requestMessage = (String) args[1];
				 JSONObject jsonObject = new JSONObject(requestMessage);
				 assertEquals("9d46fd7e-e915-4870-b54e-144abe34f5e4",jsonObject.getString(ImporterService.UUID));
				 assertEquals("XYZ Mobile",jsonObject.getString(ImporterService.NAME));
				 assertEquals("smart phone",jsonObject.getString(ImporterService.DESCRIPTION));
				 assertEquals("XYZ",jsonObject.getString(ImporterService.PROVIDER));
				 assertTrue(jsonObject.getBoolean(ImporterService.AVAILABLE));
				 assertEquals("PC",jsonObject.getString(ImporterService.MEASUREMENT_UNIT));
				 return null;
			 }
		}).when(kafkaTemplate).send(anyString(),anyString());
		
		importerService.executeImporterService();
	}

	@Test(expected=ImporterException.class)
	public void testForFileNotFound() throws IOException, ImporterException {
		importerService.setCommercetoolsCSVLocation("abc.csv");
		importerService.executeImporterService();
	}

}
