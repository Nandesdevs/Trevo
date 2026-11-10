const CONFIG = {
  apiMode: "api",
  apiBaseUrl: window.TREVO_API_URL || "/api/v1",
  storagePrefix: "trevo-",
  cloudinaryCloudName: "plhhyo3x",
  cloudinaryPreset: "trevo_unsigned",
};

const seed = {
  user: null,
  albums: [{ id: 1, name: "Boas-vindas", description: "Seu primeiro álbum no Trevo", emoji: "🌿" }],
  categories: [{ id: 1, name: "Momentos especiais", emoji: "📸" }],
  trash: [],
  comments: [
    { id: 1, author: "Maria", text: "Que lembrança linda! 💚" },
    { id: 2, author: "João", text: "Adorei essa foto." },
  ],
};

// Storage ---------------------------------------------------------------------
const storage = {
  get(key, fallback) {
    try {
      const raw = localStorage.getItem(`${CONFIG.storagePrefix}${key}`);
      return raw === null ? fallback : JSON.parse(raw);
    } catch { return fallback; }
  },
  set(key, value) { localStorage.setItem(`${CONFIG.storagePrefix}${key}`, JSON.stringify(value)); },
  remove(key) { localStorage.removeItem(`${CONFIG.storagePrefix}${key}`); },
};

const state = {
  user: storage.get("user", seed.user),
  albums: storage.get("albums", seed.albums),
  categories: storage.get("categories", seed.categories),
  trash: storage.get("trash", seed.trash),
  comments: storage.get("comments", seed.comments),
  theme: localStorage.getItem("trevo-theme") || "light",
};

function save() {
  ["user", "albums", "categories", "trash", "comments"].forEach((key) => storage.set(key, state[key]));
}

// API -------------------------------------------------------------------------
// Centralize aqui todas as chamadas HTTP; a lista completa está em BACKEND.md.
const api = {
  enabled: CONFIG.apiMode === "api",
  async request(path, { method = "GET", body, headers = {} } = {}) {
    const token = storage.get("token", null);
    const response = await fetch(`${CONFIG.apiBaseUrl}${path}`, {
      method,
      headers: { "Content-Type": "application/json", ...(token && { Authorization: `Bearer ${token}` }), ...headers },
      ...(body && { body: JSON.stringify(body) }),
    });
    const data = response.status === 204 ? null : await response.json().catch(() => null);
    if (!response.ok) throw new Error(data?.message || "Não foi possível concluir a solicitação.");
    return data;
  },
  auth: {
    login: (login, password) => api.request("/auth/login", { method: "POST", body: { login, password } }),
    register: (name, email, password) => api.request("/auth/register", { method: "POST", body: { name, email, password } }),
    forgot: (contact) => api.request("/auth/forgot-password", { method: "POST", body: { contact } }),
    reset: (token, password) => api.request("/auth/reset-password", { method: "POST", body: { token, password } }),
  },
  albums: {
    list: () => api.request("/albums"),
    create: (body) => api.request("/albums", { method: "POST", body }),
    update: (id, body) => api.request(`/albums/${id}`, { method: "PATCH", body }),
    remove: (id) => api.request(`/albums/${id}`, { method: "DELETE" }),
  },
  categories: {
    list: (albumId) => api.request(`/albums/${albumId}/categories`),
    create: (albumId, body) => api.request(`/albums/${albumId}/categories`, { method: "POST", body }),
    update: (id, body) => api.request(`/categories/${id}`, { method: "PATCH", body }),
    remove: (id) => api.request(`/categories/${id}`, { method: "DELETE" }),
  },
  comments: {
    list: (categoryId) => api.request(`/categories/${categoryId}/comments`),
    create: (categoryId, text) => api.request(`/categories/${categoryId}/comments`, { method: "POST", body: { text } }),
    remove: (id) => api.request(`/comments/${id}`, { method: "DELETE" }),
  },
  profile: { get: () => api.request("/me"), update: (body) => api.request("/me", { method: "PATCH", body }) },
};

// UI helpers ------------------------------------------------------------------
const app = document.querySelector("#app");
const escapeHtml = (value) => String(value).replace(/[&<>"']/g, (char) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;", "'": "&#39;" })[char]);
const route = () => location.hash.slice(1) || "/login";
const go = (path) => { location.hash = path; };
const closeModal = () => document.querySelector("#modal-backdrop")?.remove();

