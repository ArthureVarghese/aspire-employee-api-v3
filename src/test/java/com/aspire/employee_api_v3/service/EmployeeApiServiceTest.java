package com.aspire.employee_api_v3.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.repository.EmployeeJpaRepository;
import com.aspire.employee_api_v3.view.EmployeeResponse;
import com.aspire.employee_api_v3.view.GenericResponse;

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
    private Employee mockEmployee1;
    private Employee mockEmployee2;
    private Employee mockEmployee3;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployee=new Employee(1,"Stephen","IND-ASP-ML-SALES","IND-ASP-ML",0,"Manager");
        mockEmployee1=new Employee(2,"Carlos","IND-ASP-ML-SALES","IND-ASP-ML",1,"Associate");
        mockEmployee2=new Employee(3,"Harry","IND-ASP-ML-DELIVERY","IND-ASP-ML",0,"Manager");
        mockEmployee3=new Employee(4,"Ginny","IND-ASP-ML-DELIVERY","IND-ASP-ML",3,"Associate");

        
    }

    
    @Test
    public void testGetEmployeeDetails_WithLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeRepo.findByNameStartingWith("S", PageRequest.of(0, 25))).thenReturn(employees);
        
        ResponseEntity<EmployeeResponse> response = mainService.getEmployeeDetails("S",0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees()).isEqualTo(employees);

    }

    @Test
    public void testGetEmployeeDetails_NoLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeRepo.findAll( PageRequest.of(0, 25))).thenReturn(new PageImpl<>(employees));
        
        ResponseEntity<EmployeeResponse> response = mainService.getEmployeeDetails(null,0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees()).isEqualTo(employees);

    }

    @Test
    public void testGetEmployeeDetails_InvalidLetter() throws Exception {
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


    @Test
    public void testUpdateEmployeeManager_ValidData() {
        when(employeeRepo.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepo.findById(3)).thenReturn(Optional.of(mockEmployee2));

        ResponseEntity<GenericResponse> response=mainService.updateEmployeeManager(2,3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Carlos's manager details has been updated");

    }

    @Test
    public void testUpdateEmployeeManager_InValidEmployeeId() {
        when(employeeRepo.findById(2)).thenReturn(Optional.empty());

        ResponseEntity<GenericResponse> response=mainService.updateEmployeeManager(2,3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("No employee Found");

    }

    @Test
    public void testUpdateEmployeeManager_EmployeeISAManager() {
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        ResponseEntity<GenericResponse> response=mainService.updateEmployeeManager(1,3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Manager id of a manager can't be changed");

    }

    @Test
    public void testUpdateEmployeeManager_CurrentManagerSameAsNewManager() {
        when(employeeRepo.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        
        ResponseEntity<GenericResponse> response=mainService.updateEmployeeManager(2,1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Current Manager same as New manager");

    }

    @Test
    public void testUpdateEmployeeManager_InvalidManagerId() {
        when(employeeRepo.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(employeeRepo.findById(3)).thenReturn(Optional.empty());
        
        
        ResponseEntity<GenericResponse> response=mainService.updateEmployeeManager(2,3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("No Manager Found");

    }

    @Test
    public void testUpdateEmployeeManager_ManagerIdDoesNotBelongToManager() {
        when(employeeRepo.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeRepo.findById(1)).thenReturn(Optional.of(mockEmployee1));
        when(employeeRepo.findById(4)).thenReturn(Optional.of(mockEmployee3));
        
        
        ResponseEntity<GenericResponse> response=mainService.updateEmployeeManager(2,4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Provided manager id doesn't belong to a manager");

    }


}
