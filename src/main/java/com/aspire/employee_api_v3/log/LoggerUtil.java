package com.aspire.employee_api_v3.log;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class Logger {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);

    @Around("execution(public * com.aspire.employee_api_v3.service.EmployeeApiService.*(..)) ||" +
    "execution(* com.aspire.employee_api_v3.repository.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        logger.info("Starting Executing Method {} at time {}",joinPoint.getSignature(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss")));
        
        Object proceed = joinPoint.proceed();

        logger.info("Finished Executing Method {} at time {}",joinPoint.getSignature(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy | HH:mm:ss")));
        
        long executionTime = System.currentTimeMillis() - start;

        logger.info("Method '{}' in class '{}' executed in {} ms", joinPoint.getSignature().getName(), joinPoint.getSignature().getDeclaringType().getSimpleName(), executionTime);
        return proceed;
    }

    @Around("execution(public * com.aspire.employee_api_v3.cache.CacheImplementer.*(..))")
    public Object logWhenUsingCache(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        logger.info("Fetching from Cache for {}",joinPoint.getSignature().getName());
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;
        logger.info("Took Time {} ms",executionTime);
        return proceed;
    }

}

