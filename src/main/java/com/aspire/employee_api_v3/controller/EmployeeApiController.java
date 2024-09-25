package com.aspire.employee_api_v3.controller;

import com.aspire.employee_api_v3.service.EmployeeApiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.aspire.employee_api_v3.exceptions.PageNumberException;
import com.aspire.employee_api_v3.view.EmployeeResponse;
import com.aspire.employee_api_v3.view.GenericResponse;
import com.aspire.employee_api_v3.view.StreamList;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping (path = "/api/v3", produces = "application/json")
public class EmployeeApiController {

    @Autowired
    EmployeeApiService employeeApiService;

    @GetMapping (path="/employees", produces = "application/json")
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public EmployeeResponse getEmployeeDetails(@RequestParam (name="starts-with",required = false) String letter, @RequestParam (name="page",defaultValue = "1") String page) throws IllegalArgumentException {
        return employeeApiService.getEmployeeDetails(letter,parseAndValidatePageNumber(page));
        
    }

    @GetMapping (path = "/streams", produces = "application/json")
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public StreamList getStreams(
            @RequestParam (name = "page-number", required = false, defaultValue = "1") String pageNumber) {
        return employeeApiService.getAllStreams(parseAndValidatePageNumber(pageNumber));
    }

    @PutMapping (path = "/employees/manager", produces = "application/json")
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public GenericResponse updateEmployeeManager(
            @RequestParam (name = "employee-id",required = true) Integer employeeId, @RequestParam (name="manager-id",required = true) Integer managerId
    ) {
        return employeeApiService.updateEmployeeManager(employeeId, managerId);
    }

    @PutMapping (path = "/employees/account", produces = "application/json")
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public GenericResponse updateEmployeeAccountName(
            @RequestParam (name="employee-id",required = true) Integer employeeId, @RequestParam (name="account-name",required = true) String accountName, @RequestParam (name="stream-id",required = true) String streamId
    ) {
        return employeeApiService.updateEmployeeAccountName(employeeId, accountName, streamId);
    }

    @PutMapping(path="/employees/designation",produces = "application/json")
    @ResponseStatus (HttpStatus.OK)
    @ResponseBody
    public GenericResponse updateEmployeeDesignation(
            @RequestParam(name = "employee-id", required = true) Integer employeeId,
            @RequestParam(name = "designation", required = true) String designation,
            @RequestParam(name = "stream-id", required = false) String streamId,
            @RequestParam(name = "manager-id", required = false) Integer managerId
    ){

        return employeeApiService.changeDesignation(employeeId,designation,streamId,managerId);

    }


    // Throws error if page number is non-numeric or less than 0
    private int parseAndValidatePageNumber(String pageNumber) {
        int number = Integer.parseInt(pageNumber);
        // zero based index adjustment
        number -= 1;
        if (number < 0) throw new PageNumberException("Value Cannot be less than 1");
        return number;
    }

}
