package br.com.trevo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioCadastroDto {

    @NotBlank
    private String nome;
    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 6)
    private String senha;
}
