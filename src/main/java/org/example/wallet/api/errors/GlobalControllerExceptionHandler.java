package org.example.wallet.api.errors;

import lombok.extern.slf4j.Slf4j;
import org.example.wallet.api.dtos.error.ErrorDtoResponse;
import org.example.wallet.api.dtos.error.SingleErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleBadRequestException(ServerException ex) {
        log.error("ServerException occurred: {}", ex.getMessage(), ex);

        ServerErrorCode code = ex.getServerErrorCode();

        SingleErrorDto error = SingleErrorDto.builder()
                .errorCode(code.name())
                .message(ex.getMessage())
                .build();

        return ErrorDtoResponse.builder()
                .errors(List.of(error))
                .build();
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDtoResponse handleNotFound(NoHandlerFoundException ex) {
        log.error("NoHandlerFoundException: URL {} not found", ex.getRequestURL(), ex);

        SingleErrorDto error = SingleErrorDto.builder()
                .errorCode("NOT_FOUND")
                .message("Ресурс по адресу " + ex.getRequestURL() + " не найден")
                .build();

        return ErrorDtoResponse.builder().errors(List.of(error)).build();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException: parameter '{}', value '{}', required type '{}'",
                ex.getName(), ex.getValue(), ex.getRequiredType(), ex);

        SingleErrorDto error = SingleErrorDto.builder()
                .errorCode("INVALID_TYPE")
                .message("Неверный тип параметра: " + ex.getName())
                .build();

        return ErrorDtoResponse.builder()
                .errors(List.of(error))
                .build();
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ErrorDtoResponse handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.error("HttpMediaTypeNotSupportedException: {}", ex.getMessage(), ex);

        SingleErrorDto error = SingleErrorDto.builder()
                .errorCode("UNSUPPORTED_MEDIA_TYPE")
                .message("Неподдерживаемый тип тела запроса")
                .build();

        return ErrorDtoResponse.builder()
                .errors(List.of(error))
                .build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDtoResponse handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.error("HttpRequestMethodNotSupportedException: method '{}' not allowed", ex.getMethod(), ex);

        SingleErrorDto error = SingleErrorDto.builder()
                .errorCode("METHOD_NOT_ALLOWED")
                .message("Запрос с методом " + ex.getMethod() + " не поддерживается на данный адрес")
                .build();

        return ErrorDtoResponse.builder().errors(List.of(error)).build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDtoResponse handleBadRequestException(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: validation failed", ex);

        List<SingleErrorDto> errors = new ArrayList<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(SingleErrorDto.builder()
                    .errorCode("VALIDATION_ERROR")
                    .message(error.getDefaultMessage())
                    .build());
        }

        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(SingleErrorDto.builder()
                    .errorCode("VALIDATION_ERROR")
                    .message(error.getDefaultMessage())
                    .build());
        }

        return ErrorDtoResponse.builder()
                .errors(errors)
                .build();
    }

}
