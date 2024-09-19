package com.aspire.employee_api_v3.service;

import java.util.Collections;
import java.util.List;

import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.repository.EmployeeJpaRepository;
import com.aspire.employee_api_v3.view.EmployeeResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aspire.employee_api_v3.model.Stream;
import com.aspire.employee_api_v3.repository.StreamJpaRepository;
import com.aspire.employee_api_v3.view.StreamList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
public class EmployeeApiService {

    @Autowired
    private EmployeeJpaRepository employeeRepo;

    @Autowired
    StreamJpaRepository streamJpaRepository;

    public ResponseEntity<EmployeeResponse> getEmployeeDetails(String letter, Integer page) {

        List<Employee> employee=Collections.emptyList();
        try{
            Pageable pageRequest = PageRequest.of(page-1, 25);
            Page<Employee> employees;
            EmployeeResponse response;
            if (letter == null) {
                employees = employeeRepo.findAll(pageRequest);
                response = new EmployeeResponse(employees.getContent(),null);
            }
            else if(letter.length()!=1)
            {
                throw new Exception("A single value expected");
            }
            else {
                employee = employeeRepo.findByNameStartingWith(letter,pageRequest);
                response = new EmployeeResponse(employee,null);
                if(employee.isEmpty())
                    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch(Exception e){
            return new ResponseEntity<>(new EmployeeResponse(employee,e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    public StreamList getAllStreams(int pageNumber) {

        int PAGE_SIZE = 25;
        Page<Stream> streams = streamJpaRepository.findAll(PageRequest.of(pageNumber, PAGE_SIZE));
        return new StreamList(streams.stream().toList());
    }
}
