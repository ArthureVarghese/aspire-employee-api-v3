package com.aspire.employee_api_v3.service;

import java.util.List;

import com.aspire.employee_api_v3.exceptions.CustomException;
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

    public EmployeeResponse getEmployeeDetails(String letter, Integer page) throws IllegalArgumentException {

        Pageable pageRequest = PageRequest.of(page, 25);
        
        if (letter == null) {
            Page<Employee> employees = employeeRepo.findAll(pageRequest);
            return new EmployeeResponse(employees.getContent());
        }
        
        List<Employee> employee = employeeRepo.findByNameStartingWith(letter,pageRequest);
        if(employee.isEmpty())
            throw new EntityNotFoundException("No employee with given credentials");
        return new EmployeeResponse(employee);
        
        
    }


    public StreamList getAllStreams(int pageNumber) {

        int PAGE_SIZE = 25;
        Page<Stream> streams = streamJpaRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        return new StreamList(streams.stream().toList());
    }


    public GenericResponse updateEmployeeManager(Integer employeeId, Integer managerId) {

        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No employee Found"));

        if(employeeId==managerId)
            throw new CustomException("Employee id and manager id can't be same");

        if(employee.getManagerId()==0)
            throw new CustomException("Manager id of a manager can't be changed");

        Employee currentManager=employeeRepo.findById(employee.getManagerId())
            .orElseThrow(() -> new EntityNotFoundException("No Current Manager Found"));

        if(currentManager.getId()==managerId)
            throw new CustomException("Current Manager same as New manager");

        Employee newManager=employeeRepo.findById(managerId)
            .orElseThrow(() -> new EntityNotFoundException("No Manager Found"));
        
        if(newManager.getManagerId()!=0){
            throw new CustomException("Provided manager id doesn't belong to a manager");
        }

        employee.setManagerId(managerId);
        employee.setStream(newManager.getStream());
        employee.setAccount(newManager.getAccount());
        employeeRepo.save(employee);
        
        return new GenericResponse(employee.getName()+"'s manager details has been updated");
        
    }


    public GenericResponse updateEmployeeAccountName(Integer employeeId, String accountName,
            String streamId) {

        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No employee Found"));        
            
        Account account=accountJpaRepository.findByName(accountName)
            .orElseThrow(() -> new EntityNotFoundException("Account Name doesn't exist"));
        
        if(employee.getAccount().getId().equals(account.getId()))
            throw new CustomException("Employee belongs to the given account");

        Stream stream=streamJpaRepository.findById(streamId)
            .orElseThrow(() -> new EntityNotFoundException("Stream doesn't exist"));
        
        if(!stream.getAccount().getId().equals(account.getId()))
            throw new CustomException("Account and stream doesn't match");
        
        Employee streamManager=employeeRepo.findByStreamAndManagerId(stream,0);
        if(employee.getManagerId()==0){

            if(streamManager!=null)
                throw new CustomException("Manager already exists for the given stream");

            List<Employee> employeesManagedByManager=employeeRepo.findByManagerId(employeeId);

            if(!employeesManagedByManager.isEmpty())
                throw new CustomException("Account name of a manager with subbordinates can't be updated");
            
        }

        if(employee.getManagerId()!=0){
            if(streamManager==null)
                throw new CustomException("Can't add employee to a stream with no manager");
            employee.setManagerId(streamManager.getId());
        }
        
        employee.setAccount(account);
        employee.setStream(stream);
        employeeRepo.save(employee);
            
        return new GenericResponse(employee.getName()+"'s account details has been updated");
    }

}
