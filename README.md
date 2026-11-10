# Trevo

> Guarde suas fotos e vídeos de forma mais organizada na Trevo.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## 🌐 Demo

**Link:** [https://trevo-b26c.onrender.com/api/v1/](https://trevo-b26c.onrender.com/api/v1/)

> ⚠️ A primeira requisição pode demorar ~30 segundos (servidor free dorme após 15 min de inatividade).

## 📋 Sobre

O Trevo é um site desenvolvido como trabalho do curso SENAI, com intuito de facilitar a organização de pastas e arquivos pessoais e profissionais, com capacidade de criar **álbuns → categorias → armazenar fotos/vídeos**.

## ✨ Funcionalidades

- ✅ Login e cadastro com JWT
- ✅ CRUD de álbuns
- ✅ CRUD de categorias
- ✅ Upload de fotos e vídeos (armazenados no Cloudinary)
- ✅ Lixeira com restauração
- ✅ Perfil com foto de avatar
- ✅ Recuperação de senha
- ✅ Tema claro/escuro

## 🚀 Como rodar

### Pré-requisitos
- Java 17+
- MySQL 8+
- Conta no [TiDB Cloud](https://tidbcloud.com) (grátis, MySQL na nuvem)
- Conta no [Cloudinary](https://cloudinary.com) (grátis)

### Passo a passo

1. Clone o repositório:
   ```
   git clone https://github.com/Nandesdevs/Trevo.git
   cd Trevo/trevo
2. Configure o banco no TiDB Cloud:

- Acesse o TiDB Cloud

- Create Resource → Serverless → Free

- Copie: Host, Port, User, Password

-No SQL Editor, rode: CREATE DATABASE trevo;
3.Configure o src/main/resources/application.yml:

spring:
  datasource:
    url: jdbc:mysql://SEU_HOST_TIDB:4000/trevo?ssl-mode=REQUIRED
    username: SEU_USER_TIDB
    password: SUA_SENHA_TIDB

app:
  jwt:
    secret: SUA_CHAVE_SECRETA_DE_32_CARACTERES
4.Crie o upload preset no Cloudinary:

- Settings → Upload → Add upload preset

- Nome: trevo_unsigned

- Signing Mode: Unsigned

- Save
5.Configure o Cloudinary no src/main/resources/static/script.js:
```
const CONFIG = {
  ...
  cloudinaryCloudName: "SEU_CLOUD_NAME",
  cloudinaryPreset: "trevo_unsigned",
};
```
6.Rode o backend:
```
./mvnw spring-boot:run
```
Windows(PowerShell):
```
.\mvnw.cmd spring-boot:run
```

Acesse: http://localhost:8080/api/v1/
⚠️ Não abra o index.html com duplo clique (file://) — o navegador bloqueia por CORS. Use um servidor HTTP.

### 📁 Estrutura do projeto
```
Trevo/\
└── trevo/\
    ├── src/main/java/br/com/trevo/\
    │   ├── config/          # CORS, JWT filter\
    │   ├── controller/      # REST controllers\
    │   ├── database/        # Entidades e repositories\
    │   ├── dto/             # DTOs de entrada/saída\
    │   ├── handler/         # Exception handlers\
    │   └── service/         # Regras de negócio\
    ├── src/main/resources/\
    │   ├── static/          # Front (HTML, CSS, JS, assets)\
    │   └── application.yml\
    ├── Dockerfile\
    └── pom.xml
```
🛠️ Tecnologias
---------------
Backend	Frontend	Banco
-----------------------
Spring Boot 3	HTML5	MySQL 8
---------------------------
Spring Data JPA	CSS3
--------------------
Spring Security (JWT)	JavaScript (vanilla)
------------------------------------------
jBCrypt
-------
Lombok
------

### 🚢 Deploy
O projeto está hospedado no Render (free tier) com:

- Backend: Spring Boot em Docker

- Banco: TiDB Cloud (MySQL serverless)

- Storage: Cloudinary (25GB grátis)

### 👤 Autores
Nandes

- GitHub: @Nandesdevs, @iakinzz, @Ksantoss0

- Projeto: Trevo
