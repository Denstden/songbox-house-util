package songbox.house.util;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.empty;
import static org.slf4j.LoggerFactory.getLogger;
import static songbox.house.util.Constants.PERFORMANCE_MARKER;

@Slf4j
public final class RetryUtil {

    private static final Logger LOGGER = getLogger(RetryUtil.class);

    public static final int DEFAULT_RETRIES = 10;
    private static final int DEFAULT_SLEEP_MS = 500;

    private RetryUtil() {
    }

    public static <T, R> Optional<R> getOptionalWithRetries(final Function<T, Optional<R>> function, final T input,
            final int maxRetries, final String operation) {
        final long startMs = currentTimeMillis();
        int retries = 0;
        Optional<R> result;
        do {
            if (retries != 0) {
                log.debug("Retry {}, {}", retries, operation);
            }
            try {
                result = function.apply(input);
            } catch (Exception e) {
                log.debug("Retryable exception", e);
                result = empty();
            }
        } while (!result.isPresent() && ++retries < maxRetries);

        if (result.isPresent()) {
            log.debug(PERFORMANCE_MARKER, "Executed {} in {} retries, time {}ms",
                    operation, retries, currentTimeMillis() - startMs);
        } else {
            log.warn("Can't execute {} in {} tries", operation, retries);
        }


        return result;
    }

    public static <T, R> Optional<R> getOptionalWithRetriesWithProgressiveDelay(final Function<T, Optional<R>> function,
            final T input, final int maxRetries, int sleepMs, int delayMultiplier, final String operation) {

        final long startMs = currentTimeMillis();
        int retries = 0;
        Optional<R> result;
        do {
            if (retries != 0) {
                int sleep = sleepMs * withMultipliedDelay(retries, delayMultiplier);
                log.debug("Retry {}, {} - sleeping {}ms", retries, operation, sleep);
                doSleep(sleep);
            }
            try {
                result = function.apply(input);
            } catch (Exception e) {
                log.debug("Retryable exception", e);
                result = empty();
            }
        } while (!result.isPresent() && ++retries < maxRetries);

        if (result.isPresent()) {
            log.debug(PERFORMANCE_MARKER, "Executed {} in {} retries, time {}ms",
                    operation, retries, currentTimeMillis() - startMs);
        } else {
            log.warn("Can't execute {} in {} tries", operation, retries);
        }


        return result;
    }

    public static <T, E, R> Optional<R> getOptionalWithRetries(final BiFunction<T, E, Optional<R>> function,
            final T input1, final E input2, final int maxRetries, final String operation) {
        int retries = 0;
        Optional<R> result;
        do {
            if (retries != 0) {
                log.debug("Retry {}, {}", retries, operation);
            }
            try {
                result = function.apply(input1, input2);
            } catch (Exception e) {
                log.debug("Retryable exception", e);
                result = empty();
            }
        } while (!result.isPresent() && ++retries < maxRetries);

        if (!result.isPresent()) {
            log.warn("Can't execute {} in {} tries", operation, retries);
        }

        return result;
    }

    public static <T, R> Optional<R> getOptionalWithDefaultRetries(final Function<T, Optional<R>> function,
            final T input, final String operation) {
        return getOptionalWithRetries(function, input, DEFAULT_RETRIES, operation);
    }

    public static <R> void executeWithRetryOnException(Consumer<R> consumer, R input) {
        executeWithRetryOnException(consumer, input, DEFAULT_RETRIES);
    }

    public static <R> void executeWithRetryOnException(Consumer<R> consumer, R input, int maxRetries) {
        executeWithRetryOnException(consumer, input, DEFAULT_SLEEP_MS, maxRetries);
    }

    public static <T> void executeWithRetryOnException(Consumer<T> function, T input, int sleepMs, int maxRetries) {
        executeWithRetryOnExceptionWithProgressiveDelay(function, input, sleepMs, RetryUtil::withoutDelay,
                1, maxRetries);
    }

    public static <T> void executeWithRetryOnExceptionWithProgressiveDelay(Consumer<T> function, T input, int sleepMs,
            int delayMultiplier, int maxRetries) {

        executeWithRetryOnExceptionWithProgressiveDelay(function, input, sleepMs, RetryUtil::withMultipliedDelay,
                delayMultiplier, maxRetries);
    }

