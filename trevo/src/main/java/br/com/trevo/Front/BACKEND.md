# Integração com o backend

O arquivo `script.js` foi separado em camadas de estado, API, utilitários, páginas e mutações. A camada HTTP fica concentrada no objeto `api`.

Para conectar, coloque isto **antes** de `script.js` no `index.html`:

```html
<script>window.TREVO_API_URL = "https://api.seudominio.com/api/v1";</script>
```

Depois, em `script.js`, troque `apiMode: "mock"` por `apiMode: "api"`. O endereço-base local padrão é `http://localhost:3000/api/v1`.

## Rotas que o backend deve expor

| Ação | Rota | Corpo |
| --- | --- | --- |
| Entrar | `POST /auth/login` | `{ login, password }` |
| Criar conta | `POST /auth/register` | `{ name, email, password }` |
| Esqueci a senha | `POST /auth/forgot-password` | `{ contact }` |
| Redefinir senha | `POST /auth/reset-password` | `{ token, password }` |
| Perfil | `GET` / `PATCH /me` | `{ name, bio }` para edição |
| Álbuns | `GET` / `POST /albums` | `{ name, description, emoji }` |
| Um álbum | `PATCH` / `DELETE /albums/:albumId` | dados a editar |
| Categorias | `GET` / `POST /albums/:albumId/categories` | `{ name, emoji }` |
| Uma categoria | `PATCH` / `DELETE /categories/:categoryId` | dados a editar |
| Upload | `POST /categories/:categoryId/media` | `multipart/form-data`, campo `file` |
| Comentários | `GET` / `POST /categories/:categoryId/comments` | `{ text }` |
| Comentário | `DELETE /comments/:commentId` | — |

As rotas privadas usam `Authorization: Bearer <token>`. Login e cadastro devem responder assim:

```json
{ "token": "seu-jwt", "user": { "id": 1, "name": "Ana", "email": "ana@exemplo.com" } }
```

Lembre de liberar CORS no backend para a origem do front-end, por exemplo `http://localhost:5500` em desenvolvimento.
