# 🚀 Mini Mundo - Projeto de Laboratório para Testes e Avaliações Técnicas  

## 📌 Sobre o Projeto  

O **Mini Mundo** é um projeto de laboratório destinado a testes e implementações de validação técnica para seleção de desenvolvedores. Ele permite avaliar candidatos por meio da implementação de **issues específicas**, garantindo que sigam boas práticas de desenvolvimento, versionamento e deploy contínuo.  

Cada avaliação requer que o candidato implemente uma ou mais **issues**, seguindo um fluxo padronizado que envolve:  

✅ **Uso de Conventional Commit e Gitflow** para organização do histórico de commits.  
✅ **Uso de JWT** para validação das requisições autenticadas.  
✅ **Criação de uma imagem Docker** para execução do projeto após a compilação.  
✅ **Registro da imagem no Docker Hub** para facilitar a distribuição.  
✅ **Configuração de CI/CD** para automação do build e versionamento da imagem.  

## 🛠️ Requisitos da Avaliação  

Durante a implementação, o candidato deverá:  

1️⃣ Implementar **uma ou duas issues**, conforme definido no processo de avaliação.  
2️⃣ Seguir a convenção de commits **Conventional Commit** e o fluxo **Gitflow**.  
3️⃣ Criar uma **imagem Docker** do projeto após a compilação.  
4️⃣ Registrar a imagem no **Docker Hub**.  
5️⃣ Implementar **CI/CD** para que, ao realizar um commit na branch `master` contendo uma **tag no padrão**:  

   ```regex
   /^(v|V)?(\d+\.)?(\d+\.)?(\*|\d+).?(hf\d+|Hf\d+|HF\d+)?$/
   ```  
   
   a pipeline gere e publique automaticamente uma **nova imagem Docker no Docker Hub**.  

## 🔥 Tecnologias Utilizadas  

- **Git e Gitflow** 📂 (Organização do versionamento)  
- **Docker** 🐳 (Containerização do projeto)  
- **Docker Hub** 📦 (Registro das imagens)  
- **CI/CD** ⚡ (Automação de build e deploy)  

## 🎯 Objetivo  

Este projeto simula um ambiente de desenvolvimento real, avaliando as habilidades do candidato em:  

🔹 Implementação de funcionalidades conforme **requisitos técnicos**.  
🔹 Uso correto de **versionamento e boas práticas de Git**.  
🔹 **Criação e publicação de imagens Docker** para execução do projeto.  
🔹 Automação de processos via **CI/CD** para gerar versões consistentes.  

## 🚀 Como Participar?  

Os candidatos receberão **instruções específicas** para a implementação das **issues** e deverão seguir as diretrizes acima para concluir a avaliação com sucesso.  

## 📋 Guia de Instalação e Execução

### Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

1. **Java Development Kit (JDK) 17**
   - [Download do JDK 17](https://www.oracle.com/java/technologies/downloads/#java17)
   - Verifique a instalação com: `java -version`

2. **Maven**
   - [Download do Maven](https://maven.apache.org/download.cgi)
   - Verifique a instalação com: `mvn -version`

3. **MySQL 8.0**
   - [Download do MySQL](https://dev.mysql.com/downloads/mysql/)
   - Verifique a instalação com: `mysql --version`

4. **Git**
   - [Download do Git](https://git-scm.com/downloads)
   - Verifique a instalação com: `git --version`

### Passo a Passo para Execução

#### 1. Clone do Repositório

```bash
git clone [URL_DO_REPOSITÓRIO]
cd o-mini-mundo
```

#### 2. Configuração do Banco de Dados

1. Abra o MySQL Command Line Client ou MySQL Workbench
2. Execute os seguintes comandos:

```sql
CREATE DATABASE IF NOT EXISTS o_mini_mundo;
CREATE USER IF NOT EXISTS 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON o_mini_mundo.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. Configuração do Ambiente

1. Crie um arquivo `.env` na raiz do projeto (opcional, para personalizar configurações):

```properties
JWT_SECRET=sua_chave_secreta_aqui
```

#### 4. Compilação do Projeto

```bash
mvn clean install
```

#### 5. Execução do Projeto

```bash
mvn spring-boot:run
```

O projeto estará disponível em: `http://localhost:8080/api`

### Solução de Problemas Comuns

#### Erro de Conexão com o Banco de Dados

Se encontrar erro de conexão com o MySQL:
1. Verifique se o MySQL está rodando: `sudo service mysql status` (Linux) ou verifique os Serviços do Windows
2. Confirme se as credenciais no `application.yml` correspondem às suas configurações
3. Verifique se a porta 3306 está disponível

#### Erro de Porta em Uso

Se a porta 8080 estiver em uso:
1. Altere a porta no `application.yml`:
```yaml
server:
  port: 8081  # ou outra porta disponível
```

#### Erro de Compilação

Se encontrar erros durante a compilação:
1. Verifique se está usando Java 17: `java -version`
2. Limpe o cache do Maven: `mvn clean`
3. Atualize as dependências: `mvn dependency:purge-local-repository`

### Testando a API

Após iniciar o projeto, você pode testar a API usando:

1. **Postman**:
   - Importe o arquivo `mini-mundo-postman-collection.json`
   - Importe o arquivo `mini-mundo-environments.json`
   - Selecione o ambiente apropriado
   - Execute as requisições de teste

2. **Swagger UI**:
   - Acesse: `http://localhost:8080/api/swagger-ui.html`

### Estrutura do Projeto

```
o-mini-mundo/
├── src/
│   ├── main/
│   │   ├── java/        # Código fonte
│   │   └── resources/   # Configurações
│   └── test/            # Testes
├── pom.xml              # Dependências Maven
└── README.md           # Este arquivo
```

### Suporte

Se encontrar problemas não listados aqui:
1. Verifique os logs da aplicação
2. Consulte a documentação do Spring Boot
3. Abra uma issue no repositório