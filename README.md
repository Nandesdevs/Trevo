# Trevo

> Guarde suas fotos e vídeos de forma mais organizada na Trevo.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## 📋 Sobre

O Trevo é um site desenvolvido como trabalho do curso SENAI, com intuito de facilitar a organização de pastas e arquivos pessoais e profissionais, com capacidade de criar **álbuns → categorias → armazenar fotos/vídeos**.

## ✨ Funcionalidades

- ✅ Login e cadastro com JWT
- ✅ CRUD de álbuns
- ✅ Upload de fotos e vídeos
- ✅ Lixeira com restauração
- ✅ Perfil com foto de avatar
- ✅ Recuperação de senha

## 🚀 Como rodar

### Pré-requisitos
- Java 17+
- MySQL 8+

### Passo a passo

1. Clone o repositório:
   ```bash
   git clone https://github.com/Nandesdevs/Trevo.git
   cd Trevo
2. Configure o banco no src/main/resources/application.yml:

yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/trevo?createDatabaseIfNotExist=true
    username: root
    password: SUA_SENHA
3.Rode o backend:

Windows (PowerShell):
powershell
  .\mvnw.cmd spring-boot:run
  
Linux/Mac:
bash
./mvnw spring-boot:run
4.Abra o front:

Com VS Code: botão direito em index.html → Open with Live Server

Sem VS Code: python -m http.server 5500 e acesse http://localhost:5500
5.Acesse: http://localhost:5500

⚠️ Não abra o index.html com duplo clique (file://) — o navegador bloqueia por CORS. Use um servidor HTTP.

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
