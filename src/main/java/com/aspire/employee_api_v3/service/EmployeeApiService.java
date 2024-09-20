package com.aspire.employee_api_v3.service;

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


@Service
public class EmployeeApiService {

    @Autowired
    private EmployeeJpaRepository employeeRepo;

    @Autowired
    StreamJpaRepository streamJpaRepository;

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
}
