package com.aspire.employee_api_v3.view;

import java.util.List;

import com.aspire.employee_api_v3.model.Employee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    List<Employee> employees;
    String error;
}
