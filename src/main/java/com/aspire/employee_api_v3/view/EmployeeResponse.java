package com.aspire.employee_api_v3.view;

import java.util.List;

import com.aspire.employee_api_v3.model.Employee;


import lombok.Data;


@Data
public class EmployeeResponse {

    private List<EmployeeDto> employees; 

    public EmployeeResponse(List<Employee> employeeList) {
        this.employees = employeeList.stream()
            .map(this::convertToDto)
            .toList();
    }

    private EmployeeDto convertToDto(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setStreamId(employee.getStream() != null ? employee.getStream().getId() : null);
        dto.setAccountId(employee.getAccount() != null ? employee.getAccount().getId() : null);
        dto.setManagerId(employee.getManagerId());
        dto.setDesignation(employee.getDesignation());
        return dto;
    }
}
