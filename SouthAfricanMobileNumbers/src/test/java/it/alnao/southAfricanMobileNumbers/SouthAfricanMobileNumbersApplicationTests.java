package it.alnao.southAfricanMobileNumbers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.alnao.southAfricanMobileNumbers.entities.SouthAfricanMobileNumbersEntity;
import it.alnao.southAfricanMobileNumbers.repository.SouthAfricanMobileNumbersRepository;
import it.alnao.southAfricanMobileNumbers.service.SouthAfricanMobileNumbersService;


@SpringBootTest
class SouthAfricanMobileNumbersApplicationTests {

	 private MockMvc mockMvc;
		
		@Autowired
		private WebApplicationContext wac;
		
		@Autowired
		SouthAfricanMobileNumbersRepository repository;
		
	String jsonData =  
			"[" + 
			"    {\"idNumber\":\"1\",\"phoneNumber\":\"27720374211\",\"type\":\"\",\"loadDate\":\"\"}\n" + 
			"," + 
			"    {\"idNumber\":\"2\",\"phoneNumber\":\"37720374211\",\"type\":\"\",\"loadDate\":\"\"}\n" + 
			"," + 
			"    {\"idNumber\":\"3\",\"phoneNumber\":\"720374211\",\"type\":\"\",\"loadDate\":\"\"}\n" + 
			"," + 
			"    {\"idNumber\":\"3\",\"phoneNumber\":\"27777720374211\",\"type\":\"\",\"loadDate\":\"\"}\n" + 
			"]";
	String jsonDataResponse =  "[" + 
			"    {\"id\": \"6044f51d9bf8d839c89ec1e7\",\"idNumber\": \"1\",\"phoneNumber\": \"27720374211\",\"type\": \"OK\",\"loadDate\": \"\"},\n" + 
			"    {\"id\": \"6044f51d9bf8d839c89ec1e8\",\"idNumber\": \"2\",\"phoneNumber\": \"37720374211\",\"type\": \"KO\",\"loadDate\": \"\"},\n" + 
			"    {\"id\": \"6044f51d9bf8d839c89ec1e9\",\"idNumber\": \"3\",\"phoneNumber\": \"27720374211\",\"type\": \"OK INT PREFIX\",\"loadDate\": \"\"},\n" + 
			"    {\"id\": \"6044f51d9bf8d839c89ec1ea\",\"idNumber\": \"3\",\"phoneNumber\": \"27777720374211\",\"type\": \"KO\",\"loadDate\": \"\"}\n" + 
			"]";
	
	@Test
	void test() throws Exception {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/southAfricanMobileNumbersController/uploadFile")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonData)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andDo(print())
        		.andReturn();

		String content = result.getResponse().getContentAsString();
		assertThat(jsonDataResponse.equalsIgnoreCase(content));
	}
	


}
