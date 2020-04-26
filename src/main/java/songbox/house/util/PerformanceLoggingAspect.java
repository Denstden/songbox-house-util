package songbox.house.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import static java.lang.System.currentTimeMillis;
import static songbox.house.util.Constants.PERFORMANCE_MARKER;

@Aspect
@Component
@Slf4j
public class PerformanceLoggingAspect {

    @Pointcut("@annotation(songbox.house.util.Measurable)")
    public void performanceLog() {
    }

    @Around("performanceLog()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        final long start = currentTimeMillis();
        try {
            Object output = pjp.proceed();
            log.info(PERFORMANCE_MARKER, "{} - {}ms", pjp.getSignature().toShortString(), currentTimeMillis() - start);
            return output;
        } catch (Throwable e) {
            log.info(PERFORMANCE_MARKER, "{} - {}ms with exception '{}'", pjp.getSignature().toShortString(), currentTimeMillis() - start, e.getMessage());
            throw e;
        }
    }
}
