package com.estoque.sistema.config;

import com.estoque.sistema.model.*;
import com.estoque.sistema.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            UsuarioRepository usuarioRepository, 
            CategoriaRepository categoriaRepository,
            IngredienteRepository ingredienteRepository,
            ProdutoRepository produtoRepository,
            MesaRepository mesaRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            String adminEmail = "admin@restaurante.com";
            if (!usuarioRepository.existsByEmail(adminEmail)) {
                Usuario admin = new Usuario();
                admin.setNome("Administrador");
                admin.setEmail(adminEmail);
                admin.setSenha(passwordEncoder.encode("admin123"));
                admin.setTipoPerfil(TipoPerfil.DONO);
                usuarioRepository.save(admin);
                log.info("SEMENTE: Usuário admin criado.");
            }

            if (categoriaRepository.count() == 0) {
                Categoria lanches = new Categoria();
                lanches.setNome("Lanches");
                lanches.setDescricao("Hambúrgueres artesanais");
                categoriaRepository.save(lanches);

                Categoria bebidas = new Categoria();
                bebidas.setNome("Bebidas");
                bebidas.setDescricao("Sucos e refrigerantes");
                categoriaRepository.save(bebidas);
                log.info("SEMENTE: Categorias criadas.");
            }

            if (ingredienteRepository.count() == 0) {
                Ingrediente carne = new Ingrediente();
                carne.setNome("Carne Bovina (Blend)");
                carne.setQuantidade(new BigDecimal("10.000"));
                carne.setQuantidadeMinima(new BigDecimal("2.000"));
                carne.setUnidadeMedida(UnidadeMedida.KG);
                carne.setCustoUnitario(new BigDecimal("35.00"));
                carne.setDataValidade(LocalDate.now().plusDays(2)); // VENCENDO EM 2 DIAS!
                ingredienteRepository.save(carne);

                Ingrediente pão = new Ingrediente();
                pão.setNome("Pão Brioche");
                pão.setQuantidade(new BigDecimal("50"));
                pão.setQuantidadeMinima(new BigDecimal("10"));
                pão.setUnidadeMedida(UnidadeMedida.UNIDADE);
                pão.setCustoUnitario(new BigDecimal("1.50"));
                pão.setDataValidade(LocalDate.now().plusDays(15));
                ingredienteRepository.save(pão);
                log.info("SEMENTE: Ingredientes criados (com validade próxima).");
            }

            if (mesaRepository.count() == 0) {
                for (int i = 1; i <= 5; i++) {
                    Mesa mesa = new Mesa();
                    mesa.setNumero(i);
                    mesa.setCapacidade(4);
                    mesa.setStatus(StatusMesa.LIVRE);
                    mesaRepository.save(mesa);
                }
                log.info("SEMENTE: Mesas de 1 a 5 criadas.");
            }
        };
    }
}
