package com.aspire.employee_api_v3.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name="employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Stream stream;

    @Column(name = "stream_id")
    private String streamId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "account_id")
    private String accountId;

    private Integer managerId;
    private String designation;
    
}
