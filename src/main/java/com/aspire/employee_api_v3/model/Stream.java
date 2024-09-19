package com.aspire.employee_api_v3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name = "stream")
public class Stream {

    @Id
    private String id;
    private String name;
    private String accountId;
    
}
