package br.com.trevo.config;

import br.com.trevo.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);   // remove "Bearer "

            try {
                Integer usuarioId = jwtService.extrairUsuarioId(token);
                request.setAttribute("usuarioId", usuarioId);   // disponibiliza pro Controller
            } catch (Exception e) {
                // token inválido/expirado — segue sem usuário logado
                // o Controller decide se bloqueia ou não
            }
        }

        chain.doFilter(request, response);   // continua a requisição
    }
}
