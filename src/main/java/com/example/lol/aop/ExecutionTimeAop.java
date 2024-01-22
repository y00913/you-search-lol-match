package com.example.lol.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Slf4j
@Component
public class ExecutionTimeAop {

    @Around("execution(* *..controller.*.*(..))")
    public Object calculateExecutionTime(ProceedingJoinPoint pjp) throws Throwable {
        StopWatch sw = new StopWatch();
        sw.start();

        Object result = pjp.proceed();

        sw.stop();
        long executionTime = sw.getTotalTimeMillis();

        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        String task = className + "." + methodName;

        log.info("[ExecutionTime] " + task + "-->" + executionTime + "(ms)");

        return result;
    }

}
