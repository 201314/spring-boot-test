package com.baomidou.springboot.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserRestTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void test() throws UnsupportedEncodingException, Exception {
		String json = "{\"name\":\"linzl\",\"testType\":10}";
		String responseString = mockMvc.perform(post("/user/test")// .accept(MediaType.APPLICATION_JSON_UTF8)

				.content(json).contentType(MediaType.APPLICATION_JSON))

				.andDo(print())

				.andExpect(status().isOk())

				.andReturn().getResponse().getContentAsString();

		System.out.println("response:" + responseString);
	}

}
