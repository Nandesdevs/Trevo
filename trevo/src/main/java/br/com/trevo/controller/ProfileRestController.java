package br.com.trevo.controller;

import br.com.trevo.database.model.ArquivoEntity;
import br.com.trevo.database.model.TipoArquivo;
import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.database.repository.IUsuarioRepository;
import br.com.trevo.dto.ProfileRequest;
import br.com.trevo.dto.ProfileResponse;
import br.com.trevo.service.ArquivoService;
import br.com.trevo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class ProfileRestController {

    private final UsuarioService usuarioService;
    private final IUsuarioRepository usuarioRepository;
    private final ArquivoService arquivoService;

    @GetMapping
    public ProfileResponse get(@RequestAttribute("usuarioId") Integer usuarioId) {
        UsuarioEntity u = usuarioService.buscar(usuarioId);
        return ProfileResponse.from(u);
    }

    @PatchMapping
    public ProfileResponse update(@RequestBody ProfileRequest req, @RequestAttribute("usuarioId") Integer usuarioId) {
        UsuarioEntity u = usuarioService.atualizar(usuarioId, req.name(), req.bio());
        return ProfileResponse.from(u);
    }

    @PostMapping("/me/avatar")
    public ProfileResponse uploadAvatar(@RequestBody AvatarRequest req,
                                        @RequestAttribute("usuarioId") Integer usuarioId) {
        UsuarioEntity u = usuarioService.buscar(usuarioId);

        ArquivoEntity avatar = arquivoService.salvarUrl(
                req.url(), "avatar.jpg", TipoArquivo.FOTO_PERFIL, null);

        u.setFotoDePerfil(avatar);
        usuarioRepository.save(u);

        return ProfileResponse.from(u);
    }

    record AvatarRequest(String url) {}
}
