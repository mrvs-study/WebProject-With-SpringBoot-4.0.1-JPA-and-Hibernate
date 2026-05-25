const user = JSON.parse(sessionStorage.getItem('em_user') || 'null');
if (!user || !user.idFuncionario) { window.location.href = 'index.html'; }
else {
  document.getElementById('userName').textContent = user.name;
  document.getElementById('userRole').textContent = user.role;
  document.getElementById('userAvatar').textContent = user.name.charAt(0).toUpperCase();
}

const ROLE_PAGES = {
  GERENTE:    ['dashboard', 'pdv', 'produtos', 'clientes', 'fornecedores', 'estoque', 'relatorios', 'usuarios'],
  OPERADOR:   ['dashboard', 'pdv', 'produtos', 'clientes'],
  ESTOQUISTA: ['produtos', 'estoque', 'fornecedores'],
};

const ROLE_WRITE = {
  GERENTE:    ['pdv', 'produtos', 'clientes', 'fornecedores', 'estoque', 'usuarios'],
  OPERADOR:   ['pdv', 'clientes'],
  ESTOQUISTA: ['produtos', 'fornecedores', 'estoque'],
};

const allowedPages = ROLE_PAGES[user.cargo] || ROLE_PAGES.GERENTE;
const writePages   = ROLE_WRITE[user.cargo]  || ROLE_WRITE.GERENTE;

function canWrite(page) { return writePages.includes(page); }

function logout() {
  sessionStorage.removeItem('em_user');
  window.location.href = 'index.html';
}

const API = '';

async function api(method, path, body) {
  const opts = { method, headers: { 'Content-Type': 'application/json' } };
  if (body) opts.body = JSON.stringify(body);
  try {
    const res = await fetch(API + path, opts);
    if (res.status === 204) return null;
    const json = await res.json();
    if (!res.ok) throw new Error(json.message || json.erro || 'Erro na requisição');
    return json;
  } catch (e) {
    throw e;
  }
}

function toast(msg, type = '') {
  const el = document.createElement('div');
  el.className = `toast ${type}`;
  el.textContent = msg;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 3000);
}

function openModal(id) { document.getElementById(id).classList.remove('hidden'); }
function closeModal(id) { document.getElementById(id).classList.add('hidden'); }

let currentPage = allowedPages[0];

function navigate(page) {
  if (!allowedPages.includes(page)) {
    toast('Acesso negado para este módulo.', 'error');
    return;
  }
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  document.getElementById('page-' + page).classList.add('active');
  document.querySelector(`.nav-item[data-page="${page}"]`).classList.add('active');
  currentPage = page;
  loadPage(page);
}

document.querySelectorAll('.nav-item').forEach(el => {
  if (!allowedPages.includes(el.dataset.page)) {
    el.style.display = 'none';
  } else {
    el.addEventListener('click', () => navigate(el.dataset.page));
  }
});

document.querySelectorAll('.nav-label').forEach(label => {
  let next = label.nextElementSibling;
  let hasVisible = false;
  while (next && !next.classList.contains('nav-label')) {
    if (next.style.display !== 'none') { hasVisible = true; break; }
    next = next.nextElementSibling;
  }
  if (!hasVisible) label.style.display = 'none';
});

document.querySelectorAll('[data-write]').forEach(el => {
  if (!canWrite(el.dataset.write)) el.style.display = 'none';
});

document.querySelectorAll('[data-require-page]').forEach(el => {
  if (!allowedPages.includes(el.dataset.requirePage)) el.style.display = 'none';
});

function loadPage(page) {
  const loaders = {
    dashboard:    loadDashboard,
    pdv:          loadPDV,
    produtos:     loadProdutos,
    clientes:     loadClientes,
    fornecedores: loadFornecedores,
    estoque:      loadEstoque,
    relatorios:   loadRelatorios,
    usuarios:     loadUsuarios,
  };
  if (loaders[page]) loaders[page]();
}

