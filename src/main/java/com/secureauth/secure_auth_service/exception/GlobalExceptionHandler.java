package com.secureauth.secure_auth_service.exception;

import com.secureauth.secure_auth_service.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UserNotFoundException userNotFoundException, HttpServletRequest request){
        ApiResponse<Object> response =
                ApiResponse.builder()
                        .success(false)
                        .message(userNotFoundException.getMessage())
                        .data(null)
                        .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /*
     * Handles invalid login.
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleInvalidCredentials(
            InvalidCredentialsException ex){

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(response);

    }

    /*
     * Handles duplicate email.
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Object>>
    handleDuplicateEmail(
            EmailAlreadyExistsException ex){

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);

    }

    /*
     * Handles validation annotations like
     * @NotBlank, @Email, @Size
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>>
    validation(
            MethodArgumentNotValidException ex){

        String message =
                ex.getBindingResult()
                        .getFieldError()
                        .getDefaultMessage();

        ApiResponse<Object> response =
                ApiResponse.builder()
                        .success(false)
                        .message(message)
                        .data(null)
                        .build();

        return ResponseEntity
                .badRequest()
                .body(response);

    }

}
