# CoreEstoque: Gestão Inteligente de Suprimentos e Vendas

O projeto é uma solução completa de backend desenvolvida para atender às demandas de operações gastronômicas modernas. O sistema integra o controle rigoroso de estoque com uma gestão de pedidos versátil, suportando tanto o atendimento presencial quanto o serviço de delivery.

Desenvolvido com Java 21 e Spring Boot 3.2, o software prioriza a facilidade de manutenção, a segurança dos dados e a automação de processos críticos.

## Funcionalidades Principais

### 1. Gestão de Pedidos Multicanal
O sistema diferencia automaticamente o fluxo de pedidos para garantir uma operação fluida:
- Pedidos Presenciais: Controle de ocupação de mesas e encerramento de conta.
- Pedidos Delivery: Gerenciamento de dados de entrega, telefone e aplicação de taxas de serviço e frete.
- Cálculo Automático: Soma transparente de itens, taxas de serviço e taxas de entrega de acordo com as regras de negócio.

### 2. Trilha de Auditoria e Responsabilidade
Cada ação dentro do sistema é registrada com foco em transparência:
- Movimentações de estoque (entradas, saídas ou perdas) registram automaticamente o usuário responsável pela ação.
- Históricos de pedidos mantêm logs de quem alterou o status ou registrou o pagamento, garantindo rastreabilidade total.

### 3. Inteligência de Estoque e Suprimentos
O sistema não apenas armazena quantidades, mas age de forma preventiva:
- Monitoramento de Validade: Controle de datas de vencimento com alertas antecipados para evitar desperdícios.
- Alertas de Nível Crítico: Identificação visual e sistêmica de itens que estão abaixo da quantidade mínima de segurança.
- Notificações Proativas: O sistema envia e-mails automáticos diários com o resumo do estoque e disparos instantâneos caso um item essencial atinja nível zero.

### 4. Gestão de Imagens Otimizada
- Os produtos do cardápio suportam fotos que são automaticamente processadas.
- O sistema realiza o redimensionamento e a conversão para o formato WebP, garantindo que o cardápio carregue de forma rápida tanto em computadores quanto em dispositivos móveis.

### 5. Documentação Técnica Interativa
- Através da integração com o Swagger/OpenAPI, toda a interface da API está documentada de forma visual, permitindo testes rápidos e facilitando a integração com aplicativos móveis ou sites de vendas.

## Stack Tecnológica

- Linguagem: Java 21 (LTS)
- Framework: Spring Boot 3.2.4
- Banco de Dados: PostgreSQL 17
- Segurança: Spring Security e Autenticação via JWT
- Documentação: OpenAPI 3 (Swagger)
- Infraestrutura: Docker e Docker Compose
- Comunicação: Spring Mail (JavaMailSender) com suporte a e-mail assíncrono

## Como Executar o Projeto

A forma recomendada de execução é utilizando o Docker, que já configura o banco de dados e a aplicação automaticamente.

### Passo 1: Pré-requisitos
Certifique-se de ter o Docker instalado em sua máquina.

### Passo 2: Execução
Na raiz do projeto, execute o comando:
```bash
docker-compose up -d --build
```

### Passo 3: Acesso
- A API estará disponível em: http://localhost:8080
- A documentação interativa (Swagger) estará em: http://localhost:8080/swagger-ui.html

## Configuração de Notificações
O sistema está preparado para enviar alertas de e-mail. Para configurar o destino dos alertas, altere a propriedade `app.notificacao.email.destino` no arquivo `application.properties` ou através de variáveis de ambiente no arquivo Docker Compose.

---
Este sistema foi projetado para ser escalável, seguro e prático, focado na resolução de problemas reais de gestão de estoque e vendas.
