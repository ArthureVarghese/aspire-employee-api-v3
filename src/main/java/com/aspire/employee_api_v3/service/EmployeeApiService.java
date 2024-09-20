package com.aspire.employee_api_v3.service;

import java.util.List;

import com.aspire.employee_api_v3.model.Employee;
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


    public ResponseEntity<GenericResponse> updateEmployeeManager(Integer employeeId, Integer managerId) {

        Employee employee=employeeRepo.findById(employeeId).orElse(null);
        if(employee==null)
            return new ResponseEntity<>(new GenericResponse("No employee Found"),HttpStatus.NOT_FOUND);
        if(employee.getManagerId()==0)
            return new ResponseEntity<>(new GenericResponse("Manager id of a manager can't be changed"),HttpStatus.BAD_REQUEST);

        Employee currentManager=employeeRepo.findById(employee.getManagerId()).orElse(null);
        if(currentManager==null)
            return new ResponseEntity<>(new GenericResponse("No Current Manager Found"),HttpStatus.NOT_FOUND);
        if(currentManager.getId()==managerId)
            return new ResponseEntity<>(new GenericResponse("Current Manager same as New manager"),HttpStatus.CONFLICT);

        Employee newManager=employeeRepo.findById(managerId).orElse(null);
        if(newManager==null)
            return new ResponseEntity<>(new GenericResponse("No Manager Found"),HttpStatus.NOT_FOUND);
        if(newManager.getManagerId()!=0){
            return new ResponseEntity<>(new GenericResponse("Provided manager id doesn't belong to a manager"),HttpStatus.BAD_REQUEST);
        }

        employee.setManagerId(managerId);
        employee.setStreamId(newManager.getStreamId());
        employee.setAccountId(newManager.getAccountId());
        employeeRepo.save(employee);
        
        return new ResponseEntity<>(new GenericResponse(employee.getName()+"'s manager details has been updated"),HttpStatus.OK);
    }

}
