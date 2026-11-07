package br.com.trevo.controller;

import br.com.trevo.database.model.AlbumEntity;
import br.com.trevo.dto.AlbumRequest;
import br.com.trevo.dto.AlbumResponse;
import br.com.trevo.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/albums")
@RequiredArgsConstructor()
public class AlbumRestController {

    private final AlbumService albumService;

    @GetMapping
    public List<AlbumResponse> listar(@RequestAttribute("usuarioId") Integer usuarioId) {
        return albumService.listarPorUsuario(usuarioId)
                .stream().map(AlbumResponse::from).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse criar(@RequestBody AlbumRequest req, @RequestAttribute("usuarioId") Integer usuarioId) {
        AlbumEntity album = albumService.criar(req.name(), req.description(), usuarioId);
        return AlbumResponse.from(album);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumResponse editar(@PathVariable Integer id, @RequestBody AlbumRequest req, @RequestAttribute("usuarioId") Integer usuarioId) {
        AlbumEntity album = albumService.editar(id, req.name(), req.description(), usuarioId);
        return AlbumResponse.from(album);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id, @RequestAttribute("usuarioId") Integer usuarioId) {
        albumService.deletar(id, usuarioId);
    }


}