function toast(message) {
  const element = document.querySelector("#toast");
  element.textContent = message;
  element.classList.add("show");
  setTimeout(() => element.classList.remove("show"), 2600);
}

function clover(className = "") {
  const asset = state.theme === "dark" ? "assets/trevo-escuro-transparente.png" : "assets/trevo-claro-transparente.png";
  return `<img class="clover-icon ${className}" src="${asset}" alt="" aria-hidden="true">`;
}

function shell(content) {
  app.innerHTML = `<nav class="topbar">
    <button class="icon-btn back-btn" aria-label="Voltar" onclick="back()">←</button>
    <a class="brand" href="#/home">${clover("brand-clover")} Trevo</a>
    <input class="search" placeholder="Pesquisar álbuns, fotos ou categorias" aria-label="Pesquisar">
    <div class="nav-actions"><button class="icon-btn" onclick="go('/trash')">🗑</button><button class="icon-btn" onclick="go('/profile')">♙</button><button class="icon-btn" onclick="toggleTheme()">${state.theme === "dark" ? "☀" : "☾"}</button></div>
  </nav>${content}`;
}

function authLayout(title, fields, action, footer, art = '<img class="auth-clover" src="assets/logo-trevo-transparente.png" alt="Símbolo do Trevo">') {
  app.innerHTML = `<section class="auth-page"><div class="auth-box"><div class="auth-art">${art}</div><form class="auth-form" id="auth-form"><h1>${title}</h1>${fields}<p id="form-error" class="error" hidden></p><button class="btn" type="submit">${action}</button><p class="auth-links">${footer}</p></form></div></section>`;
}

function showFormError(message) {
  const error = document.querySelector("#form-error");
  error.textContent = message;
  error.hidden = false;
}

// Auth pages ------------------------------------------------------------------
function login() {
  authLayout("Bem-vindo ao Trevo", '<label class="field">E-mail<input required name="login" type="email" placeholder="Digite seu e-mail"></label><label class="field">Senha<input required name="password" type="password" placeholder="Digite sua senha"></label><a href="#/forgot">Esqueceu a senha?</a>', "Entrar", 'Não possui conta? <a href="#/signup">Cadastre-se gratuitamente</a>', '<img class="login-logo" src="assets/logo-login-transparente.png" alt="Logo do Trevo">');
  document.querySelector("#auth-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
      const result = api.enabled ? await api.auth.login(form.login.value, form.password.value) : { user: { name: "Visitante", email: form.login.value } };
      if (result.token) storage.set("token", result.token);
      state.user = result.user || result;
      save(); go("/home");
    } catch (error) { showFormError(error.message); }
  };
}

function signup() {
  authLayout("Crie sua conta", '<label class="field">Nome<input required name="name" placeholder="Seu nome"></label><label class="field">E-mail<input required name="email" type="email" placeholder="voce@email.com"></label><label class="field">Senha<input required minlength="6" name="password" type="password" placeholder="Mínimo de 6 caracteres"></label><label class="field">Confirmar senha<input required name="confirm" type="password" placeholder="Repita a senha"></label>', "Cadastrar", 'Já tem conta? <a href="#/login">Entrar</a>');
  document.querySelector("#auth-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    if (form.password.value !== form.confirm.value) return showFormError("As senhas precisam ser iguais.");
    try {
      const result = api.enabled ? await api.auth.register(form.name.value, form.email.value, form.password.value) : { user: { name: form.name.value, email: form.email.value } };
      if (result.token) storage.set("token", result.token);
      state.user = result.user || result;
      save(); toast("Conta criada com sucesso!"); go("/home");
    } catch (error) { showFormError(error.message); }
  };
}

function forgot() {
  authLayout("Recuperar senha",
      '<label class="field">E-mail<input required name="contact" type="email" placeholder="voce@email.com"></label><p>Enviaremos um código para confirmar sua identidade.</p>',
      "Enviar código",
      '<a href="#/login">Voltar ao login</a>');

  document.querySelector("#auth-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
      const result = await api.auth.forgot(form.contact.value);
      if (result.token) storage.set("resetToken", result.token);
      toast("Código enviado!");
      go("/reset");
    } catch (error) {
      showFormError(error.message);
    }
  };
}

