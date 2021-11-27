package br.cams7.tests.springwebfluxessentials.logging;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogEntryExitAspect {

  @Around("@annotation(br.cams7.tests.springwebfluxessentials.logging.LogEntryExit)")
  public Object log(ProceedingJoinPoint point) throws Throwable {
    var codeSignature = (CodeSignature) point.getSignature();
    var methodSignature = (MethodSignature) point.getSignature();
    Method method = methodSignature.getMethod();
    Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
    var annotation = method.getAnnotation(LogEntryExit.class);
    LogLevel level = annotation.value();
    ChronoUnit unit = annotation.unit();
    boolean showArgs = annotation.showArgs();
    boolean showResult = annotation.showResult();
    boolean showExecutionTime = annotation.showExecutionTime();
    String methodName = method.getName();
    Object[] methodArgs = point.getArgs();
    String[] methodParams = codeSignature.getParameterNames();
    log(logger, level, entry(methodName, showArgs, methodParams, methodArgs));
    var start = Instant.now();
    var response = point.proceed();
    var end = Instant.now();
    var duration = String.format("%s %s", unit.between(start, end), unit.name().toLowerCase());
    log(logger, level, exit(methodName, duration, response, showResult, showExecutionTime));
    return response;
  }

  private static String entry(String methodName, boolean showArgs, String[] params, Object[] args) {
    StringJoiner message = new StringJoiner(" ").add("Started").add(methodName).add("method");
    if (showArgs
        && Objects.nonNull(params)
        && Objects.nonNull(args)
        && params.length == args.length) {
      Map<String, Object> values = new HashMap<>(params.length);
      for (int i = 0; i < params.length; i++) {
        values.put(params[i], args[i]);
      }
      message.add("with args:").add(values.toString());
    }
    return message.toString();
  }

  private static String exit(
      String methodName,
      String duration,
      Object result,
      boolean showResult,
      boolean showExecutionTime) {
    var message = new StringJoiner(" ").add("Finished").add(methodName).add("method");
    if (showExecutionTime) {
      message.add("in").add(duration);
    }
    if (showResult) {
      message.add("with return:").add(result.toString());
    }
    return message.toString();
  }

  private static void log(Logger logger, LogLevel level, String message) {
    switch (level) {
      case DEBUG:
        logger.debug(message);
        break;
      case TRACE:
        logger.trace(message);
        break;
      case WARN:
        logger.warn(message);
        break;
      case ERROR:
      case FATAL:
        logger.error(message);
        break;
      default:
        logger.info(message);
        break;
    }
  }
}
