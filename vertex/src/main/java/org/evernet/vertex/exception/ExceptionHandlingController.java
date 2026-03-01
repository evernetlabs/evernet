package org.evernet.vertex.exception;

import lombok.extern.slf4j.Slf4j;
import org.evernet.vertex.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class ExceptionHandlingController {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleAuthenticationException(AuthenticationException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ErrorResponse.withMessage(e.getBindingResult().getAllErrors().getFirst().getDefaultMessage());
    }

    @ExceptionHandler(ClientException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleClientException(ClientException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorResponse handleInvalidTokenException(InvalidTokenException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorResponse handleNotAllowedException(NotAllowedException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNotFoundException(NotFoundException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(ServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleServerException(ServerException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoResourceFoundException(NoResourceFoundException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleUnknownException(Throwable e) {
        log.error("Unknown error occurred", e);
        return ErrorResponse.withMessage(e.getMessage());
    }
}