function brl(n) {
  return 'R$ ' + Number(n || 0).toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function fmtDate(isoStr) {
  if (!isoStr) return '—';
  return new Date(isoStr).toLocaleDateString('pt-BR');
}

const WEEKDAYS = ['Dom', 'Seg', 'Ter', 'Qua', 'Qui', 'Sex', 'Sáb'];
const MONTHS = ['Jan','Fev','Mar','Abr','Mai','Jun','Jul','Ago','Set','Out','Nov','Dez'];

function ptDate(d) {
  const days = ['domingo','segunda-feira','terça-feira','quarta-feira','quinta-feira','sexta-feira','sábado'];
  const months = ['janeiro','fevereiro','março','abril','maio','junho','julho','agosto','setembro','outubro','novembro','dezembro'];
  return `${days[d.getDay()]}, ${d.getDate()} de ${months[d.getMonth()]} de ${d.getFullYear()}`;
}

function catColor(cat) {
  const map = {
    'grãos':'#ede9fe|#5b21b6','bebidas':'#dbeafe|#1e40af','laticínios':'#fef3c7|#92400e',
    'limpeza':'#dcfce7|#166534','higiene':'#fce7f3|#9d174d','carnes':'#fee2e2|#991b1b'
  };
  const k = (cat||'').toLowerCase();
  for (const [key, val] of Object.entries(map)) {
    if (k.includes(key)) { const [bg, fg] = val.split('|'); return { bg, fg }; }
  }
  return { bg: '#f1f5f9', fg: '#475569' };
}

function avatarColor(name) {
  const colors = ['#10b981','#3b82f6','#8b5cf6','#f59e0b','#ef4444','#06b6d4'];
  let h = 0;
  for (let c of (name||'?')) h = (h * 31 + c.charCodeAt(0)) % colors.length;
  return colors[h];
}

async function loadDashboard() {
  const d = new Date();
  document.getElementById('dash-date').textContent = ptDate(d);

  const [vendas, clientes, estoquesBaixos] = await Promise.allSettled([
    api('GET', '/vendas'),
    api('GET', '/clientes'),
    api('GET', '/estoques/sem-estoque'),
  ]);

  const vendasList = vendas.status === 'fulfilled' ? (vendas.value || []) : [];
  const clientesList = clientes.status === 'fulfilled' ? (clientes.value || []) : [];
  const baixosList = estoquesBaixos.status === 'fulfilled' ? (estoquesBaixos.value || []) : [];

  const today = d.toDateString();
  const vendasHoje = vendasList.filter(v => v.momento && new Date(v.momento).toDateString() === today);
  const fatHoje = vendasHoje.filter(v => v.statusVenda !== 'CANCELADA').reduce((s, v) => s + (v.total || 0), 0);

  document.getElementById('stat-fat').textContent = brl(fatHoje);
  document.getElementById('stat-fat-sub').textContent = `${vendasHoje.length} vendas hoje`;
  document.getElementById('stat-vendas').textContent = vendasHoje.filter(v => v.statusVenda !== 'CANCELADA').length;
  document.getElementById('stat-vendas-sub').textContent = 'finalizadas hoje';
  document.getElementById('stat-estoque').textContent = baixosList.length;
  document.getElementById('stat-estoque-sub').textContent = baixosList.length > 0 ? 'Produtos abaixo do mínimo' : 'Tudo em ordem';
  document.getElementById('stat-clientes').textContent = clientesList.length;
  document.getElementById('stat-clientes-sub').textContent = 'Cadastrados no sistema';

  const badgeE = document.getElementById('badge-estoque');
  if (baixosList.length > 0) { badgeE.textContent = baixosList.length; badgeE.style.display = ''; }
  else badgeE.style.display = 'none';

  const days7 = [];
  for (let i = 6; i >= 0; i--) {
    const dd = new Date(); dd.setDate(dd.getDate() - i);
    days7.push(dd);
  }
  const totals = days7.map(dd => {
    const ds = dd.toDateString();
    return vendasList.filter(v => v.momento && new Date(v.momento).toDateString() === ds && v.statusVenda !== 'CANCELADA')
                     .reduce((s, v) => s + (v.total || 0), 0);
  });
  const maxVal = Math.max(...totals, 1);
  const chartEl = document.getElementById('dash-chart');
  chartEl.innerHTML = days7.map((dd, i) => {
    const pct = Math.max((totals[i] / maxVal) * 100, 4);
    const isToday = dd.toDateString() === d.toDateString();
    return `<div class="bar-wrap">
      <div class="bar ${isToday ? 'today' : ''}" style="height:${pct}%"></div>
      <div class="bar-label">${WEEKDAYS[dd.getDay()].charAt(0)}</div>
    </div>`;
  }).join('');

  const finalVendas = vendasList.filter(v => v.statusVenda === 'FINALIZADA');
  let pagamentos = { DINHEIRO: 0, PIX: 0, CARTAO_CREDITO: 0, CARTAO_DEBITO: 0 };
  finalVendas.forEach(v => {
    if (v.pagamento && v.pagamento.formaPagamento) pagamentos[v.pagamento.formaPagamento] = (pagamentos[v.pagamento.formaPagamento] || 0) + 1;
  });
  const totalPag = Object.values(pagamentos).reduce((a, b) => a + b, 0) || 1;
  const payLabels = { DINHEIRO: 'Dinheiro', PIX: 'Pix', CARTAO_CREDITO: 'Cartão Crédito', CARTAO_DEBITO: 'Cartão Débito' };
  const payColors = { DINHEIRO: '#0f172a', PIX: '#10b981', CARTAO_CREDITO: '#3b82f6', CARTAO_DEBITO: '#8b5cf6' };
  document.getElementById('dash-payments').innerHTML = Object.entries(pagamentos)
    .sort((a, b) => b[1] - a[1])
    .map(([k, v]) => {
      const pct = Math.round(v / totalPag * 100);
      return `<div class="payment-row">
        <div class="payment-name-row"><span class="payment-name">${payLabels[k]}</span><span class="payment-pct">${pct}%</span></div>
        <div class="payment-bar-bg"><div class="payment-bar-fill" style="width:${pct}%;background:${payColors[k]}"></div></div>
      </div>`;
    }).join('');

  const lastSales = [...vendasList].sort((a, b) => new Date(b.momento) - new Date(a.momento)).slice(0, 5);
  const statusBadge = { FINALIZADA: 'badge-green', CANCELADA: 'badge-pink', ABERTA: 'badge-blue' };
  const statusLabel = { FINALIZADA: 'Concluída', CANCELADA: 'Cancelada', ABERTA: 'Aberta' };
  const tbody = document.getElementById('dash-sales-body');
  if (lastSales.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state"><div class="empty-icon">🛒</div><p>Nenhuma venda registrada</p></td></tr>`;
  } else {
    tbody.innerHTML = lastSales.map(v => `<tr>
      <td><strong>#${String(v.idVenda).padStart(3,'0')}</strong></td>
      <td>${v.clienteNome || '—'}</td>
      <td>${(v.itens || []).length} ${(v.itens || []).length === 1 ? 'item' : 'itens'}</td>
      <td>${brl(v.total)}</td>
      <td><span class="badge ${statusBadge[v.statusVenda] || 'badge-gray'}">${statusLabel[v.statusVenda] || v.statusVenda}</span></td>
    </tr>`).join('');
  }
}

let pdvProdutos = [];
let pdvEstoques = {};
let cart = [];
let selectedPayment = null;
let activeCategory = 'Todos';

async function loadPDV() {
  try {
    const [prods, estoques] = await Promise.all([api('GET', '/produtos'), api('GET', '/estoques')]);
    pdvProdutos = prods || [];
    pdvEstoques = {};
    (estoques || []).forEach(e => { if (e.produtoId) pdvEstoques[e.produtoId] = e; });
    renderPDV();
  } catch (e) {
    document.getElementById('pdv-product-grid').innerHTML = `<div class="empty-state"><p>Erro ao carregar produtos: ${e.message}</p></div>`;
  }
}

function renderPDV() {
  const cats = ['Todos', ...new Set(pdvProdutos.map(p => p.categoria).filter(Boolean))];
  document.getElementById('pdv-filter-tabs').innerHTML = cats.map(c =>
    `<button class="filter-tab ${c === activeCategory ? 'active' : ''}" onclick="setCat('${c}')">${c}</button>`
  ).join('');
  renderProductGrid();
}

function setCat(cat) { activeCategory = cat; renderPDV(); }

function filterPdvProducts() { renderProductGrid(); }

function renderProductGrid() {
  const query = (document.getElementById('pdv-search')?.value || '').toLowerCase();
  const filtered = pdvProdutos.filter(p => {
    const matchCat = activeCategory === 'Todos' || p.categoria === activeCategory;
    const matchQ = !query || p.nome.toLowerCase().includes(query) || (p.categoria||'').toLowerCase().includes(query);
    return matchCat && matchQ;
  });
  const grid = document.getElementById('pdv-product-grid');
  if (filtered.length === 0) {
    grid.innerHTML = `<div class="empty-state"><div class="empty-icon">📦</div><p>Nenhum produto encontrado</p></div>`;
    return;
  }
  grid.innerHTML = filtered.map(p => {
    const est = pdvEstoques[p.idProduto];
    const qtd = est ? est.quantidadeAtual : '?';
    return `<div class="product-card" onclick="addToCart(${p.idProduto})">
      <div class="product-card-name">${p.nome}</div>
      <div class="product-card-meta">${p.categoria || ''} · ${qtd} un</div>
      <div class="product-card-price">${brl(p.preco)}</div>
    </div>`;
  }).join('');
}

function addToCart(prodId) {
  const prod = pdvProdutos.find(p => p.idProduto === prodId);
  if (!prod) return;
  const est = pdvEstoques[prodId];
  const maxQtd = est ? est.quantidadeAtual : 999;
  const existing = cart.find(c => c.produtoId === prodId);
  if (existing) {
    if (existing.quantidade >= maxQtd) { toast('Estoque insuficiente', 'error'); return; }
    existing.quantidade++;
  } else {
    if (maxQtd < 1) { toast('Produto sem estoque', 'error'); return; }
    cart.push({ produtoId: prodId, nome: prod.nome, preco: prod.preco, quantidade: 1 });
  }
  renderCart();
}

function renderCart() {
  const count = cart.reduce((s, i) => s + i.quantidade, 0);
  const total = cart.reduce((s, i) => s + i.preco * i.quantidade, 0);
  document.getElementById('cart-count').textContent = count;
  document.getElementById('cart-total').textContent = brl(total);

  const badge = document.getElementById('badge-pdv');
  if (count > 0) { badge.textContent = count; badge.style.display = ''; }
  else badge.style.display = 'none';

  const el = document.getElementById('cart-items');
  if (cart.length === 0) {
    el.innerHTML = `<div class="cart-empty">Selecione produtos ao lado</div>`;
  } else {
    el.innerHTML = cart.map(item => `
      <div class="cart-item">
        <div>
          <div class="cart-item-name">${item.nome}</div>
          <div class="cart-item-sub">${brl(item.preco)} × ${item.quantidade} = ${brl(item.preco * item.quantidade)}</div>
        </div>
        <div class="cart-item-right">
          <div class="cart-qty">
            <div class="qty-btn" onclick="changeQty(${item.produtoId}, -1)">−</div>
            <div class="qty-val">${item.quantidade}</div>
            <div class="qty-btn" onclick="changeQty(${item.produtoId}, 1)">+</div>
          </div>
        </div>
      </div>`).join('');
  }
  const btn = document.getElementById('btn-finalize');
  btn.className = 'btn-finalize' + (cart.length > 0 && selectedPayment ? ' ready' : '');
}

function changeQty(prodId, delta) {
  const item = cart.find(c => c.produtoId === prodId);
  if (!item) return;
  item.quantidade += delta;
  if (item.quantidade <= 0) cart = cart.filter(c => c.produtoId !== prodId);
  renderCart();
}

function selectPayment(btn) {
  document.querySelectorAll('.pay-btn').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  selectedPayment = btn.dataset.method;
  renderCart();
}

async function finalizarVenda() {
  if (cart.length === 0 || !selectedPayment) return;
  try {
    const vendaBody = { itens: cart.map(i => ({ produtoId: i.produtoId, quantidade: i.quantidade })) };
    const venda = await api('POST', '/vendas', vendaBody);
    await api('POST', '/pagamentos', { vendaId: venda.idVenda, valor: venda.total, formaPagamento: selectedPayment });
    await api('PUT', `/vendas/${venda.idVenda}/finalizar`);
    toast(`Venda #${String(venda.idVenda).padStart(3,'0')} finalizada com sucesso!`, 'success');
    cart = [];
    selectedPayment = null;
    document.querySelectorAll('.pay-btn').forEach(b => b.classList.remove('active'));
    renderCart();
    loadPDV();
  } catch (e) {
    toast('Erro ao finalizar venda: ' + e.message, 'error');
  }
}

function cancelarVenda() {
  if (cart.length === 0) return;
  cart = [];
  selectedPayment = null;
  document.querySelectorAll('.pay-btn').forEach(b => b.classList.remove('active'));
  renderCart();
  toast('Venda cancelada', '');
}

let produtosList = [];
let estoquesMap = {};

async function loadProdutos() {
  try {
    const [prods, estoques] = await Promise.all([api('GET', '/produtos'), api('GET', '/estoques')]);
    produtosList = prods || [];
    estoquesMap = {};
    (estoques || []).forEach(e => { if (e.produtoId) estoquesMap[e.produtoId] = e; });
    renderProdutos(produtosList);
  } catch (e) {
    document.getElementById('produtos-body').innerHTML = `<tr><td colspan="7" class="empty-state"><p>Erro: ${e.message}</p></td></tr>`;
  }
}

function filterProdutos() {
  const q = (document.getElementById('prod-search')?.value || '').toLowerCase();
  renderProdutos(produtosList.filter(p => p.nome.toLowerCase().includes(q) || (p.categoria||'').toLowerCase().includes(q)));
}

function renderProdutos(list) {
  const tbody = document.getElementById('produtos-body');
  if (list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state"><div class="empty-icon">📦</div><p>Nenhum produto encontrado</p></td></tr>`;
    return;
  }
  tbody.innerHTML = list.map(p => {
    const est = estoquesMap[p.idProduto];
    const qtd = est ? est.quantidadeAtual : null;
    const low = qtd !== null && qtd <= 5;
    const zero = qtd === 0;
    const qtdHtml = qtd === null ? '—' : zero ? `<span style="color:var(--red)">${qtd} un</span>` :
      low ? `<span style="color:var(--orange)">${qtd} un ⚠</span>` : `${qtd} un`;
    const { bg, fg } = catColor(p.categoria);
    return `<tr>
      <td><span style="color:var(--text-muted);font-weight:600">#${String(p.idProduto).padStart(3,'0')}</span></td>
      <td><strong>${p.nome}</strong></td>
      <td><span class="cat-tag" style="background:${bg};color:${fg}">${p.categoria||'—'}</span></td>
      <td>${brl(p.preco)}</td>
      <td>${qtdHtml}</td>
      <td><span class="badge ${zero ? 'badge-red' : 'badge-green'}">${zero ? 'Inativo' : 'Ativo'}</span></td>
      <td>${canWrite('produtos') ? `
        <button class="btn btn-sm btn-outline" onclick="openProdutoModal(${p.idProduto})" style="margin-right:4px">✏</button>
        <button class="btn btn-sm btn-danger" onclick="confirmDelete('produto',${p.idProduto},'${p.nome.replace(/'/g,"\\'")}')">🗑</button>
      ` : '—'}</td>
    </tr>`;
  }).join('');
}

