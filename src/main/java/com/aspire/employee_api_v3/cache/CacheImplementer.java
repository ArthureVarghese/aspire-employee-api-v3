package com.aspire.employee_api_v3.cache;

import com.aspire.employee_api_v3.log.LoggerUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class CacheImplementer {

    private static final Logger logger = LoggerFactory.getLogger(CacheImplementer.class);

    @Autowired
    Cache<String,Object> cache;

    @Around("@annotation(com.aspire.employee_api_v3.cache.annotation.Cached)")
    public Object useCache(ProceedingJoinPoint joinPoint) throws Throwable{
        String key = keyGenerator(joinPoint.getArgs(),joinPoint.getSignature().getName());
        if(cache.containsKey(key)){
            long start = System.currentTimeMillis();
            logger.info("Fetching from Cache for {}",joinPoint.getSignature().getName());
            Object cacheValue = cache.get(key);
            long executionTime = System.currentTimeMillis() - start;
            logger.info("Took Cache Time {} ms",executionTime);
            return cacheValue;
        }

        Object result = joinPoint.proceed();

        cache.put(key,result);
        return result;
    }

    @Around("@annotation(com.aspire.employee_api_v3.cache.annotation.CacheUpdate)")
    public Object updateCache(ProceedingJoinPoint joinPoint) throws Throwable{
        String key = keyGenerator(joinPoint.getArgs(), joinPoint.getSignature().getName());
        Object result = joinPoint.proceed();
        cache.put(key,result);
        return result;
    }

    @Around("@annotation(com.aspire.employee_api_v3.cache.annotation.CacheDelete)")
    public Object deleteCache(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("\n\n\n\nDeleting Cache\n\n\n\n");
        cache.clear();
        return joinPoint.proceed();
    }

    private String keyGenerator(Object[] args, String name){
        StringBuilder stringBuilder = new StringBuilder(name);

        if(args == null || args.length == 0){
            stringBuilder.append("DEFAULT_VALUE_FOR_NO_ARGUMENT");
            return stringBuilder.toString();
        }
        for (Object arg : args) {
            stringBuilder.append(arg.toString());
        }
        return stringBuilder.toString();
    }
}
