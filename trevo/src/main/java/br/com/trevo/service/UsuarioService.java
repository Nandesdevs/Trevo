package br.com.trevo.service;

import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.database.repository.IUsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final IUsuarioRepository usuarioRepository;

    public UsuarioEntity buscar(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    @Transactional
    public UsuarioEntity atualizar(Integer id, String nome, String bio) {
        UsuarioEntity usuario = buscar(id);
        if (nome != null && !nome.isBlank()) usuario.setNome(nome);
        if (bio != null) usuario.setBiografia(bio);
        return usuarioRepository.save(usuario);
    }
}