function openProdutoModal(id) {
  if (!canWrite('produtos')) return;
  document.getElementById('modal-produto-title').textContent = id ? 'Editar Produto' : 'Novo Produto';
  document.getElementById('mp-id').value = id || '';
  const p = id ? produtosList.find(x => x.idProduto === id) : null;
  document.getElementById('mp-nome').value = p?.nome || '';
  document.getElementById('mp-categoria').value = p?.categoria || '';
  document.getElementById('mp-preco').value = p?.preco || '';
  document.getElementById('mp-validade').value = p?.dataDeValidade ? p.dataDeValidade.split('T')[0] : '';
  document.getElementById('mp-img').value = p?.imgUrlProduto || '';
  const estoqueSection = document.getElementById('mp-estoque-section');
  if (id) {
    estoqueSection.style.display = 'none';
  } else {
    estoqueSection.style.display = '';
    document.getElementById('mp-localizacao').value = '';
    document.getElementById('mp-qtd-max').value = '';
  }
  openModal('modal-produto');
}

async function saveProduto() {
  if (!canWrite('produtos')) return;
  const id = document.getElementById('mp-id').value;
  const nome = document.getElementById('mp-nome').value.trim();
  const categoria = document.getElementById('mp-categoria').value.trim();
  const preco = parseFloat(document.getElementById('mp-preco').value);
  const validade = document.getElementById('mp-validade').value;
  const img = document.getElementById('mp-img').value.trim();
  if (!nome || !categoria || isNaN(preco)) { toast('Preencha os campos obrigatórios', 'error'); return; }
  const body = { nome, categoria, preco, imgUrlProduto: img || null };
  if (validade) body.dataDeValidade = new Date(validade).toISOString();
  try {
    if (id) {
      await api('PUT', `/produtos/${id}`, body);
      toast('Produto atualizado!', 'success');
    } else {
      const localizacao = document.getElementById('mp-localizacao').value.trim();
      const qtdMax = parseInt(document.getElementById('mp-qtd-max').value);
      if (!localizacao || !qtdMax || qtdMax < 1) { toast('Informe a localização e a quantidade máxima do estoque', 'error'); return; }
      const novoProduto = await api('POST', '/produtos', body);
      await api('POST', '/estoques', { produtoId: novoProduto.idProduto, quantidadeAtual: 0, quantidadeMaxima: qtdMax, localizacao });
      toast('Produto cadastrado com estoque!', 'success');
    }
    closeModal('modal-produto');
    loadProdutos();
  } catch (e) { toast('Erro: ' + e.message, 'error'); }
}

