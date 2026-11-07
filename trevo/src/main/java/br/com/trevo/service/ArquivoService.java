package br.com.trevo.service;

import br.com.trevo.database.model.ArquivoEntity;
import br.com.trevo.database.model.SubAlbumEntity;
import br.com.trevo.database.model.TipoArquivo;
import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.database.repository.IArquivoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArquivoService {

    @Value("${app.trevo.uploads.dir}")
    private String uploadDir;

    private final IArquivoRepository arquivoRepository;

    public ArquivoEntity salvar(MultipartFile file, TipoArquivo tipo, SubAlbumEntity subAlbum) {
        try {
            String original = file.getOriginalFilename();
            String extensao = original != null && original.contains(".")
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            String nomeUnico = UUID.randomUUID() + extensao;

            Path destino = Path.of(uploadDir, nomeUnico).toAbsolutePath();
            Files.createDirectories(destino.getParent());
            file.transferTo(destino.toFile());

            ArquivoEntity arquivo = ArquivoEntity.builder()
                    .nome(original)
                    .caminho(destino.toString())
                    .tipo(tipo)
                    .subAlbum(subAlbum)
                    .build();

            return arquivoRepository.save(arquivo);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar arquivo", e);
        }
    }

    public ArquivoEntity buscar(Integer id) {
        return arquivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));
    }

    public List<ArquivoEntity> listarPorSubAlbum(Integer subAlbumId) {
        return arquivoRepository.findBySubAlbumIdAndDeletadoFalse(subAlbumId);
    }

    public List<ArquivoEntity> listarLixeira(Integer usuarioId) {
        return arquivoRepository.findByDeletadoTrueAndSubAlbumAlbumUsuarioIdOrderByDeletadoEmDesc(usuarioId);
    }

    @Transactional
    public void restaurar(Integer id, Integer usuarioId) {
        ArquivoEntity a = buscar(id);
        if (!a.getSubAlbum().getAlbum().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Arquivo não pertence ao usuário");
        }
        a.setDeletado(false);
        a.setDeletadoEm(null);
        arquivoRepository.save(a);
    }

    @Transactional
    public void esvaziarLixeira(Integer usuarioId) {
        List<ArquivoEntity> lixeira = listarLixeira(usuarioId);
        List<String> caminhos = lixeira.stream().map(ArquivoEntity::getCaminho).toList();
        arquivoRepository.deleteAll(lixeira);
        caminhos.forEach(c -> {
            try { Files.deleteIfExists(Path.of(c)); }
            catch (IOException e) { /* log */ }
        });
    }

    @Transactional
    public void moverParaLixeira(Integer id, Integer usuarioId) {
        ArquivoEntity a = buscar(id);
        if (!a.getSubAlbum().getAlbum().getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("Arquivo não pertence ao usuário");
        }
        a.setDeletado(true);
        a.setDeletadoEm(LocalDateTime.now());
        arquivoRepository.save(a);
    }

    public ArquivoEntity salvarAvatar(MultipartFile file, UsuarioEntity usuario) {
        try {
            String original = file.getOriginalFilename();
            String extensao = original != null && original.contains(".")
                    ? original.substring(original.lastIndexOf('.')) : "";
            String nomeUnico = UUID.randomUUID() + extensao;

            Path destino = Path.of(uploadDir, nomeUnico).toAbsolutePath();
            Files.createDirectories(destino.getParent());
            file.transferTo(destino.toFile());

            ArquivoEntity arquivo = ArquivoEntity.builder()
                    .nome(original)
                    .caminho(destino.toString())
                    .tipo(TipoArquivo.FOTO_PERFIL)
                    .subAlbum(null)   // perfil não tem subálbum
                    .build();

            return arquivoRepository.save(arquivo);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao salvar avatar: " + e.getMessage(), e);
        }
    }
}
