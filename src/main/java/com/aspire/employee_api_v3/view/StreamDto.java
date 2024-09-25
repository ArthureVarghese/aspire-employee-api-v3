package com.aspire.employee_api_v3.view;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StreamDto {
    private String id;
    private String name;
    private String accountId;
}
