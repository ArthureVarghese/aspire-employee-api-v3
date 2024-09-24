package com.aspire.employee_api_v3.service;

import com.aspire.employee_api_v3.exceptions.CustomException;
import com.aspire.employee_api_v3.model.Account;
import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.model.Stream;
import com.aspire.employee_api_v3.repository.AccountJpaRepository;
import com.aspire.employee_api_v3.repository.EmployeeJpaRepository;
import com.aspire.employee_api_v3.repository.StreamJpaRepository;
import com.aspire.employee_api_v3.view.EmployeeResponse;
import com.aspire.employee_api_v3.view.GenericResponse;
import com.aspire.employee_api_v3.view.StreamList;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;


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
    private Account mockAccount;
    private Stream mockStream;
    private Employee streamManager;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockEmployee=new Employee();
        mockEmployee1=new Employee();
        mockAccount=new Account();
        mockAccount.setId("IND-ASP-ML");
        mockStream=new Stream();
        mockStream.setAccountId("IND-ASP-ML");
    }

    
    @Test
    public void testGetEmployeeDetails_WithLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeJpaRepository.findByNameStartingWith("S", PageRequest.of(0, 25))).thenReturn(employees);
        
        EmployeeResponse response = employeeApiService.getEmployeeDetails("S",0);

        assertThat(response).isNotNull();
        assertThat(response.getEmployees().size()).isEqualTo(1);  
        assertThat(response.getEmployees().get(0).getId()).isEqualTo(mockEmployee.getId());
        
    }

    @Test
    public void testGetEmployeeDetails_NoLetter() throws Exception {
        List<Employee> employees = Collections.singletonList(mockEmployee);
        when(employeeJpaRepository.findAll( PageRequest.of(0, 25))).thenReturn(new PageImpl<>(employees));
        
        EmployeeResponse response = employeeApiService.getEmployeeDetails(null,0);
        
        assertThat(response).isNotNull();
        assertThat(response.getEmployees().size()).isEqualTo(1);  
        assertThat(response.getEmployees().get(0).getId()).isEqualTo(mockEmployee.getId());

    }

    @Test
    public void testGetEmployeeDetails_InvalidLetter() throws Exception {
        
        when(employeeJpaRepository.findByNameStartingWith("K", PageRequest.of(0, 25))).thenReturn(Collections.emptyList());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.getEmployeeDetails("K",0);
        });        
        assertThat(exception.getMessage()).isEqualTo("No employee with given credentials");

    }


    @Test
    public void testUpdateEmployeeManager_ValidData() {

        mockEmployee.setId(1);
        mockEmployee1.setManagerId(1);
        mockEmployee1.setName("Carlos");
        mockEmployee.setManagerId(0);
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(employeeJpaRepository.findById(3)).thenReturn(Optional.of(mockEmployee));

        GenericResponse response=employeeApiService.updateEmployeeManager(2,3);

        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Carlos's manager details has been updated");

    }

    @Test
    public void testUpdateEmployeeManager_InValidEmployeeId() {

        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeApiService.updateEmployeeManager(2,3);
        });        
        assertThat(exception.getMessage()).isEqualTo("No employee Found");

    }

    @Test
    public void testUpdateEmployeeManager_SameEmployeeIdAndManagerId() {
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeManager(1,1);;
        });
        
        assertThat(exception.getMessage()).isEqualTo("Employee id and manager id can't be same");

    }


    @Test
    public void testUpdateEmployeeManager_EmployeeISAManager() {
        mockEmployee.setManagerId(0);
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeManager(1,3);;
        });
        
        assertThat(exception.getMessage()).isEqualTo("Manager id of a manager can't be changed");

    }

    @Test
    public void testUpdateEmployeeManager_CurrentManagerSameAsNewManager() {
        mockEmployee.setId(1);
        mockEmployee1.setManagerId(1);
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeManager(2,1);;
        });
        
        assertThat(exception.getMessage()).isEqualTo("Current Manager same as New manager");

    }

    @Test
    public void testUpdateEmployeeManager_InvalidManagerId(){

        mockEmployee.setId(1);
        mockEmployee1.setManagerId(1);
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

        mockEmployee1.setId(2);
        mockEmployee1.setManagerId(1);
        mockEmployee.setManagerId(2);
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee1));
        when(employeeJpaRepository.findById(4)).thenReturn(Optional.of(mockEmployee));
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeManager(2,4);;
        });
        
        assertThat(exception.getMessage()).isEqualTo("Provided manager id doesn't belong to a manager");

    }
    @Test
    public void testUpdateEmployeeAccountName_ValidEmployeeData(){

        mockEmployee1.setManagerId(1);
        mockEmployee1.setName("Carlos");
        mockEmployee1.setAccountId("IND-ASP-AI");
        streamManager= new Employee();
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(streamManager); 

        GenericResponse response=employeeApiService.updateEmployeeAccountName(2,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Carlos's account details has been updated");
        
    }

    @Test
    public void testUpdateEmployeeAccountName_ValidManagerData(){

        mockEmployee.setName("Stephen");
        mockEmployee.setManagerId(0);
        mockEmployee.setAccountId("IND-ASP-AI");
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByManagerId(1)).thenReturn(Collections.emptyList()); 
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(null);

        GenericResponse response=employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");
        
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("Stephen's account details has been updated");

    
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

        streamManager= new Employee();
        streamManager.setAccountId("IND-ASP-ML");
        when(employeeJpaRepository.findById(5)).thenReturn(Optional.of(streamManager));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeAccountName(5,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");;
        }); 

        assertThat(exception.getMessage()).isEqualTo("Employee belongs to the given account");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_InvalidStream(){

        mockEmployee.setAccountId("IND-ASP-AI");
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

        mockEmployee.setAccountId("IND-ASP-AI");
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        mockStream.setAccountId("IND-ASP-SAAS-SALES");
        when(streamJpaRepository.findById("IND-ASP-SAAS-SALES")).thenReturn(Optional.of(mockStream));
        //("IND-ASP-SAAS-SALES","Aspire Software Service - Sales",new Account("IND-ASP-SAAS","Aspire Software Service")
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-SAAS-SALES");;
        });   

        assertThat(exception.getMessage()).isEqualTo("Account and stream doesn't match");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_ManagerWithSubordinates(){

        mockEmployee.setAccountId("IND-ASP-AI");
        mockEmployee.setManagerId(0);
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.existsByManagerId(1)).thenReturn(true); 
        
        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");;
        }); 

        assertThat(exception.getMessage()).isEqualTo("Account name of a manager with subbordinates can't be updated");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_StreamAlreadyHasAManager(){

        mockEmployee.setAccountId("IND-ASP-AI");
        mockEmployee.setManagerId(0);
        streamManager= new Employee();
        when(employeeJpaRepository.findById(1)).thenReturn(Optional.of(mockEmployee));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.existsByManagerId(1)).thenReturn(false); 
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(streamManager);
       

        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeAccountName(1,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");;
        }); 

        assertThat(exception.getMessage()).isEqualTo("Manager already exists for the given stream");

    
    }

    @Test
    public void testUpdateEmployeeAccountName_AddingEmployeeToAStreamWithNoManager(){

        mockEmployee1.setManagerId(1);
        mockEmployee1.setAccountId("IND-ASP-AI");
        when(employeeJpaRepository.findById(2)).thenReturn(Optional.of(mockEmployee1));
        when(accountJpaRepository.findByName("Aspire Machine Learning")).thenReturn(Optional.of(mockAccount));
        when(streamJpaRepository.findById("IND-ASP-ML-DELIVERY")).thenReturn(Optional.of(mockStream));
        when(employeeJpaRepository.findByManagerId(1)).thenReturn(Collections.emptyList()); 
        when(employeeJpaRepository.findByStreamAndManagerId(mockStream, 0)).thenReturn(null);
       

        CustomException exception = assertThrows(CustomException.class, () -> {
            employeeApiService.updateEmployeeAccountName(2,"Aspire Machine Learning","IND-ASP-ML-DELIVERY");;
        }); 

        assertThat(exception.getMessage()).isEqualTo("Can't add employee to a stream with no manager");

    
    }

    @Test
    public void testGetStreams() {

        Page<Stream> page = new PageImpl<>(List.of(mockStream), PageRequest.of(0, 25), 1);

        when(streamJpaRepository.findAll(PageRequest.of(0, 25)))
                .thenReturn(page);
        StreamList streamList = employeeApiService.getAllStreams(0);
        assertThat(streamList).isNotNull();
        assertThat(streamList.getStreams().size()).isEqualTo(1);
    }


    @Test
    void changeDesignationWithoutEmployeeId() {
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.empty());
        Exception ex = assertThrows(EntityNotFoundException.class,
                () -> employeeApiService.changeDesignation(0,"","",0));

        assertTrue(ex.getMessage().contains("Found"));
    }

    @Test
    void changeDesignationWithInvalidDesignation() {
        Employee emp = new Employee();
        emp.setDesignation("associate");
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp));
        Exception ex = assertThrows(CustomException.class,
                () -> employeeApiService.changeDesignation(0,"unknown_designation","",0));
        assertTrue(ex.getMessage().contains("No Such Designation Found"));
    }
    @Test
    void changeDesignationWithSameDesignation() {
        Employee emp = new Employee();
        emp.setDesignation("manager");
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp));
        Exception ex = assertThrows(CustomException.class,
                () -> employeeApiService.changeDesignation(0,"manager","",0));
        assertTrue(ex.getMessage().contains("Same"));
    }

    @Test
    void changeDesignationToManagerWithoutStreamId() {
        Employee emp = new Employee();
        emp.setDesignation("associate");
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp));
        Exception ex = assertThrows(EntityNotFoundException.class,
                () -> employeeApiService.changeDesignation(0,"manager","",0));
        assertTrue(ex.getMessage().contains("No Such"));
    }

    @Test
    void changeDesignationToManagerWithManagerAlreadyExists() {
        Employee emp = new Employee();
        emp.setDesignation("associate");
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp));
        when(streamJpaRepository.findById(any())).thenReturn(Optional.of(new Stream()));
        when(employeeJpaRepository.findByStreamAndManagerId (any(),any())).thenReturn(emp);

        Exception ex = assertThrows(CustomException.class,
                () -> employeeApiService.changeDesignation(0,"manager","",0));
        assertTrue(ex.getMessage().contains("Already"));
    }

    @Test
    void changeDesignationToManager() {
        Employee emp = new Employee();
        emp.setDesignation("associate");
        Stream stream = new Stream();
        stream.setAccountId("ACC_ID");
        stream.setAccount(new Account());
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp));
        when(streamJpaRepository.findById(any())).thenReturn(Optional.of(stream));
        when(employeeJpaRepository.findByStreamAndManagerId (any(),any())).thenReturn(null);
        when(employeeJpaRepository.save(any())).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            assertEquals("MANAGER", e.getDesignation());
            assertEquals("STR_ID", e.getStreamId());
            assertEquals("ACC_ID", e.getAccountId());
            return null;
        });
        employeeApiService.changeDesignation(0,"manager","STR_ID",0);
    }

    @Test
    void changeDesignationToAssociateWithoutEmployeeIdAndManagerIdSame() {
        Employee emp = new Employee();
        emp.setDesignation("manager");
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp));

        Exception ex = assertThrows(CustomException.class,
                () -> employeeApiService.changeDesignation(1,"associate","",1));
        assertTrue(ex.getMessage().contains("Same"));


    }

    @Test
    void changeDesignationToAssociateWithoutManager() {
        Employee emp = new Employee();
        emp.setDesignation("manager");
        when(employeeJpaRepository.findById(anyInt()))
                .thenReturn(Optional.of(emp))
                .thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class,
                () -> employeeApiService.changeDesignation(1,"associate","",5));
        assertTrue(ex.getMessage().contains("No Such"));
    }

    @Test
    void changeDesignationToAssociateWithManagerHavingEmployees() {
        Employee emp = new Employee();
        emp.setDesignation("manager");
        when(employeeJpaRepository.findById(anyInt()))
                .thenReturn(Optional.of(emp));
        when(employeeJpaRepository.existsByManagerId(anyInt())).thenReturn(true);

        Exception ex = assertThrows(CustomException.class,
                () -> employeeApiService.changeDesignation(1,"associate","",5));
        assertTrue(ex.getMessage().contains("Cannot change designation"));

    }

    @Test
    void changeDesignationToAssociate(){
        Employee emp = new Employee();
        Stream stream = new Stream();
        stream.setId("STR_ID");
        Employee manager = new Employee();
        manager.setStreamId("STR_ID");
        manager.setStream(new Stream());
        manager.setAccountId("ACC_ID");
        manager.setAccount(new Account());

        emp.setDesignation("manager");
        when(employeeJpaRepository.findById(anyInt())).thenReturn(Optional.of(emp)).thenReturn(Optional.of(manager));
        when(employeeJpaRepository.existsByManagerId(anyInt())).thenReturn(false);
        when(employeeJpaRepository.save(any())).thenAnswer(invocation -> {
            Employee e = invocation.getArgument(0);
            assertEquals("ASSOCIATE", e.getDesignation());
            assertEquals("STR_ID", e.getStreamId());
            assertEquals("ACC_ID", e.getAccountId());
            return null;
        });
        employeeApiService.changeDesignation(1,"associate","",0);
    }
}
