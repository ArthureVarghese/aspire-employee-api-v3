package com.aspire.employee_api_v3.repository;

import java.util.Optional;

import com.aspire.employee_api_v3.model.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountJpaRepository extends JpaRepository<Account, String>{

    Optional<Account> findByName(String accountName);
    
}