function reset() {
  authLayout("Criar nova senha",
      '<label class="field">Nova senha<input required minlength="6" name="password" type="password"></label><label class="field">Confirmar senha<input required name="confirm" type="password"></label>',
      "Alterar senha",
      "");

  document.querySelector("#auth-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;

    if (form.password.value !== form.confirm.value) {
      return showFormError("As senhas precisam ser iguais.");
    }

    try {
      const token = storage.get("resetToken", "");
      await api.request("/auth/reset-password", {
        method: "POST",
        body: { token, password: form.password.value }
      });
      storage.remove("resetToken");
      toast("Senha alterada com sucesso!");
      go("/login");
    } catch (error) {
      showFormError(error.message);
    }
  };
}

// Application pages -----------------------------------------------------------
function cover(item) {
  return item.photo ? `<img src="${escapeHtml(item.photo)}" alt="Capa do álbum ${escapeHtml(item.name)}">` : state.theme === "light" ? '<img class="album-clover" src="assets/trevo-home-claro.png" alt="Trevo de quatro folhas">' : clover("album-clover");
}

function albumCard(item) {
  return `<article class="card"><div class="cover">${cover(item)}</div><div class="card-body"><h3>${escapeHtml(item.name)}</h3><p>${escapeHtml(item.description || "Sem descrição")}</p><div class="card-footer"><button class="btn secondary" onclick="go('/album/${item.id}')">Abrir</button><button class="menu-btn" onclick="openAlbumMenu(${item.id})" aria-label="Opções do álbum">•••</button></div></div></article>`;
}

async function home() {
  let albums = [];
  try {
    albums = await api.albums.list();
    state.albums = albums;
    save();
  } catch (e) { toast(e.message); }

  shell(`<section class="page">
    <div class="page-head">
      <div><h1>Página inicial</h1><p>Organize suas memórias no seu próprio Trevo.</p></div>
      <button class="btn" onclick="openModal('album')">＋ Criar álbum</button>
    </div>
    <div class="grid">
      ${albums.map(a => `
        <article class="card">
          <div class="cover">🌿</div>
          <div class="card-body">
            <h3>${escapeHtml(a.name)}</h3>
            <p>${escapeHtml(a.description || "Sem descrição")}</p>
            <div class="card-footer">
              <button class="btn secondary" onclick="go('/album/${a.id}')">Abrir</button>
              <button class="menu-btn" onclick="openAlbumMenu(${a.id}, '${escapeHtml(a.name)}')">•••</button>
            </div>
          </div>
        </article>
      `).join("")}
    </div>
  </section>`);
}

async function album() {
  const albumId = Number(route().split("/").pop());
  const selected = state.albums.find(item => item.id === albumId) || { name: "Álbum", description: "" };

  let categorias = [];
  try {
    categorias = await api.categories.list(albumId);
  } catch (e) { toast(e.message); }

  shell(`<section class="page">
    <div class="page-head">
      <div><h1>${escapeHtml(selected.name)}</h1><p>${escapeHtml(selected.description || "")}</p></div>
      <button class="btn" onclick="openModal('category', null, ${albumId})">＋ Categoria</button>
    </div>
    <div class="grid">
      ${categorias.length ? categorias.map(c => `
        <article class="card">
          <div class="cover">📁</div>
          <div class="card-body">
            <h3>${escapeHtml(c.name)}</h3>
            <div class="card-footer">
              <button class="btn secondary" onclick="go('/comments/${c.id}')">Abrir</button>
              <button class="menu-btn" onclick="openCategoryMenu(${c.id}, '${escapeHtml(c.name)}')">•••</button>
            </div>
          </div>
        </article>
      `).join("") : '<p class="no-comments">Nenhuma categoria ainda.</p>'}
    </div>
  </section>`);
}

