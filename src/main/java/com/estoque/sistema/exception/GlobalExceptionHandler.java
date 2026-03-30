package com.estoque.sistema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Object> handleOptimisticLocking(ObjectOptimisticLockingFailureException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Conflito de edição simultânea. Recarregue o item e tente novamente.");
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        Map<String, Object> body = buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
        body.put("detalheTecnico", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String mensagem) {
        return new ResponseEntity<>(buildBody(status, mensagem), status);
    }

    private Map<String, Object> buildBody(HttpStatus status, String mensagem) {
        Map<String, Object> body = new HashMap<>();
        body.put("dataHora", LocalDateTime.now());
        body.put("mensagem", mensagem);
        body.put("statusHttp", status.value());
        return body;
    }
}
