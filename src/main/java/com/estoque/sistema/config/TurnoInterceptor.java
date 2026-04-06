package com.estoque.sistema.config;

import com.estoque.sistema.repository.TurnoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class TurnoInterceptor implements HandlerInterceptor {

    private final TurnoRepository turnoRepository;

    public TurnoInterceptor(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) throws IOException {

        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        boolean isPostPedido = method != null && method.equalsIgnoreCase("POST")
                && uri != null && uri.startsWith("/pedidos");

        if (isPostPedido && !turnoRepository.existsByAbertoTrue()) {
            response.setStatus(HttpStatus.CONFLICT.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"statusHttp\":409,\"mensagem\":\"O restaurante está fechado. Abra um turno antes de registrar pedidos.\"}"
            );
            return false;
        }

        return true;
    }
}