    public static <T, R> T executeWithRetryOnException(Function<R, T> function, R input, T defaultValue, int sleepMs,
            int maxRetries) {

        return executeWithRetryOnExceptionWithProgressiveDelay(function, input, defaultValue, sleepMs,
                RetryUtil::withoutDelay, 1, maxRetries);
    }

    public static <T, R> T executeWithRetryOnException(Function<R, T> function, R input, T defaultValue) {
        return executeWithRetryOnException(function, input, defaultValue, DEFAULT_RETRIES);
    }

    public static <T, R> T executeWithRetryOnException(Function<R, T> function, R input, T defaultValue,
            int maxRetries) {

        return executeWithRetryOnExceptionWithProgressiveDelay(function, input, defaultValue, DEFAULT_SLEEP_MS,
                RetryUtil::withoutDelay, 1, maxRetries);
    }

    public static <T, R> T executeWithRetryOnExceptionWithProgressiveDelay(Function<R, T> function, R input,
            T defaultValue, int sleepMs, int delayMultiplier, int maxRetries) {

        return executeWithRetryOnExceptionWithProgressiveDelay(function, input, defaultValue, sleepMs,
                RetryUtil::withMultipliedDelay, delayMultiplier, maxRetries);
    }

    public static <T, R> T executeWithRetryThrowingException(Function<R, T> function, R input, T defaultValue) {
        return executeWithRetryThrowingException(function, input, defaultValue, DEFAULT_RETRIES);
    }

    public static <T, R, U> U executeWithRetryThrowingException(BiFunction<R, T, U> function, R input1, T input2,
            U defaultValue) {
        return executeWithRetryThrowingException(function, input1, input2, defaultValue, DEFAULT_RETRIES);
    }

    public static <T, R, U> U executeWithRetryThrowingException(BiFunction<R, T, U> function, R input1, T input2,
            U defaultValue,
            int maxRetries) {
        int retries = 0;
        boolean needRetry;
        U result = defaultValue;
        Exception lastException = null;
        do {
            try {
                result = function.apply(input1, input2);
                needRetry = false;
                lastException = null;
            } catch (Exception e) {
                lastException = e;
                LOGGER.debug("Retryable exception", e);
                needRetry = retries++ < maxRetries;
                if (needRetry) {
                    doSleep(DEFAULT_SLEEP_MS * retries);
                } else {
                    LOGGER.warn("Can't execute function in {} retries for input {}, {}", retries, input1, input2);
                }
            }
        } while (needRetry);

        if (nonNull(lastException)) {
            throw new RuntimeException(lastException);
        }

        return result;
    }

    public static <T, R, U> T executeWithRetryOnException(BiFunction<R, U, T> function, R input1, U input2,
            T defaultValue, int maxRetries) {
        int retries = 0;
        boolean needRetry;
        T result = defaultValue;
        do {
            try {
                result = function.apply(input1, input2);
                needRetry = false;
            } catch (Exception e) {
                LOGGER.debug("Retryable exception", e);
                needRetry = retries++ < maxRetries;
                if (needRetry) {
                    doSleep(DEFAULT_SLEEP_MS * retries);
                } else {
                    LOGGER.warn("Can't execute function in {} retries for input {}|{}", retries, input1, input2);
                }
            }
        } while (needRetry);

        return result;
    }

    public static <T, R> List<T> executeWithRetryOnException(BiFunction<R, R, List<T>> function, R input1, R input2,
            int maxRetries) {
        return executeWithRetryOnException(function, input1, input2, newArrayList(), maxRetries);
    }

    public static <T, R1, R2> List<T> executeWithRetryOnException(BiFunction<R1, R2, List<T>> function, R1 input1,
            R2 input2) {
        return executeWithRetryOnException(function, input1, input2, newArrayList(), DEFAULT_RETRIES);
    }

    public static <T, R> T executeWithRetryThrowingException(Function<R, T> function, R input, T defaultValue,
            int maxRetries) {
        return executeWithRetryThrowingException(function, input, defaultValue, maxRetries, DEFAULT_SLEEP_MS);
    }

