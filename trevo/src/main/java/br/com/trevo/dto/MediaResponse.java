package br.com.trevo.dto;

import br.com.trevo.database.model.ArquivoEntity;

public record MediaResponse(Integer id, String nome, String tipo, String url) {
    public static MediaResponse from(ArquivoEntity a) {
        return new MediaResponse(
                a.getId(),
                a.getNome(),
                a.getTipo().name(),
                a.getUrl()
        );
    }
}
