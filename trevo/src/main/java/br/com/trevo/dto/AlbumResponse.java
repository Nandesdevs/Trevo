package br.com.trevo.dto;

import br.com.trevo.database.model.AlbumEntity;

public record AlbumResponse(Integer id, String name, String description) {
    public static AlbumResponse from(AlbumEntity a) {
        return new AlbumResponse(a.getId(), a.getNome(), a.getDescricao());
    }
}
