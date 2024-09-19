package com.aspire.employee_api_v3.repository;

import com.aspire.employee_api_v3.model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StreamJpaRepository extends JpaRepository<Stream,String> {

}
