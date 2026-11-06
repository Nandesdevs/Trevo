package br.com.trevo.service;

import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.database.repository.IUsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    @Transactional
    public UsuarioEntity cadastrar(String nome, String email, String senha) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RuntimeException("Email já cadastrado");
        }

        String hash = BCrypt.hashpw(senha, BCrypt.gensalt());

        UsuarioEntity usuario = UsuarioEntity.builder()
                .nome(nome)
                .email(email)
                .senha(hash)
                .build();

        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity login(String email, String senha) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!BCrypt.checkpw(senha, usuario.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        return usuario;
    }

    @Transactional
    public String gerarTokenRecuperacao(String email) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email não encontrado"));

        String token = jwtService.gerarReset(usuario.getId());
        return token;
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        Integer usuarioId = jwtService.extrairUsuarioIdReset(token);

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setSenha(BCrypt.hashpw(novaSenha, BCrypt.gensalt()));
        usuarioRepository.save(usuario);
    }
}