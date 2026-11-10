package br.com.trevo.service;

import br.com.trevo.database.model.ArquivoEntity;
import br.com.trevo.database.model.SubAlbumEntity;
import br.com.trevo.database.model.TipoArquivo;
import br.com.trevo.database.repository.IArquivoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArquivoService {

    private final IArquivoRepository arquivoRepository;


    public ArquivoEntity salvarUrl(String url, String nome, TipoArquivo tipo, SubAlbumEntity subAlbum) {
        ArquivoEntity arquivo = ArquivoEntity.builder()
                .nome(nome)
                .url(url)
                .tipo(tipo)
                .subAlbum(subAlbum)
                .build();

        return arquivoRepository.save(arquivo);
    }


    public ArquivoEntity buscar(Integer id) {
        return arquivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));
    }

    public List<ArquivoEntity> listarPorSubAlbum(Integer subAlbumId) {
        return arquivoRepository.findBySubAlbumIdAndDeletadoFalse(subAlbumId);
    }

    public List<ArquivoEntity> listarLixeira(Integer usuarioId) {
        return arquivoRepository
                .findByDeletadoTrueAndSubAlbumAlbumUsuarioIdOrderByDeletadoEmDesc(usuarioId);
    }

    @Transactional
    public void moverParaLixeira(Integer id, Integer usuarioId) {
        ArquivoEntity a = buscar(id);
        validarDono(a, usuarioId);
        a.setDeletado(true);
        a.setDeletadoEm(LocalDateTime.now());
        arquivoRepository.save(a);
    }

    @Transactional
    public void restaurar(Integer id, Integer usuarioId) {
        ArquivoEntity a = buscar(id);
        validarDono(a, usuarioId);
        a.setDeletado(false);
        a.setDeletadoEm(null);
        arquivoRepository.save(a);
    }

    @Transactional
    public void esvaziarLixeira(Integer usuarioId) {
        List<ArquivoEntity> lixeira = listarLixeira(usuarioId);
        arquivoRepository.deleteAll(lixeira);
        // Os arquivos ficam no Cloudinary (opcional deletar via API)
    }


    private void validarDono(ArquivoEntity a, Integer usuarioId) {
        if (a.getSubAlbum() == null
                || !a.getSubAlbum().getAlbum().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Arquivo não pertence ao usuário");
        }
    }
}