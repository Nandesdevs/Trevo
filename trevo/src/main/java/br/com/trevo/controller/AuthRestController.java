package br.com.trevo.controller;

import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.dto.ForgotPasswordRequest;
import br.com.trevo.dto.LoginRequest;
import br.com.trevo.dto.RegisterRequest;
import br.com.trevo.dto.ResetPasswordRequest;
import br.com.trevo.service.AuthService;
import br.com.trevo.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor()
public class AuthRestController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@RequestBody RegisterRequest req) {
        UsuarioEntity usuario = authService.cadastrar(
            req.name(), req.email(),  req.password());

        String token = jwtService.gerar(usuario.getId());
        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", usuario.getId(),
                        "name", usuario.getNome(),
                        "email", usuario.getEmail()
                )
        );
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        UsuarioEntity usuario = authService.login(req.login(), req.password());

        String token = jwtService.gerar(usuario.getId());

        return Map.of(
                "token", token,
                "user", Map.of(
                        "id", usuario.getId(),
                        "name", usuario.getNome(),
                        "email", usuario.getEmail()
                )
        );
    }

    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        String token = authService.gerarTokenRecuperacao(req.contact());
        return Map.of(
                "message", "Se o email existir, enviaremos instruções",
                "token", token
        );
    }

    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resetPassword(@RequestBody ResetPasswordRequest req) {
        authService.redefinirSenha(req.token(), req.password());
    }
}