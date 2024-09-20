package com.aspire.employee_api_v3.controller;

import com.aspire.employee_api_v3.service.EmployeeApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aspire.employee_api_v3.exceptions.PageNumberException;
import com.aspire.employee_api_v3.view.StreamList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v3", produces = "application/json")
public class EmployeeApiController {

    @Autowired
    EmployeeApiService employeeApiService;

    @GetMapping("/employees")
    public ResponseEntity<?> getEmployeeDetails(@RequestParam(required = false)String letter, @RequestParam(defaultValue = "1")String page) throws IllegalArgumentException {
        // Pass the list of employee-manager pairs to the service
        return employeeApiService.getEmployeeDetails(letter,validateAndReturnPageNumber(page));
    }

    @GetMapping(path = "/streams", produces = "application/json")
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public StreamList getStreams(
            @RequestParam(name = "page-number", required = false, defaultValue = "1") String pageNumber
    ){
        return employeeApiService.getAllStreams(validateAndReturnPageNumber(pageNumber));
    }

    private int validateAndReturnPageNumber(String pageNumber){
        int number = Integer.parseInt(pageNumber) - 1;
        if(number < 0) throw new PageNumberException("Value Cannot be less than 1");
        return number;
    }

    

}
