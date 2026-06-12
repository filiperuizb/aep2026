package org.boligon.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.boligon.auth.dto.LoginRequest;
import org.boligon.auth.dto.RegistroRequest;
import org.boligon.auth.dto.UsuarioResponse;
import org.boligon.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@RequestBody LoginRequest request,
                                 HttpServletRequest httpRequest,
                                 HttpServletResponse httpResponse) {
        return UsuarioResponse.converter(
                authService.fazerLogin(request.getEmail(), request.getSenha(), httpRequest, httpResponse)
        );
    }

    @PostMapping("/registrar")
    @ResponseStatus(HttpStatus.CREATED)
    public void registrar(@RequestBody RegistroRequest request) {
        authService.registrar(request.getNome(), request.getEmail(), request.getSenha());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.encerrarSessao(request, response);
    }
}
