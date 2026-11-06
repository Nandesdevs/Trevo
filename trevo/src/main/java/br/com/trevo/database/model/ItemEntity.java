package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", nullable = false, length = 20)
    private TipoItem tipoItem;
}
