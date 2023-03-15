package com.store.becoffeestore.handler;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingExceptionHandler {

    Logger logger = LoggerFactory.getLogger(LoggingExceptionHandler.class);

    @Pointcut("within(com.store..*) && execution(* *(..))")
    public void allMethods() {}

    @AfterThrowing(value = "allMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {

        logger.error(exception.getMessage());
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        logger.error("class {}, method {}, arguments {} ", className, methodName, methodArgs);

    }
}