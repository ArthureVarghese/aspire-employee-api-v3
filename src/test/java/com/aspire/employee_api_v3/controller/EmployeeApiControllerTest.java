package com.aspire.employee_api_v3.controller;

import com.aspire.employee_api_v3.model.Account;
import com.aspire.employee_api_v3.model.Stream;
import com.aspire.employee_api_v3.service.EmployeeApiService;
import com.aspire.employee_api_v3.view.StreamList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest (EmployeeApiController.class)
@ExtendWith (MockitoExtension.class)
@AutoConfigureMockMvc
class EmployeeApiControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    EmployeeApiService employeeApiService;

    @Test
    void getStreamsWithNoParams() {
        StreamList streamList = new StreamList(List.of(new Stream("ID", "NAME", new Account())));
        when(employeeApiService.getAllStreams(any(Integer.class))).thenReturn(streamList);

        try {
            mvc.perform(MockMvcRequestBuilders.get("/api/v3/streams")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.streams", hasSize(1)));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    @Test
    void getStreamWithWrongPagination() {

        try {
            mvc.perform(MockMvcRequestBuilders.get("/api/v3/streams")
                    .queryParam("page-number", "-1")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Invalid Value for Page Number"));
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

}