package org.example;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {
    /**
     * 拦截所有service包下的所有方法
     */
    @Around("execution(* org.example.service.*.*(..))")
    public Object logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("==============================");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("Service method {} is called with arguments: {}", joinPoint.getSignature().getName(),
                joinPoint.getArgs());
        Object result = joinPoint.proceed();
        String point = joinPoint.getTarget().getClass().getName()
                + "."
                +  joinPoint.getSignature().getName();
        stopWatch.stop();
        long takeTime = stopWatch.getTotalTimeMillis();

        if (takeTime > 3000) {
            log.error("{} 耗时偏长 {} 毫秒", point, takeTime);
        } else if (takeTime > 2000) {
            log.warn("{} 耗时中等 {} 毫秒", point, takeTime);
        } else {
            log.info("{} 耗时 {} 毫秒", point, takeTime);
        }
        log.info("Service method {} returned: {}", joinPoint.getSignature().getName(), result);
        return result;
    }
}
