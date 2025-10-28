# language: pt

Funcionalidade: Gerenciamento de Pontos de Coleta via API REST

  Como um sistema de controle de resíduos
  Eu quero interagir com a API de Pontos de Coleta
  Para garantir que as operações CRUD funcionem corretamente

  Contexto:
# Garante ambiente limpo e dependências prontas para cada cenário/
    Dado que o ambiente de teste de Ponto de Coleta está pronto

# --- CENÁRIO 1: Cadastro (POST) ---

  Cenário: Cadastrar um novo Ponto de Coleta com sucesso
    Dado que eu tenho os dados de um novo Ponto de Coleta válido
      | nome           | endereco           | bairro        | cidade        | estado | cep        |
      | Ponto Central  | Rua A, 123         | Centro        | São Paulo     | SP     | 01000-000  |
    Quando eu envio uma requisição POST para "/pontos-coleta" de Ponto de Coleta
    Então o status da resposta de Ponto de Coleta deve ser 201 Created
    E o corpo da resposta deve corresponder ao JSON Schema "pontocoleta_schema.json"
    E o campo "nome" na resposta deve ser igual a "Ponto Central"

# --- CENÁRIO 2: Consulta por ID (GET - Sucesso) ---

  Cenário: Consultar um Ponto de Coleta existente pelo ID
# Pré-condição: Cadastra um item para garantir que ele exista
    Dado que eu tenho os dados de um novo Ponto de Coleta válido
      | nome          | endereco      | bairro       | cidade         | estado | cep       |
      | Ponto Norte   | Av. B, 456    | Santana      | Campinas       | SP     | 13000-000 |
    Quando eu envio uma requisição POST para "/pontos-coleta" de Ponto de Coleta
    E eu recupero o ID do Ponto de Coleta criado no contexto

# Ação: Busca o item recém-criado
    Quando eu envio uma requisição GET para "/pontos-coleta/{id}" de Ponto de Coleta
    Então o status da resposta de Ponto de Coleta deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "pontocoleta_schema.json"
    E o campo "cidade" na resposta deve ser igual a "Campinas"


# --- CENÁRIO 3: Consulta por ID (GET - Não Encontrado) ---

  Cenário: Tentar consultar um Ponto de Coleta inexistente pelo ID
# Ação: Busca um ID que não deve existir
    Quando eu envio uma requisição GET para "/pontos-coleta/999" de Ponto de Coleta
    Então o status da resposta de Ponto de Coleta deve ser 404 Not Found
    E o corpo da resposta deve ser vazio

# --- CENÁRIO 4: Atualização (PUT) ---

  Cenário: Atualizar o nome do Ponto de Coleta existente
# Pré-condição: Cadastra um item
    Dado que eu tenho os dados de um novo Ponto de Coleta válido
      | nome          | endereco      | bairro       | cidade         | estado | cep       |
      | Ponto Leste   | R. C, 789     | Tatuapé      | São Paulo      | SP     | 03000-000 |
    Quando eu envio uma requisição POST para "/pontos-coleta" de Ponto de Coleta
    E eu recupero o ID do Ponto de Coleta criado no contexto

# Ação: Atualiza e envia o PUT
    Quando eu atualizo o nome do Ponto de Coleta para "Ponto Leste - Novo Nome"
    E eu envio uma requisição PUT para "/pontos-coleta/{id}" de Ponto de Coleta com os dados atualizados
    Então o status da resposta de Ponto de Coleta deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "pontocoleta_schema.json"
    E o campo "nome" na resposta deve ser igual a "Ponto Leste - Novo Nome"


# --- CENÁRIO 5: Deleção (DELETE) ---

  Cenário: Deletar um Ponto de Coleta existente com sucesso
# Pré-condição: Cadastra um item para deletar
    Dado que eu tenho os dados de um novo Ponto de Coleta válido
      | nome          | endereco      | bairro       | cidade         | estado | cep       |
      | Ponto Oeste   | Av. D, 101    | Barra Funda  | São Paulo      | SP     | 01100-000 |
    Quando eu envio uma requisição POST para "/pontos-coleta" de Ponto de Coleta
    E eu recupero o ID do Ponto de Coleta criado no contexto

# Ação: Deleta o item
    Quando eu envio uma requisição DELETE para "/pontos-coleta/{id}" de Ponto de Coleta
    Então o status da resposta de Ponto de Coleta deve ser 204 No Content
    E o corpo da resposta deve ser vazio

# Verificação: Tenta buscar o item deletado
    Quando eu envio uma requisição GET para "/pontos-coleta/{id}" de Ponto de Coleta
    Então o status da resposta de Ponto de Coleta deve ser 404 Not Found