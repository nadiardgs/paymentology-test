package com.transaction_compare;

import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.contains;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = IndexControllerTests.class)
public class IndexControllerTests 
{
	/*
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private static MockMvc mockMvc;

	private final String file = "file";
	private final String indexPath = "/index";
	private final String index = "index";
	
	private String noFile = "notAFile";
	private String blankFile = "/files/blankCsvFile.csv";
	private String clientFile = "/files/ClientMarkoffFile20140113.csv";
	
	@Test
	public void IndexController_FirstFileEmpty() throws Exception
	{
		MockMultipartFile file1 = new MockMultipartFile(
				file,
				noFile,
				MediaType.TEXT_PLAIN_VALUE,
				noFile.getBytes());
		
		MockMultipartFile file2 = new MockMultipartFile(
				file,
				clientFile,
				MediaType.TEXT_PLAIN_VALUE,
				clientFile.getBytes());
		
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
		
		mockMvc.perform(multipart(indexPath).file(file1).file(file2))
			.andExpect(view().name(index))
	    	.andExpect(model().attribute("message", contains("Please select the files.")));
	}*/
}
