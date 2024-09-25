package com.aspire.employee_api_v3.aspect;


import java.time.LocalTime;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(public * com.aspire.employee_api_v3.service.EmployeeApiService.*(..)) ||" + 
    "execution(* com.aspire.employee_api_v3.repository.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        logger.info("Starting Executing Method {} at time {}",joinPoint.getSignature(),LocalTime.now());
        
        Object proceed = joinPoint.proceed(); // Execute the method

        logger.info("Finished Executing Method {} at time {}",joinPoint.getSignature(),LocalTime.now());
        
        long executionTime = System.currentTimeMillis() - start;

        logger.info("Method {} executed in {} ms", joinPoint.getSignature(), executionTime);
        return proceed;
    }
}

