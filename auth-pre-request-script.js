// Script de pré-requisição para autenticação
const loginRequest = {
    url: pm.environment.get("dev_url") + '/auth/login',
    method: 'POST',
    header: {
        'Content-Type': 'application/json'
    },
    body: {
        mode: 'raw',
        raw: JSON.stringify({
            email: 'usuario@teste.com',
            senha: 'senha123'
        })
    }
};

pm.sendRequest(loginRequest, function (err, response) {
    if (err) {
        console.error(err);
    } else {
        const jsonResponse = response.json();
        pm.environment.set('dev_token', jsonResponse.token);
        console.log('Token atualizado com sucesso');
    }
}); 