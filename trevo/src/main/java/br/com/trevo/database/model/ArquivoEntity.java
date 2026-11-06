package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

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
        private String nome;              // nome original: "treino-peito.mp4"

        @Column(nullable = false)
        private String caminho;           // onde está: "uploads/videos/uuid.mp4"

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private TipoArquivo tipo;

}
