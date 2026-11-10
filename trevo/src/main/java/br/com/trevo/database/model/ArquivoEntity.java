package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "arquivo")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ArquivoEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        @Column(nullable = false)
        private String nome;

        @Column(nullable = false)
        private String url;

        //      lixeira abaixo

        @Column(nullable = false)
        @Builder.Default
        private boolean deletado = false;       // marca se o arquivo está na lixeira. false = ativo, true = na lixeira.

        private LocalDateTime deletadoEm;       // guarda quando foi pra lixeira. Serve pra ordenar a lixeira

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private TipoArquivo tipo;

        @ManyToOne()
        @JoinColumn(name = "sub_album_id")
        private SubAlbumEntity subAlbum;

}
