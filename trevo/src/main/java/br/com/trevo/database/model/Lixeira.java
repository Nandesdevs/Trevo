package br.com.trevo.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "lixeira")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Lixeira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, name = "data_exclusao")
    private LocalDate dataExclusao;

}
