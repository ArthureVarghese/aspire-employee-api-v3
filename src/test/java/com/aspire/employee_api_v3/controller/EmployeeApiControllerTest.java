package com.aspire.employee_api_v3.controller;

import com.aspire.employee_api_v3.model.Account;
import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.model.Stream;
import com.aspire.employee_api_v3.service.EmployeeApiService;
import com.aspire.employee_api_v3.view.EmployeeResponse;
import com.aspire.employee_api_v3.view.GenericResponse;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;



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
        Stream stream = new Stream();
        StreamList streamList = new StreamList(List.of(stream));
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

    @Test
    void testGetEmployeeDetails_WithParam() throws Exception {

    Employee mockEmployee = new Employee(1, "Stephen", 
    new Stream("IND-ASP-AI-DELIVERY", "Aspire Artificial Intelligence - Delivery", new Account("IND-ASP-AI", "Aspire Artificial Intelligence")),
    new Account("IND-ASP-AI", "Aspire Artificial Intelligence"),
    0, "Manager");

    EmployeeResponse response = new EmployeeResponse(List.of(mockEmployee));
    
    // Mock service method
    when(employeeApiService.getEmployeeDetails(any(String.class), any(Integer.class))).thenReturn(response);

    // Perform the request and verify response
    mvc.perform(MockMvcRequestBuilders.get("/api/v3/employees")
        .param("starts-with", "S")
        .param("page", "1")
        .accept(MediaType.APPLICATION_JSON))
        .andDo(print()) 
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees").isArray())
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].name").value("Stephen"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].streamId").value("IND-ASP-AI-DELIVERY"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].accountId").value("IND-ASP-AI"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].managerId").value(0))
        .andExpect(MockMvcResultMatchers.jsonPath("$.employees[0].designation").value("Manager"));
}


    @Test
    void testGetEmployeeDetails_WithNoParam() throws Exception {

        Employee mockEmployee = new Employee(1, "Stephen", 
    new Stream("IND-ASP-AI-DELIVERY", "Aspire Artificial Intelligence - Delivery", new Account("IND-ASP-AI", "Aspire Artificial Intelligence")),
    new Account("IND-ASP-AI", "Aspire Artificial Intelligence"),
    0, "Manager");

    EmployeeResponse response = new EmployeeResponse(List.of(mockEmployee));
    
    // Mock service method
    when(employeeApiService.getEmployeeDetails(null, 1)).thenReturn(response);

    // Perform the request and verify response
    mvc.perform(MockMvcRequestBuilders.get("/api/v3/employees")
        .accept(MediaType.APPLICATION_JSON))
        .andDo(print())  // This will show the response in the console
        .andExpect(status().isOk());
           
    }


    @Test 
    void testGetEmployeeDetails_InvalidPagination() throws Exception {

        Employee mockEmployee=new Employee(1,"Stephen",new Stream("IND-ASP-AI-DELIVERY","Aspire Artificial Intelligence - Delivery",new Account("IND-ASP-AI","Aspire Artificial Intelligence")),new Account("IND-ASP-AI","Aspire Artificial Intelligence"),0,"Manager");
        EmployeeResponse response = new EmployeeResponse(List.of(mockEmployee));
        
        // Mock service method
        when(employeeApiService.getEmployeeDetails(null,-1)).thenReturn(response);

        // Perform the request and verify response
        mvc.perform(MockMvcRequestBuilders.get("/api/v3/employees")
                .param("page","-1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid Value for Page Number"));
           
    }

    @Test
    void testUpdateEmployeeManager() throws Exception {
        GenericResponse mockResponse = new GenericResponse("Manager updated successfully");

        when(employeeApiService.updateEmployeeManager(1, 2)).thenReturn(mockResponse);

        mvc.perform(MockMvcRequestBuilders.put("/api/v3/employees/manager")
                .param("employee-id", "1")
                .param("manager-id", "2")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Manager updated successfully"));
    }

    
    @Test
    void testUpdateEmployeeAccountName() throws Exception {
        GenericResponse mockResponse = new GenericResponse("Account name updated successfully");

        when(employeeApiService.updateEmployeeAccountName(1, "Aspire Artificial Intelligence","IND-ASP-AI-DELIVERY")).thenReturn(mockResponse);

        mvc.perform(MockMvcRequestBuilders.put("/api/v3/employees/account")
                .param("employee-id", "1")
                .param("account-name", "Aspire Artificial Intelligence")
                .param("stream-id","IND-ASP-AI-DELIVERY")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Account name updated successfully"));
    }


    @Test
    void updateEmployeeDesignation() {

        GenericResponse genericResponse = new GenericResponse("Steve Rogers's account details has been updated");
        when(employeeApiService.changeDesignation(anyInt(),anyString(),any(),any())).thenReturn(genericResponse);

        try {
            mvc.perform(MockMvcRequestBuilders.put("/api/v3/employees/designation")
                            .accept(MediaType.APPLICATION_JSON)
                            .queryParam("employee-id","1")
                            .queryParam("designation","associate")
                    )
                    .andExpect(status().isOk());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}