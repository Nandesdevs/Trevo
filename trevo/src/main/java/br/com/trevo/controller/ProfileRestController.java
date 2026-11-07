package br.com.trevo.controller;

import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.dto.ProfileRequest;
import br.com.trevo.dto.ProfileResponse;
import br.com.trevo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class ProfileRestController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ProfileResponse get(@RequestAttribute("usuarioId") Integer usuarioId) {
        UsuarioEntity u = usuarioService.buscar(usuarioId);
        return new ProfileResponse(u.getId(), u.getNome(), u.getEmail(), u.getBiografia());
    }

    @PatchMapping
    public ProfileResponse update(@RequestBody ProfileRequest req,
                                  @RequestAttribute("usuarioId") Integer usuarioId) {
        UsuarioEntity u = usuarioService.atualizar(usuarioId, req.name(), req.bio());
        return new ProfileResponse(u.getId(), u.getNome(), u.getEmail(), u.getBiografia());
    }
}
