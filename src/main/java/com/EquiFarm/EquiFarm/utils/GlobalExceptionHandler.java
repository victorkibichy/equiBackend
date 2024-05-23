// package com.EquiFarm.EquiFarm.utils;

// import java.util.HashMap;
// import java.util.Map;

// import org.springframework.http.HttpStatus;

// import org.springframework.web.bind.MethodArgumentNotValidException;

// import org.springframework.web.bind.annotation.ExceptionHandler;
// import org.springframework.web.bind.annotation.ResponseStatus;

// import org.springframework.web.bind.annotation.RestControllerAdvice;



// @RestControllerAdvice
// public class GlobalExceptionHandler {

//     @ExceptionHandler(MethodArgumentNotValidException.class)
//     @ResponseStatus(HttpStatus.BAD_REQUEST)
//     public Map<String, String> handleValidationException(MethodArgumentNotValidException ex) {
//         Map<String, String> errors = new HashMap<>();
//         ex.getBindingResult().getFieldErrors().forEach(error ->
//                 errors.put(error.getField(), error.getDefaultMessage()));
//         return errors;
//     }

//     @ExceptionHandler(Exception.class)
//     @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//     public Map<String, String> handleGlobalException(Exception ex) {
//         Map<String, String> error = new HashMap<>();
//         error.put("message", "An unexpected error occurred");
//         return error;
//     }  
// }
