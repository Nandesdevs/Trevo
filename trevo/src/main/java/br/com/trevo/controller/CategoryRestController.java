package br.com.trevo.controller;

import br.com.trevo.database.model.SubAlbumEntity;
import br.com.trevo.dto.CategoryRequest;
import br.com.trevo.dto.CategoryResponse;
import br.com.trevo.service.SubAlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryRestController {

    private final SubAlbumService subAlbumService;

    @GetMapping("/albums/{albumId}/categories")
    public List<CategoryResponse> listar(@PathVariable Integer albumId, @RequestAttribute("usuarioId") Integer usuarioId) {
        return subAlbumService.listarPorAlbum(albumId, usuarioId)
                .stream().map(CategoryResponse::from).toList();
    }

    @PostMapping("/albums/{albumId}/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse criar(@PathVariable Integer albumId, @RequestBody CategoryRequest req, @RequestAttribute("usuarioId") Integer usuarioId){
        SubAlbumEntity sub = subAlbumService.criar(albumId, req.name(), usuarioId);
        return CategoryResponse.from(sub);
    }

    @PatchMapping("/categories/{id}")
    public CategoryResponse editar(@PathVariable Integer id, @RequestBody CategoryRequest req, @RequestAttribute("usuarioId") Integer usuarioId){
        SubAlbumEntity sub = subAlbumService.editar(id, req.name(), usuarioId);
        return CategoryResponse.from(sub);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Integer id, @RequestAttribute("usuarioId") Integer usuarioId) {
        subAlbumService.deletar(id, usuarioId);
    }
}
