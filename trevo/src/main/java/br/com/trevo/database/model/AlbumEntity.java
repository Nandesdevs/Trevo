package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "album")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "foto_capa_id")
    private ArquivoEntity fotoDaCapa;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioEntity usuario;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SubAlbumEntity> subAlbums = new ArrayList<>();
}
