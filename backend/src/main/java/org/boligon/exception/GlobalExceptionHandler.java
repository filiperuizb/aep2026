package org.boligon.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<Map<String, String>> tratarValidacao(ValidacaoException ex) {
        Map<String, String> corpo = new HashMap<>();
        corpo.put("mensagem", ex.getMessage());
        return ResponseEntity.badRequest().body(corpo);
    }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ResponseEntity<Map<String, String>> tratarNaoEncontrada(EntidadeNaoEncontradaException ex) {
        Map<String, String> corpo = new HashMap<>();
        corpo.put("mensagem", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> tratarRegraNegocio(RegraNegocioException ex) {
        Map<String, String> corpo = new HashMap<>();
        corpo.put("mensagem", ex.getMessage());
        return ResponseEntity.badRequest().body(corpo);
    }
}
