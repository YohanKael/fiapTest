# language: pt

Funcionalidade: Gerenciamento de Recipientes
  Como um sistema de controle de resíduos
  Eu quero gerenciar o status dos recipientes de coleta
  Para emitir alertas e garantir a logística

  Contexto:
    # A base de dados (Mocks) deve ser limpa antes de cada Cenário
    Dado que o ambiente de teste está pronto
    # Pre-condição: Um Ponto e um Material devem existir para o relacionamento
    E que um Ponto de Coleta com ID 1 e um Material com ID 1 estão cadastrados

  Cenário: Cadastrar, Buscar e Validar o Contrato de um Recipiente com sucesso
    Dado que eu tenho os dados de um novo recipiente válido
      | pontoColeta.idPonto | material.idMaterial | capacidadeMax | volumeAtual |
      | 1                   | 1                   | 95.0          | 20.0        |
    Quando eu envio uma requisição POST para "/recipientes"
    Então o status da resposta deve ser 201 Created
    E o corpo da resposta deve corresponder ao JSON Schema "recipiente_schema.json"
    E eu recupero o ID do recipiente criado no contexto

    Quando eu envio uma requisição GET para "/recipientes/{id}"
    Então o status da resposta deve ser 200 OK
    E o campo "volumeAtual" na resposta deve ser igual a 20.0

  Cenário: Atualizar e Deletar um Recipiente existente
    Dado que eu tenho os dados de um novo recipiente válido
      | pontoColeta.idPonto | material.idMaterial | capacidadeMax | volumeAtual |
      | 1                   | 1                   | 50.0          | 10.0        |
    Quando eu envio uma requisição POST para "/recipientes"
    E eu recupero o ID do recipiente criado no contexto

    Quando eu atualizo o volume do recipiente para 40.0
    E eu envio uma requisição PUT para "/recipientes/{id}" com os dados atualizados
    Então o status da resposta deve ser 200 OK
    E o campo "volumeAtual" na resposta deve ser igual a 40.0

    Quando eu envio uma requisição DELETE para "/recipientes/{id}"
    Então o status da resposta deve ser 204 No Content

    Quando eu envio uma requisição GET para "/recipientes/{id}"
    Então o status da resposta deve ser 404 Not Found