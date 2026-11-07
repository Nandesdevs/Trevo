package br.com.trevo.dto;

import br.com.trevo.database.model.SubAlbumEntity;

public record CategoryResponse(Integer id, String name, Integer albumId) {
    public static CategoryResponse from(SubAlbumEntity s) {
        return new CategoryResponse(s.getId(), s.getNome(), s.getAlbum().getId());
    }
}
