package com.aspire.employee_api_v3.view;

import lombok.Data;

@Data
public class EmployeeDto {
    private Integer id;
    private String name;
    private String streamId;
    private String accountId;
    private Integer managerId;
    private String designation;
}