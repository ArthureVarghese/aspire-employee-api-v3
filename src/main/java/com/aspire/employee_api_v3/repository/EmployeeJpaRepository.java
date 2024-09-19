package com.aspire.employee_api_v3.repository;

import java.util.List;

import com.aspire.employee_api_v3.model.Employee;

import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<Employee, Integer> {


    List<Employee> findByNameStartingWith(String letter,Pageable pageable);
    
}

