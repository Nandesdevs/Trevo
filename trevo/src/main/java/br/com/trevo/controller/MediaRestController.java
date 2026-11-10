package br.com.trevo.controller;

import br.com.trevo.database.model.ArquivoEntity;
import br.com.trevo.database.model.SubAlbumEntity;
import br.com.trevo.database.model.TipoArquivo;
import br.com.trevo.dto.MediaRequest;
import br.com.trevo.dto.MediaResponse;
import br.com.trevo.service.ArquivoService;
import br.com.trevo.service.SubAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MediaRestController {

    private final ArquivoService arquivoService;
    private final SubAlbumService subAlbumService;

    @PostMapping("/categories/{categoryId}/media")
    @ResponseStatus(HttpStatus.CREATED)
    public MediaResponse criar(@PathVariable Integer categoryId, @RequestBody MediaRequest req, @RequestAttribute("usuarioId") Integer usuarioId) {

        SubAlbumEntity sub = subAlbumService.buscar(categoryId, usuarioId);

        TipoArquivo tipo = "VIDEO".equalsIgnoreCase(req.tipo())
                ? TipoArquivo.VIDEO
                : TipoArquivo.FOTO;

        ArquivoEntity arquivo = arquivoService.salvarUrl(
                req.url(), req.nome(), tipo, sub);

        return MediaResponse.from(arquivo);
    }

    @GetMapping("/categories/{categoryId}/media")
    public List<MediaResponse> listar(@PathVariable Integer categoryId,
                                      @RequestAttribute("usuarioId") Integer usuarioId) {
        subAlbumService.buscar(categoryId, usuarioId);
        return arquivoService.listarPorSubAlbum(categoryId)
                .stream().map(MediaResponse::from).toList();
    }

    @GetMapping("/trash")
    public List<MediaResponse> listarLixeira(@RequestAttribute("usuarioId") Integer usuarioId) {
        return arquivoService.listarLixeira(usuarioId)
                .stream().map(MediaResponse::from).toList();
    }

    @PatchMapping("/trash/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restaurar(@PathVariable Integer id, @RequestAttribute("usuarioId") Integer usuarioId) {
        arquivoService.restaurar(id, usuarioId);
    }

    @DeleteMapping("/trash")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void esvaziar(@RequestAttribute("usuarioId") Integer usuarioId) {
        arquivoService.esvaziarLixeira(usuarioId);
    }

    @PatchMapping("/media/{id}/trash")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void moverParaLixeira(@PathVariable Integer id, @RequestAttribute("usuarioId") Integer usuarioId) {
        arquivoService.moverParaLixeira(id, usuarioId);
    }
}
