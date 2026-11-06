package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "foto_perfil_id", nullable = false)
    private ArquivoEntity fotoDePerfil;

    @Column(nullable = false)
    private String senha;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AlbumEntity> albums = new ArrayList<>();

}
