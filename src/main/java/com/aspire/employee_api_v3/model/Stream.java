package com.aspire.employee_api_v3.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "stream")
@Data
@NoArgsConstructor
public class Stream {

    @Id
    private String id;
    private String name;
    private String accountId;

}
