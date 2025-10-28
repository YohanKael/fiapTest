# language: pt

Funcionalidade: Gerenciamento de Alertas via API REST

  Como um sistema de monitoramento
  Eu quero interagir com a API de Alertas
  Para garantir que os alertas de recipiente sejam gerenciados corretamente

  Contexto:
    Dado que o ambiente de teste de Alerta está pronto
    E que um Ponto de Coleta com ID 1 está cadastrado para o Alerta

# --- CENÁRIO 1: Cadastro (POST) ---

  Cenário: Cadastrar um novo Alerta com sucesso
    Dado que eu tenho os dados de um novo Alerta válido
      | idPonto | mensagem | ultimaAtualizacao |
      | 1       | Nível Crítico - 95% | 2025-10-27 |
    Quando eu envio uma requisição POST para "/alertas" de Alerta
    Então o status da resposta de Alerta deve ser 201 Created
    E o corpo da resposta deve corresponder ao JSON Schema "alerta_schema.json"
    E o campo "mensagem" na resposta deve ser igual a "Nível Crítico - 95%"

# --- CENÁRIO 2: Consulta por ID (GET - Sucesso) ---

  Cenário: Consultar um Alerta existente pelo ID
    Dado que eu tenho os dados de um novo Alerta válido
      | idPonto | mensagem | ultimaAtualizacao |
      | 1       | Coleta Necessária | 2025-10-26 |
    Quando eu envio uma requisição POST para "/alertas" de Alerta
    E eu recupero o ID do Alerta criado no contexto

# Ação: Busca o item recém-criado
    Quando eu envio uma requisição GET para "/alertas/{id}" de Alerta
    Então o status da resposta de Alerta deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "alerta_schema.json"
    E o campo "ultimaAtualizacao" na resposta deve ser igual a "2025-10-26"


# --- CENÁRIO 3: Consulta por ID (GET - Não Encontrado) ---

  Cenário: Tentar consultar um Alerta inexistente pelo ID
# Ação: Busca um ID que não deve existir
    Quando eu envio uma requisição GET para "/alertas/9999" de Alerta
    Então o status da resposta de Alerta deve ser 404 Not Found
    E o corpo da resposta deve ser vazio

# --- CENÁRIO 4: Atualização (PUT) ---

  Cenário: Atualizar a mensagem de um Alerta existente
    Dado que eu tenho os dados de um novo Alerta válido
      | idPonto | mensagem | ultimaAtualizacao |
      | 1       | Alerta Antigo | 2025-10-25 |
    Quando eu envio uma requisição POST para "/alertas" de Alerta
    E eu recupero o ID do Alerta criado no contexto

# Ação: Atualiza e envia o PUT
    Quando eu atualizo a mensagem do Alerta para "Alerta Resolvido - OK"
    E eu envio uma requisição PUT para "/alertas/{id}" de Alerta com os dados atualizados
    Então o status da resposta de Alerta deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "alerta_schema.json"
    E o campo "mensagem" na resposta deve ser igual a "Alerta Resolvido - OK"


# --- CENÁRIO 5: Deleção (DELETE) ---

  Cenário: Deletar um Alerta existente com sucesso
    Dado que eu tenho os dados de um novo Alerta válido
      | idPonto | mensagem | ultimaAtualizacao |
      | 1       | Alerta de Remoção | 2025-10-24 |
    Quando eu envio uma requisição POST para "/alertas" de Alerta
    E eu recupero o ID do Alerta criado no contexto

# Ação: Deleta o item
    Quando eu envio uma requisição DELETE para "/alertas/{id}" de Alerta
    Então o status da resposta de Alerta deve ser 204 No Content
    E o corpo da resposta deve ser vazio

# Verificação: Tenta buscar o item deletado
    Quando eu envio uma requisição GET para "/alertas/{id}" de Alerta
    Então o status da resposta de Alerta deve ser 404 Not Found
