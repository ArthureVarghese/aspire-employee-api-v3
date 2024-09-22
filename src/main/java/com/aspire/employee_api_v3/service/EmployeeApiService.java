package com.aspire.employee_api_v3.service;

import java.util.List;

import com.aspire.employee_api_v3.model.Account;
import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.repository.AccountJpaRepository;
import com.aspire.employee_api_v3.repository.EmployeeJpaRepository;
import com.aspire.employee_api_v3.view.EmployeeResponse;
import com.aspire.employee_api_v3.view.GenericResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import com.aspire.employee_api_v3.model.Stream;
import com.aspire.employee_api_v3.repository.StreamJpaRepository;
import com.aspire.employee_api_v3.view.StreamList;


@Service
public class EmployeeApiService {

    @Autowired
    private EmployeeJpaRepository employeeRepo;

    @Autowired
    StreamJpaRepository streamJpaRepository;

    @Autowired
    AccountJpaRepository accountJpaRepository;

    public ResponseEntity<EmployeeResponse> getEmployeeDetails(String letter, Integer page) throws IllegalArgumentException {

        Pageable pageRequest = PageRequest.of(page, 25);
        Page<Employee> employees;
        EmployeeResponse response;
        if (letter == null) {
            employees = employeeRepo.findAll(pageRequest);
            response = new EmployeeResponse(employees.getContent());
        }
        else if(letter.length()!=1)
        {
            throw new IllegalArgumentException("Invalid letter length");
        }
        else {
            List<Employee> employee = employeeRepo.findByNameStartingWith(letter,pageRequest);
            response = new EmployeeResponse(employee);
            if(employee.isEmpty())
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
        
    }


    public StreamList getAllStreams(int pageNumber) {

        int PAGE_SIZE = 25;
        Page<Stream> streams = streamJpaRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        return new StreamList(streams.stream().toList());
    }


    public ResponseEntity<GenericResponse> updateEmployeeManager(Integer employeeId, Integer managerId) {

        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No employee Found"));

        if(employeeId==managerId)
            return new ResponseEntity<>(new GenericResponse("Employee id and manager id can't be same"), HttpStatus.BAD_REQUEST);

        if(employee.getManagerId()==0)
            return new ResponseEntity<>(new GenericResponse("Manager id of a manager can't be changed"),HttpStatus.BAD_REQUEST);

        Employee currentManager=employeeRepo.findById(employee.getManagerId())
            .orElseThrow(() -> new EntityNotFoundException("No Current Manager Found"));

        if(currentManager.getId()==managerId)
            return new ResponseEntity<>(new GenericResponse("Current Manager same as New manager"),HttpStatus.CONFLICT);

        Employee newManager=employeeRepo.findById(managerId)
            .orElseThrow(() -> new EntityNotFoundException("No Manager Found"));
        
        if(newManager.getManagerId()!=0){
            return new ResponseEntity<>(new GenericResponse("Provided manager id doesn't belong to a manager"),HttpStatus.BAD_REQUEST);
        }

        employee.setManagerId(managerId);
        employee.setStream(newManager.getStream());
        employee.setAccount(newManager.getAccount());
        employeeRepo.save(employee);
        
        return new ResponseEntity<>(new GenericResponse(employee.getName()+"'s manager details has been updated"),HttpStatus.OK);
        
    }


    public ResponseEntity<GenericResponse> updateEmployeeAccountName(Integer employeeId, String accountName,
            String streamId) {

        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No employee Found"));        
            
        Account account=accountJpaRepository.findByName(accountName)
            .orElseThrow(() -> new EntityNotFoundException("Account Name doesn't exist"));
        
        if(employee.getAccount().getId()==account.getId())
            return new ResponseEntity<>(new GenericResponse("Employee belongs to the given account"),HttpStatus.CONFLICT);

        Stream stream=streamJpaRepository.findById(streamId)
            .orElseThrow(() -> new EntityNotFoundException("Stream doesn't exist"));
        
        if(!stream.getAccount().getId().equalsIgnoreCase(account.getId()))
            return new ResponseEntity<>(new GenericResponse("Account and stream doesn't match"),HttpStatus.CONFLICT);
        
        Employee streamManager=employeeRepo.findByStreamAndManagerId(stream,0);
        if(employee.getManagerId()==0){

            if(streamManager!=null)
                return new ResponseEntity<>(new GenericResponse("Manager already exists for the given stream"),HttpStatus.BAD_REQUEST);

            List<Employee> employeesManagedByManager=employeeRepo.findByManagerId(employeeId);

            if(!employeesManagedByManager.isEmpty())
                return new ResponseEntity<>(new GenericResponse("Account name of a manager with subbordinates can't be updated"),HttpStatus.BAD_REQUEST);
            
        }

        employee.setAccount(account);
        employee.setStream(stream);
        if(employee.getManagerId()!=0){
            employee.setManagerId(streamManager.getId());
        }
        employeeRepo.save(employee);
            
        return new ResponseEntity<>(new GenericResponse(employee.getName()+"'s account details has been updated"),HttpStatus.OK);
    }

}
