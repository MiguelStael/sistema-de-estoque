# Sistema de Gestão de Estoque e Expediente - OmniRestaurante

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.4-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue?style=for-the-badge&logo=docker)](https://www.docker.com/)

Este projeto é uma solução de backend **Enterprise-Grade** para gestão de estoque e controle de expediente em restaurantes de alto fluxo. Desenvolvido com **Spring Boot 3.2** e **Java 21**, o sistema prioriza a **integridade transacional**, **observabilidade avançada** e **resiliência de infraestrutura**.

---

## Diferenciais de Nível Produção (Enterprise Hardening)

Para atingir o padrão exigido por grandes operações, o sistema implementa camadas críticas de engenharia:

### 1. Resiliência de Concorrência (Spring Retry)
Em ambientes de alta transacionalidade (ex: múltiplos garçons fechando pedidos simultaneamente), falhas de trava otimista (`OptimisticLockingFailureException`) são tratadas automaticamente. O sistema utiliza um pipeline de **Retry com Backoff**, tentando a operação novamente de forma transparente antes de retornar qualquer erro ao usuário.

### 2. Auditoria e Rastreabilidade Absoluta
- **Auditoria de Movimentação**: Cada grama de ingrediente ou ml de bebida é rastreado na tabela de auditoria transacional.
- **Auditoria de Dados Mestres (Hibernate Envers)**: Alterações em cadastros (ex: mudança de preço ou nome de produto) são versionadas. O sistema mantém um histórico completo de "quem alterou o quê e quando", essencial para auditorias fiscais e preventivas.

### 3. Observabilidade e Telemetria
Configuração nativa para monitoramento profissional:
- **Micrometer/Prometheus**: Métricas de performance de JVM e negócio exportadas via `/actuator/prometheus`.
- **Logging Estruturado**: Padrões de logs otimizados para busca e análise em ferramentas como ELK Stack ou CloudWatch.

### 4. Infraestrutura e Portabilidade (Docker)
Containerização completa via **Dockerfile multi-stage** (otimizado para produção) e **Docker Compose**, permitindo subir o ecossistema inteiro (App + PostgreSQL + Adminer) com apenas um comando.

### 5. Qualidade Assegurada (Testcontainers)
Bateria de **Testes de Integração** que utilizam containers reais do PostgreSQL para validar fluxos de ponta-a-ponta, garantindo que o sistema funcione exatamente igual em desenvolvimento e produção.

---

## Stack Tecnológica

- **Core**: Java 21, Spring Boot 3.2.4
- **Persistência**: Spring Data JPA, Hibernate Envers
- **Migração**: Flyway (Versionamento de Schema)
- **Segurança**: Spring Security & JWT
- **Resiliência**: Spring Retry
- **Infraestrutura**: Docker & Docker Compose
- **Testes**: JUnit 5, Mockito, Testcontainers

---

## Como Executar o Projeto (Zero-Config com Docker)

Esta é a forma **profissional** e recomendada. O sistema já está configurado com `Healthchecks` e `Retry Policies` para subir o ambiente completo de forma resiliente.

### Passo 1: Preparar o Ambiente
Certifique-se de que o Docker e Docker Compose estão instalados.

### Passo 2: Configurar Segredos (Opcional)
O arquivo `.env` já vem pré-configurado com valores padrão. Para produção, altere as senhas e o `JWT_SECRET` lá.

### Passo 3: Subir os Containers
```bash
docker-compose up -d --build
```
Isso iniciará:
1.  **Backend (Porta 8080)**: [http://localhost:8080](http://localhost:8080)
2.  **Adminer (Porta 8888)**: Gerenciador de banco [http://localhost:8888](http://localhost:8888)
3.  **Métricas Prometheus**: [http://localhost:8080/actuator/prometheus](http://localhost:8080/actuator/prometheus)

O sistema usará automaticamente o **Perfil de Produção** (`application-prod.properties`) dentro do Docker.

---

## Execução Local (Modo Desenvolvedor)

Caso prefira rodar sem Docker, crie um banco `sistema_estoque` e use:
```bash
./mvnw clean spring-boot:run
```

---

## Arquitetura e Boas Práticas

O projeto implementa padrões de elite da engenharia atual:
- **Resiliência Transacional**: Spring Retry para evitar falhas em alta concorrência.
- **Auditoria Absoluta**: Hibernate Envers para versionamento de dados mestres.
- **Observabilidade**: Actuator + Micrometer integrados.
- **Performance**: Otimização WebP para imagens e JVM Memory Management no Docker.

---
Este sistema foi projetado para ser **escalável**, **rastreável** e **facilmente auditável**.
