package com.estoque.sistema.autenticacaojwt;

import com.estoque.sistema.model.Usuario;
import com.estoque.sistema.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioService = usuarioService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<Map<String, String>> cadastrar(@RequestBody Map<String, String> body) {
        try {
            usuarioService.cadastrarCliente(
                    body.get("nome"),
                    body.get("email"),
                    body.get("senha")
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("mensagem", "Cadastro realizado com sucesso!"));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(body.get("email"), body.get("senha"))
            );
            Usuario usuario = usuarioRepository.findByEmail(body.get("email"))
                    .orElseThrow();
            String token = jwtService.gerarToken(usuario.getEmail(), usuario.getTipoPerfil().name());
            return ResponseEntity.ok(Map.of("token", token, "perfil", usuario.getTipoPerfil().name()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("erro", "Email ou senha inválidos."));
        }
    }
}