function abrirLightbox(url, tipo) {
  document.body.insertAdjacentHTML("beforeend", `
    <div class="modal-backdrop" id="modal-backdrop" onclick="closeModal()" style="background:#000c">
      <div style="max-width:95vw;max-height:95vh;display:flex;align-items:center;justify-content:center;position:relative">
        ${tipo === "VIDEO"
      ? `<video controls autoplay src="${url}" style="max-width:95vw;max-height:90vh;border-radius:8px;background:#000"></video>`
      : `<img src="${url}" style="max-width:95vw;max-height:90vh;border-radius:8px;background:#000;min-width:200px;min-height:200px;object-fit:contain">`}
        <button class="icon-btn" onclick="closeModal()" style="position:absolute;top:-40px;right:0;color:#fff;font-size:32px">×</button>
      </div>
    </div>
  `);
}

async function comments() {
  const categoryId = Number(route().split("/").pop());

  let midias = [];
  try {
    midias = await api.request(`/categories/${categoryId}/media`);
  } catch (e) { toast(e.message); }

  shell(`<section class="page">
    <div class="page-head">
      <h1>Momentos especiais</h1>
      <button class="btn" onclick="openModal('photo', null, ${categoryId})">＋ Adicionar fotos e vídeos</button>
    </div>
    <div class="comment-layout">
      <div class="content-preview">
        <div class="grid">
          ${midias.length ? midias.map(m => `
            <article class="card">
              ${m.tipo === "VIDEO"
      ? `<div onclick="abrirLightbox('${m.url}', 'VIDEO')" style="cursor:pointer">
       <video src="${m.url}" style="width:100%;height:145px;object-fit:cover"></video>
     </div>`
      : `<div class="cover" style="cursor:pointer" onclick="abrirLightbox('${m.url}', 'FOTO')">
       <img src="${m.url}" alt="">
     </div>`}
              <div class="card-body">
                <h3>${escapeHtml(m.nome)}</h3>
                <div class="card-footer">
                  <button class="menu-btn" onclick="deleteMedia(${m.id})">🗑</button>
                </div>
              </div>
            </article>
          `).join("") : '<p class="no-comments">Nenhuma mídia ainda.</p>'}
        </div>
      </div>
    </div>
  </section>`);
}

async function profile() {
  let user = { name: "Visitante", email: "", bio: "" };
  try {
    user = await api.request("/me");
  } catch (e) { toast(e.message); }

  shell(`<section class="page">
    <div class="page-head"><h1>Perfil</h1></div>
    <form class="profile" id="profile-form">
      <div class="profile-main">
        <label class="field">Nome<input name="name" value="${escapeHtml(user.name)}"></label>
        <label class="field">Biografia<textarea name="bio" placeholder="Conte um pouco sobre você">${escapeHtml(user.bio || "")}</textarea></label>
        <button class="btn">Salvar alterações</button>
        <button type="button" class="btn danger" onclick="deleteAccount()">Excluir conta</button>
      </div>
      <div class="profile-photo">
        <div class="avatar">
          ${user.fotoUrl
      ? `<img src="${user.fotoUrl}" alt="Avatar">`
      : `<img src="assets/logo-trevo-transparente.png" alt="Símbolo do Trevo">`}
        </div>
        <h2>${escapeHtml(user.name)}</h2>
        <label class="btn secondary" style="cursor:pointer">
          Trocar foto
          <input type="file" accept="image/*" hidden onchange="uploadAvatar(this)">
        </label>
      </div>
    </form>
  </section>`);

  document.querySelector("#profile-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    try {
      await api.request("/me", {
        method: "PATCH",
        body: { name: form.name.value, bio: form.bio.value }
      });
      toast("Perfil atualizado!");
      router();
    } catch (e) { toast(e.message); }
  };
}

async function uploadAvatar(input) {
  if (!input.files.length) return;
  const file = input.files[0];

  const cloudData = new FormData();
  cloudData.append("file", file);
  cloudData.append("upload_preset", "trevo_unsigned");

  const cloudRes = await fetch(
      `https://api.cloudinary.com/v1_1/${CONFIG.cloudinaryCloudName}/auto/upload`,
      { method: "POST", body: cloudData }
  );

  if (!cloudRes.ok) return toast("Falha no upload");

  const cloudJson = await cloudRes.json();

  try {
    await api.request("/me/avatar", {
      method: "POST",
      body: { url: cloudJson.secure_url }
    });
    toast("Foto atualizada!");
    router();
  } catch (e) { toast(e.message); }
}


