package br.com.trevo.service;

import br.com.trevo.database.model.AlbumEntity;
import br.com.trevo.database.model.SubAlbumEntity;
import br.com.trevo.database.repository.IAlbumRepository;
import br.com.trevo.database.repository.ISubAlbumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubAlbumService {

    private final ISubAlbumRepository subAlbumRepository;
    private final IAlbumRepository albumRepository;

    public List<SubAlbumEntity> listarPorAlbum(Integer albumId, Integer usuarioId) {
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        if (!album.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Álbum não pertence ao usuário");
        }
        return subAlbumRepository.findByAlbumId(albumId);
    }

    public SubAlbumEntity buscar(Integer id, Integer usuarioId) {
        SubAlbumEntity sub = subAlbumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        if (!sub.getAlbum().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Categoria não pertence ao usuário");
        }
        return sub;
    }

    @Transactional
    public SubAlbumEntity criar(Integer albumId, String nome, Integer usuarioId) {
        AlbumEntity album = albumRepository.findById(albumId)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        if (!album.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Álbum não pertence ao usuário");
        }

        SubAlbumEntity sub = SubAlbumEntity.builder()
                .nome(nome)
                .album(album)
                .build();

        return subAlbumRepository.save(sub);
    }

    @Transactional
    public SubAlbumEntity editar(Integer id, String nome, Integer usuarioId) {
        SubAlbumEntity sub = buscar(id, usuarioId);
        sub.setNome(nome);
        return subAlbumRepository.save(sub);
    }

    @Transactional
    public void deletar(Integer id, Integer usuarioId) {
        SubAlbumEntity sub = buscar(id, usuarioId);
        subAlbumRepository.delete(sub);
    }
}
