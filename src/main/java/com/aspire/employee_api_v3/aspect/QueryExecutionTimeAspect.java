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
public class QueryExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(QueryExecutionTimeAspect.class);

    @Around("execution(* com.aspire.employee_api_v3.repository.*.*(..))")
    public Object logQueryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long startTime = System.currentTimeMillis();
        logger.info("Starting Executing Query {} at time {}",joinPoint.getSignature(),LocalTime.now());
        
        Object result = joinPoint.proceed(); 
        
        logger.info("Finished Executing Method {} at time {}",joinPoint.getSignature(),LocalTime.now());

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Executed query: {} in {} ms", joinPoint.getSignature(), duration);
        
        return result;
    }
}
