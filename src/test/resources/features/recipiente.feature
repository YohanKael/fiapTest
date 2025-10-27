# language: pt

Funcionalidade: Gerenciamento de Recipientes via API REST

  Como um sistema de controle de resíduos
  Eu quero interagir com a API de Recipientes
  Para garantir que as operações CRUD funcionem corretamente

  Contexto:
    # Garante ambiente limpo e dependências prontas para cada cenário
    Dado que o ambiente de teste está pronto
    E que um Ponto de Coleta com ID 1 e um Material com ID 1 estão cadastrados

  # --- CENÁRIO 1: Cadastro (POST) ---
  Cenário: Cadastrar um novo Recipiente com sucesso
    Dado que eu tenho os dados de um novo recipiente válido
      | idPontoColeta | idMaterial | capacidadeMax | volumeAtual |
      | 1             | 1          | 95,0          | 20,0        |
    Quando eu envio uma requisição POST para "/recipientes"
    Então o status da resposta deve ser 201 Created
    E o corpo da resposta deve corresponder ao JSON Schema "recipiente_schema.json"
    E o campo "volumeAtual" na resposta deve ser igual a 20,0

  # --- CENÁRIO 2: Consulta por ID (GET - Sucesso) ---
  Cenário: Consultar um Recipiente existente pelo ID
    # Pré-condição: Cadastra um item para garantir que ele exista
    Dado que eu tenho os dados de um novo recipiente válido
      | idPontoColeta | idMaterial | capacidadeMax | volumeAtual |
      | 1             | 1          | 70,0          | 35,0        |
    Quando eu envio uma requisição POST para "/recipientes"
    E eu recupero o ID do recipiente criado no contexto

    # Ação: Busca o item recém-criado
    Quando eu envio uma requisição GET para "/recipientes/{id}"
    Então o status da resposta deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "recipiente_schema.json"
    E o campo "capacidadeMax" na resposta deve ser igual a 70,0

  # --- CENÁRIO 3: Consulta por ID (GET - Não Encontrado) ---
  Cenário: Tentar consultar um Recipiente inexistente pelo ID
    # Ação: Busca um ID que não deve existir (assume que 999 não foi criado)
    Quando eu envio uma requisição GET para "/recipientes/999"
    Então o status da resposta deve ser 404 Not Found
    E o corpo da resposta deve ser vazio

  # --- CENÁRIO 4: Atualização (PUT) ---
  Cenário: Atualizar o volume de um Recipiente existente
    # Pré-condição: Cadastra um item
    Dado que eu tenho os dados de um novo recipiente válido
      | idPontoColeta | idMaterial | capacidadeMax | volumeAtual |
      | 1             | 1          | 50,0          | 10,0        |
    Quando eu envio uma requisição POST para "/recipientes"
    E eu recupero o ID do recipiente criado no contexto

    # Ação: Atualiza e envia o PUT
    Quando eu atualizo o volume do recipiente para 45,0
    E eu envio uma requisição PUT para "/recipientes/{id}" com os dados atualizados
    Então o status da resposta deve ser 200 OK
    E o corpo da resposta deve corresponder ao JSON Schema "recipiente_schema.json"
    E o campo "volumeAtual" na resposta deve ser igual a 45,0

  # --- CENÁRIO 5: Deleção (DELETE) ---
  Cenário: Deletar um Recipiente existente com sucesso
    # Pré-condição: Cadastra um item para deletar
    Dado que eu tenho os dados de um novo recipiente válido
      | idPontoColeta | idMaterial | capacidadeMax | volumeAtual |
      | 1             | 1          | 30,0          | 5,0         |
    Quando eu envio uma requisição POST para "/recipientes"
    E eu recupero o ID do recipiente criado no contexto

    # Ação: Deleta o item
    Quando eu envio uma requisição DELETE para "/recipientes/{id}"
    Então o status da resposta deve ser 204 No Content
    E o corpo da resposta deve ser vazio

    # Verificação: Tenta buscar o item deletado
    Quando eu envio uma requisição GET para "/recipientes/{id}"
    Então o status da resposta deve ser 404 Not Found