    public static <T, R> T executeWithRetryThrowingException(Function<R, T> function, R input, T defaultValue,
            int maxRetries, int sleepMs) {
        int retries = 0;
        boolean needRetry;
        T result = defaultValue;
        Exception lastException = null;
        do {
            try {
                result = function.apply(input);
                needRetry = false;
                lastException = null;
            } catch (Exception e) {
                lastException = e;
                LOGGER.debug("Retryable exception", e);
                needRetry = retries++ < maxRetries;
                if (needRetry) {
                    doSleep(sleepMs * retries);
                } else {
                    LOGGER.warn("Can't execute function in {} retries for input {}", retries, input);
                }
            }
        } while (needRetry);

        if (nonNull(lastException)) {
            throw new RuntimeException(lastException);
        }

        return result;
    }

    public static <T, R> List<T> executeWithRetryOnException(Function<R, List<T>> function, R input, int maxRetries) {
        return executeWithRetryOnException(function, input, newArrayList(), maxRetries);
    }

    public static <T, R> List<T> executeWithRetryOnException(Function<R, List<T>> function, R input) {
        return executeWithRetryOnException(function, input, DEFAULT_RETRIES);
    }

    public static <T, R> T executeWithRetryOnExceptionWithProgressiveDelay(Function<R, T> function, R input,
            T defaultValue, int sleepMs, BiFunction<Integer, Integer, Integer> delayFunction, int delayMultiplier,
            int maxRetries) {

        return executeWithRetryOnExceptionOrNullWithProgressiveDelay(function, input, defaultValue, sleepMs,
                delayFunction, delayMultiplier, maxRetries, false);
    }

    public static <T, R> T executeWithRetryOnExceptionOrNullWithProgressiveDelay(Function<R, T> function, R input,
            T defaultValue,
            int sleepMs, int delayMultiplier, int maxRetries) {

        return executeWithRetryOnExceptionOrNullWithProgressiveDelay(function, input, defaultValue, sleepMs, RetryUtil::withMultipliedDelay,
                delayMultiplier, maxRetries, true);
    }

    private static <T, R> T executeWithRetryOnExceptionOrNullWithProgressiveDelay(Function<R, T> function, R input,
            T defaultValue, int sleepMs, BiFunction<Integer, Integer, Integer> delayFunction, int delayMultiplier,
            int maxRetries, boolean checkForNull) {

        int retries = 0;
        boolean fail;
        T result = defaultValue;
        do {
            try {
                result = function.apply(input);
                fail = checkForNull && isNull(result);
            } catch (Exception e) {
                LOGGER.warn("Exception on {} retry", retries, e);
                fail = true;
            }
            if (!fail) {
                return result;
            } else if (retries++ < maxRetries) {
                doSleep(sleepMs * delayFunction.apply(retries, delayMultiplier));
            } else {
                return defaultValue;
            }
        } while (true);
    }

    private static <T> void executeWithRetryOnExceptionWithProgressiveDelay(Consumer<T> consumer, T input,
            int sleepMs, BiFunction<Integer, Integer, Integer> delayFunction, int delayMultiplier,
            int maxRetries) {

        int retries = 0;
        boolean needRetry;
        do {
            try {
                consumer.accept(input);
                needRetry = false;
            } catch (Exception e) {
                LOGGER.warn("Exception on {} retry", retries, e);
                needRetry = retries++ < maxRetries;
                if (needRetry) {
                    doSleep(sleepMs * delayFunction.apply(retries, delayMultiplier));
                }
            }
        } while (needRetry);
    }

    private static Integer withoutDelay(Integer retry, Integer delayMultiplier) {
        return 1;
    }

    private static Integer withMultipliedDelay(Integer retry, Integer delayMultiplier) {
        return retry * delayMultiplier;
    }

    private <T> Consumer<T> toConsumer(Function<T, Void> function) {
        return function::apply;
    }

    private static void doSleep(int ms) {
        if (ms > 0) {
            try {
                sleep(ms);
            } catch (InterruptedException ex) {
                LOGGER.warn("Interrupted while sleeping", ex);
                currentThread().interrupt();
            }
        }
    }

    private static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }
}
