package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiError handleIllegalArgumentException(final IllegalArgumentException ex) {
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("Some errors with validation were handled")
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodArgumentNotValidException (final MethodArgumentNotValidException ex) {
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("Some errors with validation were handled (")
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST) //400
    @ExceptionHandler(BadRequestException.class)
    public ApiError handleBadRequestException(BadRequestException ex) {
        log.info("BadRequestException: {}", ex.getMessage());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("Request contains wrong data.")
                .status(HttpStatus.BAD_REQUEST.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN) //403
    @ExceptionHandler(ForbiddenException.class)
    public ApiError handleForbiddenException(ForbiddenException ex) {
        log.info("ForbiddenException: {}", ex.getMessage());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("There are no rights, access has forbidden.")
                .status(HttpStatus.FORBIDDEN.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND) //404
    @ExceptionHandler(NotFoundException.class)
    public ApiError handleNotFoundException(NotFoundException ex) {
        log.info("NotFoundException: {}", ex.getMessage());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("There are no object has requested.")
                .status(HttpStatus.NOT_FOUND.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.CONFLICT) //409
    @ExceptionHandler(ConflictException.class)
    public ApiError handleConflictException(ConflictException ex) {
        log.info("ConflictException: {}", ex.getMessage());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("Request contains invalid values.")
                .status(HttpStatus.CONFLICT.toString())
                .build();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) //500
    @ExceptionHandler(Throwable.class)
    public ApiError handleThrowable(Throwable ex) {
        log.info("Unexpected exception: {}", ex.getMessage());
        return ApiError.builder()
                .errors(List.of(Arrays.toString(ex.getStackTrace())))
                .message(ex.getMessage())
                .reason("Unexpected exception.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .build();
    }
}
