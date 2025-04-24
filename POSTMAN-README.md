# Mini Mundo - Coleção Postman

## Configuração

1. Importe os arquivos:
   - `mini-mundo-postman-collection.json`
   - `mini-mundo-environments.json`

2. Configure os ambientes:
   - Selecione o ambiente "Development" ou "Production"
   - Os tokens JWT serão gerados automaticamente após o login

## Estrutura da Coleção

### 1. Autenticação
- Registro de usuário
- Login (gera token JWT)

### 2. Projetos
- CRUD completo
- Pesquisa por nome
- Visualização de progresso
- Gerenciamento de data de fim do projeto

### 3. Tarefas
- CRUD completo
- Pesquisa por descrição
- Filtro por status
- Gerenciamento de predecessoras
- Validação de datas dentro do período do projeto

### 4. Testes de Validação
- Validações de regras de negócio
- Testes de segurança
- Validação de datas das tarefas

## Scripts Automatizados

### Pré-requisição
- Autenticação automática
- Gerenciamento de tokens
- Configuração de variáveis de ambiente

### Testes
- Validação de status HTTP
- Verificação de estrutura de resposta
- Testes de regras de negócio
- Testes de segurança
- Validação de datas

## Exemplos de Uso

1. **Criar um novo projeto**:
   - Use a requisição "Criar Projeto"
   - Defina a data de fim do projeto
   - O ID do projeto será automaticamente salvo

2. **Criar uma tarefa**:
   - Use a requisição "Criar Tarefa"
   - Defina as datas de início e fim dentro do período do projeto
   - O ID da tarefa será automaticamente salvo

3. **Testar validações**:
   - Execute as requisições da pasta "Testes de Validação"
   - Verifique as mensagens de erro esperadas
   - Teste a validação de datas das tarefas

## Códigos de Status

- 200: Sucesso
- 201: Criado com sucesso
- 400: Erro de validação (inclui validação de datas)
- 401: Não autenticado
- 403: Não autorizado
- 404: Recurso não encontrado
- 500: Erro interno do servidor

## Dicas

1. Execute o login primeiro para gerar o token
2. Use a variável {{baseUrl}} para a URL base
3. Os IDs são salvos automaticamente nas variáveis
4. Verifique os testes após cada requisição
5. Ao criar tarefas, certifique-se que as datas estão dentro do período do projeto 