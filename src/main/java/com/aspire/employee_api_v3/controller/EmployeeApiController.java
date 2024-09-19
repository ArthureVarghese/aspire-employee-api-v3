package com.aspire.employee_api_v3.controller;

//import java.util.List;

//import java.util.List;

//import com.aspire.employee_api_v3.model.Employee;
import com.aspire.employee_api_v3.service.EmployeeApiService;
//import com.aspire.employee_api_v3.view.Response;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v3", produces = "application/json")
public class EmployeeApiController {

    @Autowired
    EmployeeApiService employeeApiService;
    
    @GetMapping("/employees")
    public ResponseEntity<?> getEmployeeDetails(@RequestParam(required = false)String letter, @RequestParam(defaultValue = "1")Integer page) {
        // Pass the list of employee-manager pairs to the service
        return employeeApiService.getEmployeeDetails(letter,page);
    }

}
