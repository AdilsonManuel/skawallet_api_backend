### 🛡️ **SKA Wallet - Carteira Digital Segura**  

Bem-vindo ao repositório da **SKA Wallet**! 🚀  
Uma solução de carteira digital desenvolvida em **Spring Boot** para facilitar pagamentos digitais, consulta de saldo e muito mais, com foco em segurança, escalabilidade e integração com serviços modernos. 💳

---

## 📋 **Funcionalidades Principais**
- **🛡️ Autenticação e Autorização:**
  - JWT e OAuth2 para login seguro e proteção dos endpoints.
- **💰 Microserviço de Pagamentos Digitais:**
  - Gerenciamento de transações financeiras, como depósitos, retiradas e transferências.
- **📊 Histórico de Transações:**
  - Armazenamento detalhado com suporte a eventos via Kafka.
- **🔔 Notificações em Tempo Real:**
  - Utilização de WebSockets e Firebase Cloud Messaging.
- **📱 Otimização para Android:**
  - Backend preparado para integração perfeita com aplicativos móveis.

---

## 🛠️ **Tecnologias Utilizadas**
- **Backend:**
  - Java + Spring Boot 🟢
  - Spring Security 🔐
  - Spring Data JPA 🗂️
- **Banco de Dados:**
  - MySQL 🐬
- **Mensageria:**
  - Kafka 🟠
- **Notificações:**
  - WebSockets 📡 + Firebase 🔥

---

## 🚀 **Como Configurar o Projeto**

### 🖥️ **Pré-requisitos**
Certifique-se de ter instalado:
- **JDK 17+** ☕
- **Maven** 📦
- **MySQL Server** 🐬
- **Kafka** (opcional) 🟠

### 📂 **Clonando o Repositório**
```bash
git clone https://github.com/seu-usuario/ska-wallet.git
cd ska-wallet
```

### ⚙️ **Configuração do Banco de Dados**
Atualize o arquivo `application.properties` com as credenciais do seu banco de dados:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ska_wallet
spring.datasource.username=root
spring.datasource.password=senha
```

### ▶️ **Executando o Projeto**
```bash
mvn spring-boot:run
```
O backend estará disponível em `http://localhost:8080`.

---

## 📡 **Endpoints Principais**

### 🔑 **Autenticação**
- **POST** `/auth/login`: Autenticar e obter token JWT.
- **POST** `/auth/register`: Registrar novo usuário.

### 💳 **Pagamentos**
- **POST** `/transactions`: Realizar uma transação.
- **GET** `/transactions`: Listar transações do usuário.

### 🔔 **Notificações**
- **GET** `/notifications`: Consultar notificações do usuário.

Para mais detalhes, confira a documentação completa da API disponível em **Swagger**:  
`http://localhost:8080/swagger-ui.html`

---

## 🧪 **Testes com Postman**
1. Use o arquivo [Postman Collection](postman_collection.json) disponível no repositório.
2. Configure a variável `{{jwt_token}}` com o token JWT obtido no login.

---

## 🤝 **Contribuições**
Contribuições são bem-vindas! 💡  
Sinta-se à vontade para abrir **issues** ou enviar um **pull request**. 

---

## 📜 **Licença**
Este projeto é licenciado sob a **MIT License**. 📝  

---

### **💻 Desenvolvido por AZM Soluções Integradas**  
Com ❤️ e 🚀 para simplificar sua vida financeira! 
