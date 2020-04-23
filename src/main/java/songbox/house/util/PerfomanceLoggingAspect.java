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
public class PerfomanceLoggingAspect {

    @Pointcut("within(@songbox.house.util.Measurable *)")
    public void measurable() {
    }

    @Around("measurable()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {
        final long start = currentTimeMillis();
        Object output = pjp.proceed();
        final long end = currentTimeMillis() - start;
        log.info(PERFORMANCE_MARKER, "{}{} - {}ms", pjp.getSignature().getDeclaringType().getSimpleName(),
                pjp.getSignature().getName(), end - start);
        return output;
    }
}
