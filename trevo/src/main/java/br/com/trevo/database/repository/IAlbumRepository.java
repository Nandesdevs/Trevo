package br.com.trevo.database.repository;

import br.com.trevo.database.model.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IAlbumRepository extends JpaRepository<AlbumEntity, Integer> {
    List<AlbumEntity> findByUsuarioId(Integer usuarioId);
}
