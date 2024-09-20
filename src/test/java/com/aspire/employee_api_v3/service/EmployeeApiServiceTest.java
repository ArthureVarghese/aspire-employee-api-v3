package com.aspire.employee_api_v3.service;

import java.util.Collections;
import java.util.List;

import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.repository.EmployeeJpaRepository;
import com.aspire.employee_api_v3.view.EmployeeResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class EmployeeApiServiceTest {

    @InjectMocks
    private EmployeeApiService mainService;

    @Mock
    private EmployeeJpaRepository employeeRepo;
    private Employee mockEmployee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployee=new Employee(1,"Stephen","IND-ASP-ML-SALES","IND-ASP-ML",0,"Manager");

        
    }

    
    @Test
    void testGetEmployeeDetails_WithLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeRepo.findByNameStartingWith("S", PageRequest.of(0, 25))).thenReturn(employees);
        
        ResponseEntity<EmployeeResponse> response = mainService.getEmployeeDetails("S",0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees()).isEqualTo(employees);

    }

    @Test
    void testGetEmployeeDetails_NoLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeRepo.findAll( PageRequest.of(0, 25))).thenReturn(new PageImpl<>(employees));
        
        ResponseEntity<EmployeeResponse> response = mainService.getEmployeeDetails(null,0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees()).isEqualTo(employees);

    }

    @Test
    void testGetEmployeeDetails_InvalidLetter() throws Exception {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            mainService.getEmployeeDetails("AB", 0);
        });        
        assertThat(exception.getMessage()).isEqualTo("Invalid letter length");


        List<Employee> employees=Collections.emptyList();
        when(employeeRepo.findByNameStartingWith("K", PageRequest.of(0, 25))).thenReturn(employees);
        
        ResponseEntity<EmployeeResponse> response = mainService.getEmployeeDetails("K",0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees()).isEqualTo(employees);

    }
}
