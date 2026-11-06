package br.com.trevo.service;

import br.com.trevo.database.model.UsuarioEntity;
import br.com.trevo.database.repository.IUsuarioRepository;
import br.com.trevo.dto.UsuarioCadastroDto;
import br.com.trevo.dto.UsuarioLoginDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final IUsuarioRepository usuarioRepository;

    @Transactional
    public void cadastroUsuario(UsuarioCadastroDto usuarioCadastroDto) throws BadRequestException {
        UsuarioEntity usuario = usuarioRepository.findByEmail(usuarioCadastroDto.getEmail())
                .orElse(null);

        if (usuario != null) {
            throw new BadRequestException("Usuario já cadastrado com este email");
        }

        String hash = BCrypt.hashpw(usuarioCadastroDto.getSenha(), BCrypt.gensalt());

        usuarioRepository.save(UsuarioEntity.builder()
                        .nome(usuarioCadastroDto.getNome())
                        .email(usuarioCadastroDto.getEmail())
                        .senha(hash)
                .build());
    }

    public UsuarioEntity login(UsuarioLoginDto usuarioLoginDto) throws BadRequestException{
        UsuarioEntity usuario = usuarioRepository.findByEmail(usuarioLoginDto.getEmail())
                .orElseThrow(() -> new BadRequestException("Email ou senha inválidos"));

        if (!BCrypt.checkpw(usuario.getSenha(), usuario.getSenha())) {
            throw new BadRequestException("Email ou senha inválidos");
        }

        return usuario;
    }
}
