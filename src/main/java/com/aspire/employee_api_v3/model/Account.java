package com.aspire.employee_api_v3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity(name = "account")
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id
    private String id;
    private String name;

    
}
