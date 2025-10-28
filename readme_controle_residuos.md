# Sistema de Controle de Resíduos – API REST

## Descrição do Projeto

Este projeto é um sistema de controle de resíduos que fornece uma API REST para gerenciar materiais, pontos de coleta e recipientes. Ele permite realizar operações CRUD (Create, Read, Update, Delete) de forma organizada e automatizada, com validação do contrato da API via JSON Schema.

Além disso, o sistema possui um módulo de alertas que monitora níveis críticos nos recipientes e notifica o usuário quando algum recipiente está próximo da capacidade máxima.

---

## Estrutura do Projeto

O projeto contém as seguintes classes principais:

### 1. Recipiente
Responsável por gerenciar os recipientes de resíduos, incluindo:
- Cadastro de novos recipientes;
- Consulta por ID;
- Atualização de volume (`volumeAtual`);
- Exclusão de recipientes.

### 2. Ponto de Coleta
Gerencia os pontos de coleta de resíduos, permitindo:
- Cadastro de novos pontos;
- Consulta por ID;
- Atualização de informações;
- Exclusão de pontos de coleta.

### 3. Material
Gerencia os tipos de materiais que serão armazenados nos recipientes, com funcionalidades de:
- Cadastro de novos materiais;
- Consulta por ID;
- Atualização do nome do material;
- Exclusão de materiais.

### 4. Alerta
Classe responsável por monitorar os recipientes e gerar alertas quando:
- O volume atual ultrapassa um limite crítico definido;
- Notificações ou registros são emitidos para que a equipe de coleta possa agir rapidamente.

---

## Testes Automatizados

Os testes são implementados usando **Cucumber + RestAssured** e cobrem os seguintes cenários:

- **Cadastro (POST)**: Valida criação de registros e conformidade com o JSON Schema.
- **Consulta (GET)**: Verifica acesso a registros existentes e tratamento de erros para IDs inexistentes.
- **Atualização (PUT)**: Garante que os registros podem ser atualizados corretamente e mantém a validade do schema.
- **Exclusão (DELETE)**: Confirma que os registros podem ser removidos e valida comportamento da API para registros inexistentes.

Cada entidade (Recipiente, Ponto de Coleta, Material) possui seus próprios testes, garantindo cobertura completa da API.

---

## Requisitos

- Java 21+
- Maven ou Gradle
- Spring Boot 3+
- RestAssured
- Cucumber

---

## Como Executar

1. Clone o repositório:
```bash
git clone <URL_DO_REPOSITORIO>
cd <NOME_DO_PROJETO>
```

2. Execute a aplicação Spring Boot:
```bash
./mvnw spring-boot:run
```

3. Execute os testes automatizados:
```bash
./mvnw verify
```

Os testes vão validar todas as operações CRUD para Recipientes, Pontos de Coleta e Materiais, incluindo verificação de schemas JSON.

---

## Observações

- O módulo de alerta funciona em tempo real, monitorando os recipientes cadastrados.
- Todos os endpoints são versionados e seguem padrões RESTful.
- As validações via JSON Schema garantem que a API mantém consistência de dados para todas as operações.

