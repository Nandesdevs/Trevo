package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "comentario")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ComentarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String comentario;
    @Column(name = "data_comentario")
    private LocalDateTime dataComentario;

}
