package br.com.trevo.service;

import br.com.trevo.database.model.AlbumEntity;
import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.database.repository.IAlbumRepository;
import br.com.trevo.database.repository.IUsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor()
public class AlbumService {

    private final IAlbumRepository albumRepository;
    private final IUsuarioRepository usuarioRepository;

    public List<AlbumEntity> listarPorUsuario(Integer usuarioId) {
        return albumRepository.findByUsuarioId(usuarioId);
    }

    public AlbumEntity buscar(Integer id, Integer usuarioId) {
        AlbumEntity album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        if(!album.getUsuario().getId().equals(usuarioId)){
            throw new RuntimeException("Álbum não pertence ao usuário");
        }
        return album;
    }

    @Transactional
    public AlbumEntity criar(String nome, String descricao, Integer usuarioId) {
        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        AlbumEntity album = AlbumEntity.builder()
                .nome(nome)
                .descricao(descricao)
                .usuario(usuario)
                .build();

        return albumRepository.save(album);
    }

    @Transactional
    public AlbumEntity editar(Integer id, String nome, String descricao, Integer usuarioId) {
        AlbumEntity album = buscar(id, usuarioId);
        album.setNome(nome);
        album.setDescricao(descricao);
        return albumRepository.save(album);
    }

    @Transactional
    public void deletar(Integer id, Integer usuarioId) {
        AlbumEntity album = buscar(id, usuarioId);
        albumRepository.delete(album);
    }
}
