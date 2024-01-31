package com.skechers.loyalty.users.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LogExecutionTime {
	 @Around("execution(public * *(..)) && (within(com.skechers.loyalty.*.controllers..*) || within(com.skechers.loyalty.*.service..* ))")
	    private Object logAroundEveryPublicMethod(ProceedingJoinPoint joinPoint) throws Throwable {
		 long start = System.currentTimeMillis();

		    Object proceed = joinPoint.proceed();

		    long executionTime = System.currentTimeMillis() - start;

		    log.info(joinPoint.getSignature() + " executed in " + executionTime + "ms");
		    return proceed;
	    }
}
