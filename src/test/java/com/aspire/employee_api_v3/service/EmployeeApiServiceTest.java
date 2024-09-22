package com.aspire.employee_api_v3.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.aspire.employee_api_v3.model.Account;
import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.model.Stream;
import com.aspire.employee_api_v3.repository.AccountJpaRepository;
import com.aspire.employee_api_v3.repository.EmployeeJpaRepository;
import com.aspire.employee_api_v3.repository.StreamJpaRepository;
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

import jakarta.persistence.EntityNotFoundException;

public class EmployeeApiServiceTest {

    @InjectMocks
    private EmployeeApiService employeeApiService;

    @Mock
    private EmployeeJpaRepository employeeJpaRepository;

    @Mock
    private AccountJpaRepository accountJpaRepository;

    @Mock
    private StreamJpaRepository streamJpaRepository;

    private Employee mockEmployee;
    private Employee mockEmployee1;
    private Employee mockEmployee2;
    private Employee mockEmployee3;
    private Account mockAccount;
    private Stream mockStream;
    private Employee streamManager;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployee=new Employee(1,"Stephen",new Stream("IND-ASP-AI-DELIVERY","Aspire Artificial Intelligence - Delivery",new Account("IND-ASP-AI","Aspire Artificial Intelligence")),new Account("IND-ASP-AI","Aspire Artificial Intelligence"),0,"Manager");
        mockEmployee1=new Employee(2,"Carlos",new Stream("IND-ASP-AI-DELIVERY","Aspire Artificial Intelligence - Delivery",new Account("IND-ASP-AI","Aspire Artificial Intelligence")),new Account("IND-ASP-AI","Aspire Artificial Intelligence"),1,"Associate");
        mockEmployee2=new Employee(3,"Harry",new Stream("IND-ASP-AI-SALES","Aspire Artificial Intelligence - Sales",new Account("IND-ASP-AI","Aspire Artificial Intelligence")),new Account("IND-ASP-AI","Aspire Artificial Intelligence"),0,"Manager");
        mockEmployee3=new Employee(4,"Ginny",new Stream("IND-ASP-AI-DELIVERY","Aspire Artificial Intelligence - Delivery",new Account("IND-ASP-AI","Aspire Artificial Intelligence")),new Account("IND-ASP-AI","Aspire Artificial Intelligence"),3,"Associate");
        streamManager= new Employee(5,"Jackson Green",new Stream("IND-ASP-ML-DELIVERY","Aspire Machine Learning - Delivery",new Account("IND-ASP-ML","Aspire Machine Learning")),new Account("IND-ASP-ML","Aspire Machine Learning"), 0, "Manager");
        mockAccount=new Account("IND-ASP-ML","Aspire Machine Learning");
        mockStream=new Stream("IND-ASP-ML-DELIVERY","Aspire Machine Learning - Delivery",new Account("IND-ASP-ML","Aspire Machine Learning"));
        
    }

    
    @Test
    public void testGetEmployeeDetails_WithLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeJpaRepository.findByNameStartingWith("S", PageRequest.of(0, 25))).thenReturn(employees);
        
        ResponseEntity<EmployeeResponse> response = employeeApiService.getEmployeeDetails("S",0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees().size()).isEqualTo(1);  
        assertThat(responseBody.getEmployees().get(0).getId()).isEqualTo(mockEmployee.getId());
        assertThat(responseBody.getEmployees().get(0).getName()).isEqualTo(mockEmployee.getName());
        assertThat(responseBody.getEmployees().get(0).getStreamId()).isEqualTo(mockEmployee.getStream().getId());
        assertThat(responseBody.getEmployees().get(0).getAccountId()).isEqualTo(mockEmployee.getAccount().getId());
        assertThat(responseBody.getEmployees().get(0).getManagerId()).isEqualTo(mockEmployee.getManagerId());
        assertThat(responseBody.getEmployees().get(0).getDesignation()).isEqualTo(mockEmployee.getDesignation());

    }

    @Test
    public void testGetEmployeeDetails_NoLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeJpaRepository.findAll( PageRequest.of(0, 25))).thenReturn(new PageImpl<>(employees));
        
        ResponseEntity<EmployeeResponse> response = employeeApiService.getEmployeeDetails(null,0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees().size()).isEqualTo(1);  
        assertThat(responseBody.getEmployees().get(0).getId()).isEqualTo(mockEmployee.getId());
        assertThat(responseBody.getEmployees().get(0).getName()).isEqualTo(mockEmployee.getName());
        assertThat(responseBody.getEmployees().get(0).getStreamId()).isEqualTo(mockEmployee.getStream().getId());
        assertThat(responseBody.getEmployees().get(0).getAccountId()).isEqualTo(mockEmployee.getAccount().getId());
        assertThat(responseBody.getEmployees().get(0).getManagerId()).isEqualTo(mockEmployee.getManagerId());
        assertThat(responseBody.getEmployees().get(0).getDesignation()).isEqualTo(mockEmployee.getDesignation());

    }

    @Test
    public void testGetEmployeeDetails_InvalidLetter() throws Exception {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            employeeApiService.getEmployeeDetails("AB", 0);
        });        
        assertThat(exception.getMessage()).isEqualTo("Invalid letter length");


        List<Employee> employees=Collections.emptyList();
        when(employeeJpaRepository.findByNameStartingWith("K", PageRequest.of(0, 25))).thenReturn(employees);
        
        ResponseEntity<EmployeeResponse> response = employeeApiService.getEmployeeDetails("K",0);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        EmployeeResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.getEmployees()).isEqualTo(employees);

    }


    @Test
    public void testUpdateEmployeeManager_ValidData() {
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(employeeJpaRepository.findById(3)).thenReturn(Optional.of(mockEmployee2));

        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeManager(2,3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Carlos's manager details has been updated");

    }

    @Test
    public void testUpdateEmployeeManager_InValidEmployeeId() {

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.updateEmployeeManager(2,3);
        });        
        assertThat(exception.getMessage()).isEqualTo("No employee Found");

    }

    @Test
    public void testUpdateEmployeeManager_EmployeeISAManager() {
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeManager(1,3);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Manager id of a manager can't be changed");

    }

    @Test
    public void testUpdateEmployeeManager_CurrentManagerSameAsNewManager() {
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        
        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeManager(2,1);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Current Manager same as New manager");

    }

    @Test
    public void testUpdateEmployeeManager_InvalidManagerId() {

        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(employeeJpaRepository.findById(3)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.updateEmployeeManager(2,3);;
        });        
        assertThat(exception.getMessage()).isEqualTo("No Manager Found");
        
    }

    @Test
    public void testUpdateEmployeeManager_ManagerIdDoesNotBelongToManager() {
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(4)).thenReturn(Optional.of(mockEmployee3));
        
        
        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeManager(2,4);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Provided manager id doesn't belong to a manager");

    }
    @Test
    public void testUpdateEmployeeAccountName_ValidEmployeeData(){

        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(streamManager); 

        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeAccountName(2,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Carlos's account details has been updated");

        
    }

    @Test
    public void testUpdateEmployeeAccountName_ValidManagerData(){

        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByManagerId(1)).thenReturn(Collections.emptyList()); 
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(null);

        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Stephen's account details has been updated");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_InvalidEmployeeId() throws Exception{

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        });        
        assertThat(exception.getMessage()).isEqualTo("No employee Found");

    }

    @Test
    public void testUpdateEmployeeAccountName_InvalidAccountName(){

        
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        });        

        assertThat(exception.getMessage()).isEqualTo("Account Name doesn't exist");



    
    }

    @Test
    public void testUpdateEmployeeAccountName_EmployeeBelongsToSameAccount(){

        when(employeeJpaRepository.findById(5)).thenReturn(Optional.of(streamManager));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        
        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeAccountName(5,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Employee belongs to the given account");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_InvalidStream(){

        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");;
        });        
        assertThat(exception.getMessage()).isEqualTo("Stream doesn't exist");
    
    }

    @Test
    public void testUpdateEmployeeAccountName_AccountAndStreamDoesNotMatch(){

        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-SAAS-SALES")).thenReturn(Optional.of(new Stream("IND-ASP-SAAS-SALES","Aspire Software Service - Sales",new Account("IND-ASP-SAAS","Aspire Software Service"))));
        
        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-SAAS-SALES");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Account and stream doesn't match");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_ManagerWithSubordinates(){

        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByManagerId(1)).thenReturn(List.of(mockEmployee1)); 
        
        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Account name of a manager with subbordinates can't be updated");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_StreamAlreadyHasAManager(){

        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByManagerId(1)).thenReturn(Collections.emptyList()); 
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(streamManager);
       

        ResponseEntity<GenericResponse> response=employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        GenericResponse responseBody = response.getBody();
        assertThat(responseBody.getMessage()).isEqualTo("Manager already exists for the given stream");

    
    }



}
