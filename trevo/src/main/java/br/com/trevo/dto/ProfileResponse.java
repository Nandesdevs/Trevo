package br.com.trevo.dto;

import br.com.trevo.database.model.UsuarioEntity;

public record ProfileResponse(Integer id, String name, String email, String bio, String fotoUrl) {
    public static ProfileResponse from(UsuarioEntity u) {
        String url = u.getFotoDePerfil() != null ? u.getFotoDePerfil().getUrl() : null;
        return new ProfileResponse(u.getId(), u.getNome(), u.getEmail(), u.getBiografia(), url);
    }
}