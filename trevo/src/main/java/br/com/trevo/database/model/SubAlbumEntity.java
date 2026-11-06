package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sub_album")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SubAlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "foto_capa_id", nullable = false)
    private ArquivoEntity fotoDaCapa;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private AlbumEntity album;

    @OneToMany(mappedBy = "subAlbum")
    @Builder.Default
    private List<ArquivoEntity> arquivos = new ArrayList<>();
}
