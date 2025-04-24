// Verifica se o token está presente e válido
const token = pm.environment.get('token');
if (!token) {
    console.log('Token não encontrado. Realizando login...');
    
    // Dados de login baseados no contexto da requisição
    const loginData = {
        email: pm.environment.get('email'),
        senha: pm.environment.get('senha')
    };

    // Realiza o login e salva o novo token
    pm.sendRequest({
        url: pm.environment.get('baseUrl') + '/auth/login',
        method: 'POST',
        header: {
            'Content-Type': 'application/json'
        },
        body: {
            mode: 'raw',
            raw: JSON.stringify(loginData)
        }
    }, function (err, res) {
        if (err) {
            console.error('Erro ao realizar login:', err);
            return;
        }

        const response = res.json();
        if (response.token) {
            pm.environment.set('token', response.token);
            console.log('Novo token obtido com sucesso');
        } else {
            console.error('Token não recebido na resposta');
        }
    });
}

// Adiciona o token ao header de autorização
pm.request.headers.add({
    key: 'Authorization',
    value: 'Bearer ' + pm.environment.get('token')
}); 