package com.estoque.sistema.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailRequestDTO {

    @NotBlank(message = "O destinatário é obrigatório")
    @Email(message = "Formato de e-mail inválido")
    private String para;

    @NotBlank(message = "O assunto é obrigatório")
    private String assunto;

    @NotBlank(message = "O conteúdo do e-mail é obrigatório")
    private String conteudo;
}
