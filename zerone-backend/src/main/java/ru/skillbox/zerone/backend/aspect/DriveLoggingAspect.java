package ru.skillbox.zerone.backend.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class DriveLoggingAspect {

  @Pointcut("execution(* ru.skillbox.zerone.backend.service.DriveService.transferLogsToGoogleDrive())")
  public void transferFilesPointcut() {}

  @AfterThrowing(value = "transferFilesPointcut()", throwing = "ex")
  public void transferFilesAdvice(Exception ex) {

    log.warn("transfer failed cause {}", ex.getMessage());
  }

  @Pointcut("execution(* ru.skillbox.zerone.backend.configuration.GoogleDriveConfig.credentials())")
  public void credentialsPointcut() {}

  @AfterThrowing(value = "credentialsPointcut()", throwing = "ex")
  public void credentialsAdvice(Exception ex) {

    log.warn("access failed cause {}", ex.getMessage());
  }

  @Pointcut("execution(* ru.skillbox.zerone.backend.configuration.GoogleDriveConfig.httpTransport())")
  public void httpTransportPointcut() {}

  @AfterThrowing(value = "httpTransportPointcut()", throwing = "ex")
  public void httpTransportAdvice(Exception ex) {

    log.warn("access failed cause {}", ex.getMessage());
  }
}
