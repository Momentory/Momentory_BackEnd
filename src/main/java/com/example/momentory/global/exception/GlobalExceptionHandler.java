package com.example.momentory.global.exception;

import com.example.momentory.global.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.UUID;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {


    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<?> handleGeneral(GeneralException ex, HttpServletRequest req) {
        String errorId = UUID.randomUUID().toString();
        log.warn("[{}] {} {} - {}", errorId, req.getMethod(), req.getRequestURI(), ex.toString());

        return ResponseEntity
                .status(ex.getErrorReason().getHttpStatus())
                .body(ApiResponse.onFailure(ex.getErrorReason().getCode(), ex.getMessage(), ""));
    }


    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(NoResourceFoundException e) {
        // favicon 요청 같은건 굳이 로그 안 남김
    }

}
