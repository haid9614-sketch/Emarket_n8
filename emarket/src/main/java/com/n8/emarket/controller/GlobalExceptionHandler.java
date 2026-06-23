package com.n8.emarket.controller; // Đổi lại theo đúng đường dẫn thư mục của bạn

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        // Trả về mã lỗi 400 (Bad Request)
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
