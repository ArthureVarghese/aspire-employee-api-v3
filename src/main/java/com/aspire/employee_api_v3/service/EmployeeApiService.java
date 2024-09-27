package com.aspire.employee_api_v3.service;

import java.util.List;

import com.aspire.employee_api_v3.cache.annotation.CacheDelete;
import com.aspire.employee_api_v3.cache.annotation.CacheUpdate;
import com.aspire.employee_api_v3.cache.annotation.Cached;
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
import org.springframework.transaction.annotation.Transactional;


@Service
public class EmployeeApiService {

    @Autowired
    private EmployeeJpaRepository employeeRepo;

    @Autowired
    StreamJpaRepository streamJpaRepository;

    @Autowired
    AccountJpaRepository accountJpaRepository;

    @Cached
    public EmployeeResponse getEmployeeDetails(String letter, Integer page) throws IllegalArgumentException {

        Pageable pageRequest = PageRequest.of(page, 25);

        if (letter == null) {
            Page<Employee> employees = employeeRepo.findAll(pageRequest);
            return new EmployeeResponse(employees.getContent());
        }

        List<Employee> employees = employeeRepo.findByNameStartingWith(letter,pageRequest);
        return new EmployeeResponse(employees);

    }

    @Cached
    public StreamList getAllStreams(int pageNumber) {

        int PAGE_SIZE = 25;
        Page<Stream> streams = streamJpaRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        return new StreamList(streams.stream().toList());
    }

    @CacheDelete
    @Transactional
    public GenericResponse updateEmployeeManager(Integer employeeId, Integer managerId) {

        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No employee Found"));

        if(employeeId.equals(managerId))
            throw new CustomException("Employee id and manager id can't be same");

        if(employee.getManagerId()==0)
            throw new CustomException("Manager id of a manager can't be changed");

        Employee currentManager=employeeRepo.findById(employee.getManagerId())
            .orElseThrow(() -> new EntityNotFoundException("No Current Manager Found"));

        if(currentManager.getId().equals(managerId))
            throw new CustomException("Current Manager same as New manager");

        Employee newManager=employeeRepo.findById(managerId)
            .orElseThrow(() -> new EntityNotFoundException("No Manager Found"));

        if(newManager.getManagerId()!=0){
            throw new CustomException("Provided manager id doesn't belong to a manager");
        }

        employee.setManagerId(managerId);
        employee.setStream(newManager.getStream());
        employee.setStreamId(newManager.getStreamId());
        employee.setAccount(newManager.getAccount());
        employee.setAccountId(newManager.getAccountId());
        employeeRepo.save(employee);

        return new GenericResponse(employee.getName()+"'s manager details has been updated");

    }

    @CacheDelete
    @Transactional
    public GenericResponse updateEmployeeAccountName(Integer employeeId, String accountName,
            String streamId) {

        Employee employee = employeeRepo.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("No employee Found"));

        Account account=accountJpaRepository.findByName(accountName)
            .orElseThrow(() -> new EntityNotFoundException("Account Name doesn't exist"));

        if(employee.getAccountId().equals(account.getId()))
            throw new CustomException("Employee belongs to the given account");

        Stream stream=streamJpaRepository.findById(streamId)
            .orElseThrow(() -> new EntityNotFoundException("Stream doesn't exist"));

        if(!stream.getAccountId().equals(account.getId()))
            throw new CustomException("Account and stream doesn't match");

        Employee streamManager=employeeRepo.findByStreamAndManagerId(stream,0);
        if(employee.getManagerId()==0){

            if(streamManager!=null)
                throw new CustomException("Manager already exists for the given stream");

            if(employeeRepo.existsByManagerId(employeeId))
                throw new CustomException("Account name of a manager with subordinates can't be updated");

        }

        if(employee.getManagerId()!=0){
            if(streamManager==null)
                throw new CustomException("Can't add employee to a stream with no manager");
            employee.setManagerId(streamManager.getId());
        }

        employee.setAccount(account);
        employee.setAccountId(account.getId());
        employee.setStream(stream);
        employee.setStreamId(streamId);
        employeeRepo.save(employee);

        return new GenericResponse(employee.getName()+"'s account details has been updated");
    }

    @CacheDelete
    @Transactional
    public GenericResponse changeDesignation(Integer employeeId, String designation, String streamId, Integer managerId) {

        Employee employee = employeeRepo.findById(employeeId)
                            .orElseThrow(() -> new EntityNotFoundException("No Such Employee Found"));

        if(employee.getDesignation().equalsIgnoreCase(designation))
            throw new CustomException("Cannot Change to Same Designation!");

        switch (designation.toUpperCase()){
            case "MANAGER" : {

                Stream stream = streamJpaRepository.findById(streamId)
                        .orElseThrow(() -> new EntityNotFoundException("No Such Stream Found"));
                Employee manager = employeeRepo.findByStreamAndManagerId(stream,0);

                if(manager != null)
                    throw new CustomException("Manager Already exist for the given Stream");

                employee.setDesignation(designation.toUpperCase());
                employee.setStreamId(streamId);
                employee.setStream(stream);
                employee.setAccountId(stream.getAccountId());
                employee.setAccount(stream.getAccount());
                employee.setManagerId(0);
                employeeRepo.save(employee);
                break;
            }
            case "ASSOCIATE" : {
                if(employeeId.equals(managerId))
                    throw new CustomException("Employee ID and Manager ID Cannot be Same!");

                Employee manager = employeeRepo.findById(managerId)
                        .orElseThrow(() -> new EntityNotFoundException("No Such Manager Found"));
                if(employeeRepo.existsByManagerId(employeeId))
                    throw new CustomException("Cannot change designation! There are employees associated with this employee ID");

                employee.setManagerId(managerId);
                employee.setDesignation(designation.toUpperCase());
                employee.setStreamId(manager.getStreamId());
                employee.setStream(manager.getStream());
                employee.setAccountId(manager.getAccountId());
                employee.setAccount(manager.getAccount());
                employeeRepo.save(employee);
                break;
            }
            default: {
                throw new CustomException("No Such Designation Found");
            }
        }

        return new GenericResponse(employee.getName()+"'s account details has been updated");
    }
}