async function trash() {
  let itens = [];
  try {
    itens = await api.request("/trash");
  } catch (e) { toast(e.message); }

  shell(`<section class="page">
    <div class="page-head">
      <div><h1>Lixeira</h1><p>Itens excluídos podem ser restaurados.</p></div>
      ${itens.length ? '<button class="btn danger" onclick="emptyTrash()">Esvaziar lixeira</button>' : ""}
    </div>
    ${itens.length ? `<div class="grid">${itens.map(m => `
      <article class="card">
        ${m.tipo === "VIDEO"
      ? `<div onclick="abrirLightbox('${m.url}', 'VIDEO')" style="cursor:pointer">
       <video src="${m.url}" style="width:100%;height:145px;object-fit:cover"></video>
     </div>`
      : `<div class="cover" style="cursor:pointer" onclick="abrirLightbox('${m.url}', 'FOTO')">
       <img src="${m.url}" alt="">
     </div>`}
        <div class="card-body">
          <h3>${escapeHtml(m.nome)}</h3>
          <p>Item excluído</p>
          <div class="trash-actions">
            <button class="btn secondary" onclick="restoreItem(${m.id})">↶ Restaurar</button>
          </div>
        </div>
      </article>
    `).join("")}</div>` : `<div class="empty"><h2>Não há itens na lixeira</h2></div>`}
  </section>`);
}

async function deleteMedia(id) {
  if (!confirm("Mover para a lixeira?")) return;
  try {
    await api.request(`/media/${id}/trash`, { method: "PATCH" });
    toast("Movido para a lixeira");
    router();
  } catch (e) { toast(e.message); }
}

// Mutations and modal ---------------------------------------------------------
function openModal(kind, id, parentAlbumId) {
  const isPhoto = kind === "photo";
  const isAlbum = kind === "album";
  const label = isAlbum ? "álbum" : kind === "category" ? "categoria" : "foto";

  const body = `<form id="modal-form">
    ${!isPhoto ? `<label class="field">${label[0].toUpperCase() + label.slice(1)}
      <input name="name" required value="" placeholder="Nome do ${label}">
    </label>` : ""}
    ${isPhoto ? '<label class="field">Escolher arquivo<input name="file" type="file" accept="image/*,video/*" required></label>' : ""}
    ${isAlbum ? `<label class="field">Descrição<textarea name="description"></textarea></label>` : ""}
    <div class="modal-actions">
      <button type="button" class="btn outline" onclick="closeModal()">Cancelar</button>
      <button class="btn">Salvar</button>
    </div>
  </form>`;

  document.body.insertAdjacentHTML("beforeend", `<div class="modal-backdrop" id="modal-backdrop">
    <section class="modal" role="dialog" aria-modal="true">
      <header><strong>Criar</strong><button class="icon-btn" onclick="closeModal()">×</button></header>
      ${body}
    </section>
  </div>`);

  document.querySelector("#modal-backdrop").onclick = (e) => {
    if (e.target.id === "modal-backdrop") closeModal();
  };

  document.querySelector("#modal-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const data = Object.fromEntries(new FormData(form));

    try {
      if (isPhoto) {
        const fileInput = form.querySelector('input[name="file"]');
        if (!fileInput.files.length) return toast("Selecione um arquivo");

        const file = fileInput.files[0];

        const cloudData = new FormData();
        cloudData.append("file", file);
        cloudData.append("upload_preset", CONFIG.cloudinaryPreset);

        const cloudRes = await fetch(
            `https://api.cloudinary.com/v1_1/${CONFIG.cloudinaryCloudName}/auto/upload`,
            { method: "POST", body: cloudData }
        );

        if (!cloudRes.ok) throw new Error("Falha no upload pro Cloudinary");

        const cloudJson = await cloudRes.json();

        await api.request(`/categories/${parentAlbumId}/media`, {
          method: "POST",
          body: {
            url: cloudJson.secure_url,
            nome: file.name,
            tipo: file.type.startsWith("video") ? "VIDEO" : "FOTO"
          }
        });
      } else if (isAlbum) {
        await api.albums.create({ name: data.name, description: data.description });
      } else {
        await api.categories.create(parentAlbumId, { name: data.name });
      }

      closeModal();
      toast("Salvo com sucesso!");
      router();
    } catch (e) {
      toast(e.message);
    }
  };
}

