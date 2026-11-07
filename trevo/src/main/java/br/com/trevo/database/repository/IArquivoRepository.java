package br.com.trevo.database.repository;

import br.com.trevo.database.model.ArquivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IArquivoRepository extends JpaRepository<ArquivoEntity, Integer> {
    List<ArquivoEntity> findBySubAlbumIdAndDeletadoFalse(Integer subAlbumId);
    List<ArquivoEntity> findByDeletadoTrueAndSubAlbumAlbumUsuarioIdOrderByDeletadoEmDesc(Integer usuarioId);
}
