# language: pt

Funcionalidade: Gerenciamento de Materiais via API REST

  Como um sistema de controle de resíduos
  Eu quero interagir com a API de Materiais
  Para garantir que as operações CRUD funcionem corretamente

  Contexto:
    Dado que o ambiente de teste de Material está pronto

# --- CENÁRIO 1: Cadastro (POST) ---

  Cenário: Cadastrar um novo Material com sucesso
    Dado que eu tenho os dados de um novo Material válido
      | nomeMaterial |
      | Plástico PET |
    Quando eu envio uma requisição POST para "/materiais" de Material
    Então o status da resposta de Material deve ser 201 Created
    E o corpo da resposta deve corresponder ao JSON Schema "material_schema.json"
    E o campo "nomeMaterial" na resposta deve ser igual a "Plástico PET"

# --- CENÁRIO 2: Consulta por ID (GET - Sucesso) ---

  Cenário: Consultar um Material existente pelo ID
# Pré-condição: Cadastra um item para garantir que ele exista
    Dado que eu tenho os dados de um novo Material válido
      | nomeMaterial |
      | Vidro Verde  |
    Quando eu envio uma requisição POST para "/materiais" de Material
    E eu recupero o ID do Material criado no contexto

# Ação: Busca o item recém-criado
    Quando eu envio uma requisição GET para "/materiais/{id}" de Material
    Então o status da resposta de Material deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "material_schema.json"
    E o campo "nomeMaterial" na resposta deve ser igual a "Vidro Verde"


# --- CENÁRIO 3: Consulta por ID (GET - Não Encontrado) ---

  Cenário: Tentar consultar um Material inexistente pelo ID
# Ação: Busca um ID que não deve existir
    Quando eu envio uma requisição GET para "/materiais/9999" de Material
    Então o status da resposta de Material deve ser 404 Not Found
    E o corpo da resposta deve ser vazio

# --- CENÁRIO 4: Atualização (PUT) ---

  Cenário: Atualizar o nome do Material existente
# Pré-condição: Cadastra um item
    Dado que eu tenho os dados de um novo Material válido
      | nomeMaterial |
      | Metal Comum  |
    Quando eu envio uma requisição POST para "/materiais" de Material
    E eu recupero o ID do Material criado no contexto

# Ação: Atualiza e envia o PUT
    Quando eu atualizo o nome do Material para "Metal Refinado"
    E eu envio uma requisição PUT para "/materiais/{id}" de Material com os dados atualizados
    Então o status da resposta de Material deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "material_schema.json"
    E o campo "nomeMaterial" na resposta deve ser igual a "Metal Refinado"


# --- CENÁRIO 5: Deleção (DELETE) ---

  Cenário: Deletar um Material existente com sucesso
# Pré-condição: Cadastra um item para deletar
    Dado que eu tenho os dados de um novo Material válido
      | nomeMaterial |
      | Papel Branco |
    Quando eu envio uma requisição POST para "/materiais" de Material
    E eu recupero o ID do Material criado no contexto

# Ação: Deleta o item
    Quando eu envio uma requisição DELETE para "/materiais/{id}" de Material
    Então o status da resposta de Material deve ser 204 No Content
    E o corpo da resposta deve ser vazio

# Verificação: Tenta buscar o item deletado
    Quando eu envio uma requisição GET para "/materiais/{id}" de Material
    Então o status da resposta de Material deve ser 404 Not Found