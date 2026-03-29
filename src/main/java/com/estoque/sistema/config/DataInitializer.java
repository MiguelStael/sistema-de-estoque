package com.estoque.sistema.config;

import com.estoque.sistema.model.TipoPerfil;
import com.estoque.sistema.model.Usuario;
import com.estoque.sistema.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@restaurante.com";

            if (!repository.existsByEmail(adminEmail)) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador Restaurante");
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setTipoPerfil(TipoPerfil.DONO);

                repository.save(admin);
                System.out.println("--------------------------------------------------");
                System.out.println("USUÁRIO ADMIN CRIADO: " + adminEmail);
                System.out.println("SENHA: admin123");
                System.out.println("--------------------------------------------------");
            }
        };
    }
}
