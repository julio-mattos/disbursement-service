package com.sequraapi.disbursement.service.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sequraapi.disbursement.service.TestHelper;
import com.sequraapi.disbursement.service.controller.DisbursementController;
import com.sequraapi.disbursement.service.controller.reponse.SimpleOrderResponse;
import com.sequraapi.disbursement.service.controller.request.OrderRequest;
import com.sequraapi.disbursement.service.exception.MerchantNotFoundException;
import com.sequraapi.disbursement.service.repository.DisbursementRepository;
import com.sequraapi.disbursement.service.repository.MerchantRepository;
import com.sequraapi.disbursement.service.repository.OrderRepository;
import com.sequraapi.disbursement.service.service.DisbursementUseCase;
import com.sequraapi.disbursement.service.service.impl.FeeBetweenFiftyAndThreeHundredUseCaseImpl;
import com.sequraapi.disbursement.service.service.impl.FeeGreaterThenThreeHundredUseCaseImpl;
import com.sequraapi.disbursement.service.service.impl.FeeSmallerThenFiftyUseCaseImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DisbursementController.class)
public class DisbursementIntegrationTest {

    @MockBean
    private DisbursementUseCase disbursementUseCase;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {

        OrderRequest orderRequest = new OrderRequest("12345", BigDecimal.valueOf(100));
        SimpleOrderResponse simpleOrderResponse = new SimpleOrderResponse("1234567", "Order created!");

        when(disbursementUseCase.createOrder(any(OrderRequest.class))).thenReturn(simpleOrderResponse);

        String jsonRequest = objectMapper.writeValueAsString(orderRequest);
        String jsonResponse = objectMapper.writeValueAsString(simpleOrderResponse);

        mockMvc.perform(post("/disbursement")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(jsonResponse));

        verify(disbursementUseCase, times(1)).createOrder(any(OrderRequest.class));

    }

    @Test
    void shouldThrowMerchantErrorWhenCreateOrder() throws Exception {

        OrderRequest orderRequest = new OrderRequest("12345", BigDecimal.valueOf(100));
        when(disbursementUseCase.createOrder(any(OrderRequest.class))).thenThrow(MerchantNotFoundException.class);

        String jsonRequest = objectMapper.writeValueAsString(orderRequest);

        mockMvc.perform(post("/disbursement")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(disbursementUseCase, times(1)).createOrder(any(OrderRequest.class));

    }

    @Test
    void shouldReturnDisbursementReports() throws Exception {

        when(disbursementUseCase.listReports()).thenReturn(TestHelper.getListReports());

        String jsonResponse = objectMapper.writeValueAsString(TestHelper.getListReports());

        mockMvc.perform(get("/disbursement/reports"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));

        verify(disbursementUseCase, times(1)).listReports();
    }

}
