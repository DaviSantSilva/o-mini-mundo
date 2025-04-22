// Testes para autenticação
pm.test("Login bem-sucedido", function () {
    pm.response.to.have.status(200);
    pm.response.to.be.json;
    pm.expect(pm.response.json()).to.have.property('token');
});

// Testes para projetos
pm.test("Criar projeto", function () {
    pm.response.to.have.status(201);
    pm.response.to.be.json;
    const response = pm.response.json();
    pm.expect(response).to.have.property('id');
    pm.expect(response).to.have.property('nome');
    pm.expect(response).to.have.property('status');
    pm.environment.set('projetoId', response.id);
});

pm.test("Listar projetos", function () {
    pm.response.to.have.status(200);
    pm.response.to.be.json;
    pm.expect(pm.response.json()).to.be.an('array');
});

// Testes para tarefas
pm.test("Criar tarefa", function () {
    pm.response.to.have.status(201);
    pm.response.to.be.json;
    const response = pm.response.json();
    pm.expect(response).to.have.property('id');
    pm.expect(response).to.have.property('descricao');
    pm.expect(response).to.have.property('status');
    pm.environment.set('tarefaId', response.id);
});

// Testes de validação de negócio
pm.test("Validar erro de nome duplicado", function () {
    pm.response.to.have.status(400);
    pm.response.to.be.json;
    const response = pm.response.json();
    pm.expect(response).to.have.property('message');
    pm.expect(response.message).to.include('já existe');
});

pm.test("Validar erro de exclusão de projeto com tarefas", function () {
    pm.response.to.have.status(400);
    pm.response.to.be.json;
    const response = pm.response.json();
    pm.expect(response).to.have.property('message');
    pm.expect(response.message).to.include('possui tarefas');
});

// Testes de segurança
pm.test("Validar acesso não autorizado", function () {
    pm.response.to.have.status(403);
    pm.response.to.be.json;
    const response = pm.response.json();
    pm.expect(response).to.have.property('message');
    pm.expect(response.message).to.include('não autorizado');
}); 