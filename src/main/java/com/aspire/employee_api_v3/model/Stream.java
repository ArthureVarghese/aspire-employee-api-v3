package com.aspire.employee_api_v3.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "stream")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stream {

    @Id
    private String id;
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    @Column(name = "account_id")
    private String accountId;

}
