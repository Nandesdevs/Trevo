package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "usuario")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 30)
    private String nome;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(length = 30)
    private String apelido;
    private String biografia;
    @Lob
    @Column(name = "foto_de_perfil")
    private byte[] fotoDePerfil;
    @Column(nullable = false)
    private String senha;
}
