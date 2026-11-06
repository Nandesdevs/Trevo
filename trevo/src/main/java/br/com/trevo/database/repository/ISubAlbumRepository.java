package br.com.trevo.database.repository;

import br.com.trevo.database.model.SubAlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ISubAlbumRepository extends JpaRepository<SubAlbumEntity, Integer> {
    List<SubAlbumEntity> findByAlbumId(Integer albumId);
}
