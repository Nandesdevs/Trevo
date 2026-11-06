package br.com.trevo.database.repository;

import br.com.trevo.database.model.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUsuarioRepository extends JpaRepository<UsuarioEntity, Integer> {
}
