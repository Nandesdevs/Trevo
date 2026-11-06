package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

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
    @Lob
    @Column(name = "foto_da_capa")
    private byte[] fotoDaCapa;
}