function openDeleteMenu(type, id, name) {
  document.body.insertAdjacentHTML("beforeend", `<div class="modal-backdrop" id="modal-backdrop">
    <section class="modal action-menu">
      <header><strong>Opções</strong><button class="icon-btn" onclick="closeModal()">×</button></header>
      <div class="modal-content">
        <h2>${escapeHtml(name)}</h2>
        <button class="menu-action" onclick="editarItem('${type}', ${id}, '${escapeHtml(name)}')">✏ Editar</button>
        <button class="menu-action danger-action" onclick="moveToTrash('${type}', ${id})">🗑 Excluir ${type}</button>
      </div>
    </section>
  </div>`);
}

async function editarItem(type, id, name) {
  closeModal();

  const body = `<form id="modal-form">
    <label class="field">Nome<input name="name" required value="${escapeHtml(name)}"></label>
    ${type === "album" ? '<label class="field">Descrição<textarea name="description"></textarea></label>' : ""}
    <div class="modal-actions">
      <button type="button" class="btn outline" onclick="closeModal()">Cancelar</button>
      <button class="btn">Salvar</button>
    </div>
  </form>`;

  document.body.insertAdjacentHTML("beforeend", `<div class="modal-backdrop" id="modal-backdrop">
    <section class="modal">
      <header><strong>Editar</strong><button class="icon-btn" onclick="closeModal()">×</button></header>
      ${body}
    </section>
  </div>`);

  document.querySelector("#modal-form").onsubmit = async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const data = Object.fromEntries(new FormData(form));

    try {
      if (type === "album") {
        await api.albums.update(id, { name: data.name, description: data.description || "" });
      } else {
        await api.categories.update(id, { name: data.name });
      }
      closeModal();
      toast("Atualizado!");
      router();
    } catch (e) { toast(e.message); }
  };
}

function openAlbumMenu(id, name) { openDeleteMenu("album", id, name); }
function openCategoryMenu(id, name) { openDeleteMenu("categoria", id, name); }

async function moveToTrash(type, id) {
  try {
    if (type === "album") {
      await api.albums.remove(id);
      closeModal();
      go("/home");
    } else {
      await api.categories.remove(id);
      closeModal();
      router();
    }
    toast("Excluído.");
  } catch (e) {
    toast(e.message);
  }
}

async function restoreItem(id){ try { await api.request(`/trash/${id}/restore`, { method: "PATCH" }); toast("Restaurado"); router(); } catch (e) { toast(e.message); }}
async function emptyTrash() { if (!confirm("Esvaziar a lixeira?")) return; try { await api.request("/trash", { method: "DELETE" }); toast("Lixeira esvaziada"); router(); } catch (e) { toast(e.message); } }
function deleteComment(id) { if (confirm("Excluir este comentário?")) { state.comments = state.comments.filter((entry) => entry.id !== id); save(); router(); } }
function deleteAccount() { if (confirm("Deseja realmente excluir sua conta?")) { state.user = null; storage.remove("token"); save(); go("/login"); } }
function back() { route() === "/home" ? go("/login") : history.back(); }
function toggleTheme() { state.theme = state.theme === "dark" ? "light" : "dark"; localStorage.setItem("trevo-theme", state.theme); document.body.classList.toggle("dark", state.theme === "dark"); router(); }

function router() {
  const pages = { "/login": login, "/signup": signup, "/forgot": forgot, "/reset": reset, "/home": home, "/profile": profile, "/trash": trash };
  if (route().startsWith("/album/")) return album();
  if (route().startsWith("/comments/")) return comments();
  (pages[route()] || (() => go(state.user ? "/home" : "/login")))();
}

Object.assign(window, { back, closeModal,abrirLightbox, deleteAccount, deleteComment, deleteMedia, emptyTrash, go, moveToTrash, openAlbumMenu, openCategoryMenu, openModal, restoreItem, toggleTheme, uploadAvatar });
document.body.classList.toggle("dark", state.theme === "dark");
window.addEventListener("hashchange", router);
router();