let clientesList = [];

async function loadClientes() {
  try {
    clientesList = await api('GET', '/clientes') || [];
    renderClientes(clientesList);
  } catch (e) {
    document.getElementById('clientes-body').innerHTML = `<tr><td colspan="5" class="empty-state"><p>Erro: ${e.message}</p></td></tr>`;
  }
}

function filterClientes() {
  const q = (document.getElementById('cli-search')?.value || '').toLowerCase();
  renderClientes(clientesList.filter(c => c.nome.toLowerCase().includes(q) || (c.cpf||'').includes(q)));
}

function renderClientes(list) {
  const tbody = document.getElementById('clientes-body');
  if (list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="empty-state"><div class="empty-icon">👤</div><p>Nenhum cliente encontrado</p></td></tr>`;
    return;
  }
  tbody.innerHTML = list.map(c => {
    const initials = c.nome.split(' ').map(w => w[0]).slice(0, 2).join('').toUpperCase();
    const bg = avatarColor(c.nome);
    return `<tr>
      <td style="display:flex;align-items:center;gap:10px">
        <div class="avatar" style="background:${bg};color:#fff">${initials}</div>
        <strong>${c.nome}</strong>
      </td>
      <td>${c.cpf || '—'}</td>
      <td>${c.telefone || '—'}</td>
      <td>${c.email || '—'}</td>
      <td>${canWrite('clientes') ? `
        <button class="btn btn-sm btn-outline" onclick="openClienteModal(${c.idCliente})" style="margin-right:4px">✏</button>
        <button class="btn btn-sm btn-danger" onclick="confirmDelete('cliente',${c.idCliente},'${c.nome.replace(/'/g,"\\'")}')">🗑</button>
      ` : '—'}</td>
    </tr>`;
  }).join('');
}

function openClienteModal(id) {
  if (!canWrite('clientes')) return;
  document.getElementById('modal-cliente-title').textContent = id ? 'Editar Cliente' : 'Novo Cliente';
  document.getElementById('mc-id').value = id || '';
  const c = id ? clientesList.find(x => x.idCliente === id) : null;
  document.getElementById('mc-nome').value = c?.nome || '';
  document.getElementById('mc-cpf').value = c?.cpf || '';
  document.getElementById('mc-email').value = c?.email || '';
  document.getElementById('mc-telefone').value = c?.telefone || '';
  document.getElementById('mc-senha').value = '';
  openModal('modal-cliente');
}

async function saveCliente() {
  if (!canWrite('clientes')) return;
  const id = document.getElementById('mc-id').value;
  const nome = document.getElementById('mc-nome').value.trim();
  const cpf = document.getElementById('mc-cpf').value.trim();
  const email = document.getElementById('mc-email').value.trim();
  const telefone = document.getElementById('mc-telefone').value.trim();
  const senha = document.getElementById('mc-senha').value;
  if (!nome || !cpf || !email) { toast('Preencha os campos obrigatórios', 'error'); return; }
  if (!id && !senha) { toast('Informe a senha', 'error'); return; }
  const body = { nome, cpf, email, telefone };
  if (senha) body.senha = senha;
  try {
    if (id) { await api('PUT', `/clientes/${id}`, body); toast('Cliente atualizado!', 'success'); }
    else { await api('POST', '/clientes', body); toast('Cliente cadastrado!', 'success'); }
    closeModal('modal-cliente');
    loadClientes();
  } catch (e) { toast('Erro: ' + e.message, 'error'); }
}

let fornecedoresList = [];

async function loadFornecedores() {
  try {
    fornecedoresList = await api('GET', '/fornecedores') || [];
    renderFornecedores();
  } catch (e) {
    document.getElementById('fornecedores-grid').innerHTML = `<div class="empty-state"><p>Erro: ${e.message}</p></div>`;
  }
}

function renderFornecedores() {
  const grid = document.getElementById('fornecedores-grid');
  if (fornecedoresList.length === 0) {
    grid.innerHTML = `<div class="empty-state"><div class="empty-icon">🏭</div><p>Nenhum fornecedor cadastrado</p></div>`;
    return;
  }
  grid.innerHTML = fornecedoresList.map(f => `
    <div class="supplier-card">
      <div class="supplier-header">
        <div>
          <div class="supplier-name">${f.razaoSocial}</div>
          <div class="supplier-cnpj">CNPJ: ${f.cnpj}</div>
        </div>
        <span class="badge badge-green">Ativo</span>
      </div>
      <div class="supplier-detail">${f.telefone || '—'}</div>
      <div class="supplier-detail">${f.cep ? f.cep : '—'}</div>
      <div class="supplier-detail" style="color:var(--accent)">${f.email || ''}</div>
      ${canWrite('fornecedores') ? `
      <div class="supplier-actions">
        <button class="btn btn-outline" onclick="openFornecedorModal(${f.idFornecedor})">Editar</button>
        <button class="btn btn-danger" onclick="confirmDelete('fornecedor',${f.idFornecedor},'${f.razaoSocial.replace(/'/g,"\\'")}')">Remover</button>
      </div>` : ''}
    </div>`).join('');
}

function openFornecedorModal(id) {
  if (!canWrite('fornecedores')) return;
  document.getElementById('modal-fornecedor-title').textContent = id ? 'Editar Fornecedor' : 'Novo Fornecedor';
  document.getElementById('mf-id').value = id || '';
  const f = id ? fornecedoresList.find(x => x.idFornecedor === id) : null;
  document.getElementById('mf-razao').value = f?.razaoSocial || '';
  document.getElementById('mf-cnpj').value = f?.cnpj || '';
  document.getElementById('mf-telefone').value = f?.telefone || '';
  document.getElementById('mf-cep').value = f?.cep || '';
  document.getElementById('mf-email').value = f?.email || '';
  openModal('modal-fornecedor');
}

async function saveFornecedor() {
  if (!canWrite('fornecedores')) return;
  const id = document.getElementById('mf-id').value;
  const razaoSocial = document.getElementById('mf-razao').value.trim();
  const cnpj = document.getElementById('mf-cnpj').value.trim();
  const telefone = document.getElementById('mf-telefone').value.trim();
  const cep = document.getElementById('mf-cep').value.trim();
  const email = document.getElementById('mf-email').value.trim();
  if (!razaoSocial || !cnpj || !email) { toast('Preencha os campos obrigatórios', 'error'); return; }
  const body = { razaoSocial, cnpj, telefone, cep, email };
  try {
    if (id) { await api('PUT', `/fornecedores/${id}`, body); toast('Fornecedor atualizado!', 'success'); }
    else { await api('POST', '/fornecedores', body); toast('Fornecedor cadastrado!', 'success'); }
    closeModal('modal-fornecedor');
    loadFornecedores();
  } catch (e) { toast('Erro: ' + e.message, 'error'); }
}

let estoqueList = [];
let todosProdutosEstoque = [];

async function loadEstoque() {
  try {
    const [estoques, produtos] = await Promise.all([api('GET', '/estoques'), api('GET', '/produtos')]);
    estoqueList = estoques || [];
    todosProdutosEstoque = produtos || [];
    renderEstoque();
  } catch (e) {
    document.getElementById('estoque-body').innerHTML = `<tr><td colspan="7" class="empty-state"><p>Erro: ${e.message}</p></td></tr>`;
  }
}

function renderEstoque() {
  const alertEl = document.getElementById('estoque-alert');
  const baixos = estoqueList.filter(e => e.quantidadeAtual < (e.quantidadeMaxima * 0.3));
  if (baixos.length > 0) {
    alertEl.textContent = `⚠ ${baixos.length} produto(s) com estoque abaixo do mínimo! Verifique os itens em vermelho e providencie reposição.`;
    alertEl.classList.remove('hidden');
  } else {
    alertEl.classList.add('hidden');
  }

  const badgeE = document.getElementById('badge-estoque');
  if (baixos.length > 0) { badgeE.textContent = baixos.length; badgeE.style.display = ''; }
  else badgeE.style.display = 'none';

  const sorted = [...estoqueList].sort((a, b) => {
    const pa = a.quantidadeAtual / (a.quantidadeMaxima || 1);
    const pb = b.quantidadeAtual / (b.quantidadeMaxima || 1);
    return pa - pb;
  });

  const produtosComEstoque = new Set(estoqueList.map(e => e.produtoId));
  const semEstoque = todosProdutosEstoque.filter(p => !produtosComEstoque.has(p.idProduto));

  const tbody = document.getElementById('estoque-body');
  if (sorted.length === 0 && semEstoque.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state"><div class="empty-icon">📦</div><p>Nenhum produto cadastrado</p></td></tr>`;
    return;
  }

  const rowsEstoque = sorted.map(e => {
    const pct = Math.min(Math.round(e.quantidadeAtual / (e.quantidadeMaxima || 1) * 100), 100);
    const min30 = e.quantidadeMaxima * 0.3;
    let sit, sitClass, barColor, rowBg = '';
    if (e.quantidadeAtual <= 0) { sit = 'Crítico'; sitClass = 'badge-red'; barColor = '#ef4444'; rowBg = 'background:#fff5f5'; }
    else if (e.quantidadeAtual < min30) { sit = 'Baixo'; sitClass = 'badge-orange'; barColor = '#f59e0b'; rowBg = 'background:#fffbeb'; }
    else { sit = 'Normal'; sitClass = 'badge-green'; barColor = '#10b981'; }

    const canRepor = canWrite('estoque') && e.quantidadeAtual < e.quantidadeMaxima;
    return `<tr style="${rowBg}">
      <td><strong>${e.produtoNome || '—'}</strong></td>
      <td><span style="font-weight:700;color:${e.quantidadeAtual < min30 ? (e.quantidadeAtual <= 0 ? 'var(--red)' : 'var(--orange)') : 'var(--accent)'}">${e.quantidadeAtual}</span></td>
      <td>${e.quantidadeMaxima}</td>
      <td style="color:var(--text-secondary);font-size:12px">${e.localizacao || '—'}</td>
      <td><span class="badge ${sitClass}">${sit === 'Crítico' ? '⚠ ' : ''}${sit}</span></td>
      <td>
        <div class="progress-bar-bg">
          <div class="progress-bar-fill" style="width:${pct}%;background:${barColor}"></div>
        </div>
      </td>
      <td>${canRepor ? `<button class="btn btn-sm btn-outline" onclick="openReporModal(${e.idEstoque},'${(e.produtoNome||'').replace(/'/g,"\\'")}')">Repor</button>` : '—'}</td>
    </tr>`;
  }).join('');

  const rowsSemEstoque = semEstoque.map(p => `<tr style="background:#f8fafc">
    <td><strong>${p.nome}</strong></td>
    <td colspan="4" style="color:var(--text-muted);font-size:13px">Sem estoque cadastrado</td>
    <td></td>
    <td><button class="btn btn-sm btn-primary" onclick="openNovoEstoqueModal(${p.idProduto},'${p.nome.replace(/'/g,"\\'")}')">Cadastrar</button></td>
  </tr>`).join('');

  tbody.innerHTML = rowsEstoque + rowsSemEstoque;
}

function openNovoEstoqueModal(produtoId, nomeProduto) {
  document.getElementById('mne-produto-id').value = produtoId;
  document.getElementById('mne-produto-nome').value = nomeProduto;
  document.getElementById('mne-localizacao').value = '';
  document.getElementById('mne-qtd-max').value = '';
  openModal('modal-novo-estoque');
}

async function saveNovoEstoque() {
  const produtoId = parseInt(document.getElementById('mne-produto-id').value);
  const localizacao = document.getElementById('mne-localizacao').value.trim();
  const qtdMax = parseInt(document.getElementById('mne-qtd-max').value);
  if (!localizacao || !qtdMax || qtdMax < 1) { toast('Preencha localização e quantidade máxima', 'error'); return; }
  try {
    await api('POST', '/estoques', { produtoId, quantidadeAtual: 0, quantidadeMaxima: qtdMax, localizacao });
    toast('Estoque cadastrado!', 'success');
    closeModal('modal-novo-estoque');
    loadEstoque();
  } catch (e) { toast('Erro: ' + e.message, 'error'); }
}

function openReporModal(id, nomeProduto) {
  document.getElementById('mr-id').value = id;
  document.getElementById('mr-produto').textContent = nomeProduto;
  document.getElementById('mr-qtd').value = '';
  document.getElementById('mr-motivo').value = 'Reposição de estoque';
  openModal('modal-repor');
}

async function reporEstoque() {
  const id = document.getElementById('mr-id').value;
  const qtd = parseInt(document.getElementById('mr-qtd').value);
  const motivo = document.getElementById('mr-motivo').value || 'Reposição de estoque';
  if (!qtd || qtd < 1) { toast('Informe a quantidade', 'error'); return; }
  try {
    await api('PUT', `/estoques/${id}/entrada?quantidade=${qtd}&motivo=${encodeURIComponent(motivo)}`);
    toast('Estoque reposto com sucesso!', 'success');
    closeModal('modal-repor');
    loadEstoque();
  } catch (e) { toast('Erro: ' + e.message, 'error'); }
}

let vendasCache = null;
let currentRelTab = 'diario';

async function loadRelatorios() {
  try {
    vendasCache = await api('GET', '/vendas') || [];
    renderRelatorio();
  } catch (e) { console.error(e); }
}

function setRelTab(btn, tab) {
  document.querySelectorAll('.rel-tab').forEach(b => b.classList.remove('active'));
  btn.classList.add('active');
  currentRelTab = tab;
  renderRelatorio();
}

function renderRelatorio() {
  const vendas = vendasCache || [];
  const d = new Date();

  if (currentRelTab === 'diario') {
    const today = d.toDateString();
    const hojeVendas = vendas.filter(v => v.momento && new Date(v.momento).toDateString() === today);
    const finalizadas = hojeVendas.filter(v => v.statusVenda === 'FINALIZADA');
    const canceladas = hojeVendas.filter(v => v.statusVenda === 'CANCELADA');
    const receita = finalizadas.reduce((s, v) => s + (v.total || 0), 0);
    const ticket = finalizadas.length > 0 ? receita / finalizadas.length : 0;
    const cancelVal = canceladas.reduce((s, v) => s + (v.total || 0), 0);
    document.getElementById('rel-receita').textContent = brl(receita);
    document.getElementById('rel-receita-sub').textContent = `↑ ${finalizadas.length} vendas hoje`;
    document.getElementById('rel-qtd').textContent = finalizadas.length;
    document.getElementById('rel-ticket').textContent = `Ticket médio: ${brl(ticket)}`;
    document.getElementById('rel-cancel').textContent = canceladas.length;
    document.getElementById('rel-cancel-sub').textContent = `${brl(cancelVal)} cancelados`;
    document.getElementById('rel-chart-title').textContent = `Vendas por hora — ${d.getDate()}/${d.getMonth()+1}/${d.getFullYear()}`;
    const hours = {};
    for (let h = 8; h <= 20; h++) hours[h] = 0;
    hojeVendas.forEach(v => {
      if (v.momento) { const h = new Date(v.momento).getHours(); if (h >= 8 && h <= 20) hours[h] = (hours[h]||0) + 1; }
    });
    renderBarChart('rel-chart', Object.entries(hours).map(([h, c]) => ({ label: h+'h', value: c, highlight: new Date().getHours() === parseInt(h) })), 160);

  } else if (currentRelTab === 'mensal') {
    const thisMonth = d.getMonth(); const thisYear = d.getFullYear();
    const mesVendas = vendas.filter(v => v.momento && new Date(v.momento).getMonth() === thisMonth && new Date(v.momento).getFullYear() === thisYear);
    const finalizadas = mesVendas.filter(v => v.statusVenda === 'FINALIZADA');
    const canceladas = mesVendas.filter(v => v.statusVenda === 'CANCELADA');
    const receita = finalizadas.reduce((s, v) => s + (v.total || 0), 0);
    const ticket = finalizadas.length > 0 ? receita / finalizadas.length : 0;
    const cancelVal = canceladas.reduce((s, v) => s + (v.total || 0), 0);
    document.getElementById('rel-receita').textContent = brl(receita);
    document.getElementById('rel-receita-sub').textContent = `${finalizadas.length} vendas no mês`;
    document.getElementById('rel-qtd').textContent = finalizadas.length;
    document.getElementById('rel-ticket').textContent = `Ticket médio: ${brl(ticket)}`;
    document.getElementById('rel-cancel').textContent = canceladas.length;
    document.getElementById('rel-cancel-sub').textContent = `${brl(cancelVal)} cancelados`;
    document.getElementById('rel-chart-title').textContent = `Vendas por dia — ${MONTHS[thisMonth]}/${thisYear}`;
    const daysInMonth = new Date(thisYear, thisMonth + 1, 0).getDate();
    const days = {};
    for (let i = 1; i <= daysInMonth; i++) days[i] = 0;
    mesVendas.forEach(v => { if (v.momento) { const day = new Date(v.momento).getDate(); days[day] = (days[day]||0) + 1; } });
    renderBarChart('rel-chart', Object.entries(days).map(([day, c]) => ({ label: day, value: c, highlight: new Date().getDate() === parseInt(day) })), 160);

  } else {
    const weeks = {};
    vendas.forEach(v => {
      if (!v.momento) return;
      const dd = new Date(v.momento);
      const key = `${dd.getFullYear()}-W${getWeekNumber(dd)}`;
      if (!weeks[key]) weeks[key] = 0;
      if (v.statusVenda === 'FINALIZADA') weeks[key] += v.total || 0;
    });
    const last8 = Object.entries(weeks).sort().slice(-8);
    document.getElementById('rel-chart-title').textContent = 'Fluxo de Caixa — últimas 8 semanas';
    const totalFluxo = last8.reduce((s, [, v]) => s + v, 0);
    document.getElementById('rel-receita').textContent = brl(totalFluxo);
    document.getElementById('rel-receita-sub').textContent = 'últimas 8 semanas';
    document.getElementById('rel-qtd').textContent = vendas.filter(v => v.statusVenda === 'FINALIZADA').length;
    document.getElementById('rel-ticket').textContent = 'Total de vendas';
    document.getElementById('rel-cancel').textContent = vendas.filter(v => v.statusVenda === 'CANCELADA').length;
    document.getElementById('rel-cancel-sub').textContent = 'cancelamentos totais';
    renderBarChart('rel-chart', last8.map(([k, v]) => ({ label: k.split('-')[1], value: v, highlight: false })), 160);
  }
}

function renderBarChart(elId, data, height) {
  const el = document.getElementById(elId);
  el.style.height = height + 'px';
  const maxVal = Math.max(...data.map(d => d.value), 1);
  el.innerHTML = data.map(d => {
    const pct = Math.max((d.value / maxVal) * 100, d.value > 0 ? 8 : 2);
    return `<div class="bar-wrap">
      <div class="bar ${d.highlight ? 'today' : ''}" style="height:${pct}%"></div>
      <div class="bar-label">${d.label}</div>
    </div>`;
  }).join('');
}

function getWeekNumber(d) {
  const onejan = new Date(d.getFullYear(), 0, 1);
  return Math.ceil((((d - onejan) / 86400000) + onejan.getDay() + 1) / 7);
}

let usuariosList = [];

async function loadUsuarios() {
  try {
    usuariosList = await api('GET', '/funcionarios') || [];
    renderUsuarios();
  } catch (e) {
    document.getElementById('usuarios-body').innerHTML = `<tr><td colspan="7" class="empty-state"><p>Erro: ${e.message}</p></td></tr>`;
  }
}

function renderUsuarios() {
  const tbody = document.getElementById('usuarios-body');
  if (usuariosList.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="empty-state"><div class="empty-icon">👥</div><p>Nenhum usuário cadastrado</p></td></tr>`;
    return;
  }
  const cargoLabel = { GERENTE: 'Gerente', OPERADOR: 'Operador', ESTOQUISTA: 'Estoquista' };
  const cargoBadge = { GERENTE: 'badge-purple', OPERADOR: 'badge-blue', ESTOQUISTA: 'badge-orange' };
  tbody.innerHTML = usuariosList.map(f => {
    const isSelf = f.idFuncionario === user.idFuncionario;
    return `<tr>
      <td><span style="color:var(--text-muted);font-weight:600">#${String(f.idFuncionario).padStart(3,'0')}</span></td>
      <td>
        <strong>${f.nome}</strong>
        ${isSelf ? '<span class="badge badge-green" style="font-size:10px;margin-left:6px">você</span>' : ''}
      </td>
      <td>${f.email}</td>
      <td>${f.cpf || '—'}</td>
      <td><span class="badge ${cargoBadge[f.cargo] || 'badge-gray'}">${cargoLabel[f.cargo] || f.cargo}</span></td>
      <td>${brl(f.salario)}</td>
      <td>
        <button class="btn btn-sm btn-outline" onclick="openFuncionarioModal(${f.idFuncionario})" style="margin-right:4px">✏</button>
        ${!isSelf ? `<button class="btn btn-sm btn-danger" onclick="confirmDelete('funcionario',${f.idFuncionario},'${f.nome.replace(/'/g,"\\'")}')">🗑</button>` : '<span style="color:var(--text-muted);font-size:12px">—</span>'}
      </td>
    </tr>`;
  }).join('');
}

function openFuncionarioModal(id) {
  document.getElementById('modal-funcionario-title').textContent = id ? 'Editar Usuário' : 'Novo Usuário';
  document.getElementById('mfunc-id').value = id || '';
  const f = id ? usuariosList.find(x => x.idFuncionario === id) : null;
  document.getElementById('mfunc-nome').value = f?.nome || '';
  document.getElementById('mfunc-cpf').value = f?.cpf || '';
  document.getElementById('mfunc-email').value = f?.email || '';
  document.getElementById('mfunc-telefone').value = f?.telefone || '';
  document.getElementById('mfunc-cargo').value = f?.cargo || 'OPERADOR';
  document.getElementById('mfunc-salario').value = f?.salario || '';
  document.getElementById('mfunc-senha').value = '';
  document.getElementById('mfunc-senha-label').textContent = id ? 'Nova Senha (em branco = manter atual)' : 'Senha *';
  openModal('modal-funcionario');
}

async function saveFuncionario() {
  const id = document.getElementById('mfunc-id').value;
  const nome = document.getElementById('mfunc-nome').value.trim();
  const cpf = document.getElementById('mfunc-cpf').value.trim();
  const email = document.getElementById('mfunc-email').value.trim();
  const telefone = document.getElementById('mfunc-telefone').value.trim();
  const cargo = document.getElementById('mfunc-cargo').value;
  const salario = parseFloat(document.getElementById('mfunc-salario').value);
  const senha = document.getElementById('mfunc-senha').value;

  if (!nome || !cpf || !email || !cargo || isNaN(salario)) {
    toast('Preencha todos os campos obrigatórios', 'error'); return;
  }
  if (!id && !senha) { toast('Informe a senha', 'error'); return; }
  if (senha && senha.length < 4) { toast('A senha deve ter pelo menos 4 caracteres', 'error'); return; }

  const body = { nome, cpf, email, telefone, cargo, salario, dataDeAdmissao: null };
  if (senha) body.senha = senha;

  try {
    if (id) { await api('PUT', `/funcionarios/${id}`, body); toast('Usuário atualizado!', 'success'); }
    else { await api('POST', '/funcionarios', body); toast('Usuário cadastrado!', 'success'); }
    closeModal('modal-funcionario');
    loadUsuarios();
  } catch (e) { toast('Erro: ' + e.message, 'error'); }
}

function confirmDelete(type, id, name) {
  document.getElementById('confirm-msg').textContent = `Tem certeza que deseja excluir "${name}"? Esta ação não pode ser desfeita.`;
  document.getElementById('confirm-btn').onclick = () => doDelete(type, id);
  openModal('modal-confirm');
}

async function doDelete(type, id) {
  const paths = {
    produto:     `/produtos/${id}`,
    cliente:     `/clientes/${id}`,
    fornecedor:  `/fornecedores/${id}`,
    funcionario: `/funcionarios/${id}`,
  };
  const reloads = {
    produto:     loadProdutos,
    cliente:     loadClientes,
    fornecedor:  loadFornecedores,
    funcionario: loadUsuarios,
  };
  try {
    await api('DELETE', paths[type]);
    toast('Excluído com sucesso!', 'success');
    closeModal('modal-confirm');
    reloads[type]?.();
  } catch (e) { toast('Erro ao excluir: ' + e.message, 'error'); }
}

navigate(allowedPages[0]);
