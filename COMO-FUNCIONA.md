# Como o Easy Market Funciona

Este documento explica como o projeto foi construído, o que cada parte faz e como o frontend e o backend se comunicam.

---

## O que é esse projeto?

O Easy Market é um sistema de gestão para pequenos mercadinhos. Ele permite:
- Cadastrar e vender produtos pelo PDV (Ponto de Venda)
- Controlar o estoque de cada produto
- Gerenciar clientes, fornecedores e funcionários
- Ver relatórios de vendas e movimentações

O sistema tem dois lados: o **backend** (API feita em Java/Spring Boot) e o **frontend** (telas feitas em HTML, CSS e JavaScript puro).

---

## O Backend

O backend é uma API REST. Isso significa que ele só responde perguntas — ele não gera HTML nem nada visual. Quando alguém chama `GET /produtos`, ele devolve uma lista de produtos em formato JSON. Simples assim.

### Tecnologias

- **Java 21** com **Spring Boot 3.3.0**
- **MySQL** como banco de dados
- **Spring Data JPA / Hibernate** para se comunicar com o banco
- **Springdoc OpenAPI** para gerar o Swagger automaticamente

### Como o código está organizado

O código Java fica em `src/main/java/com/aprendendoSpring/course/` e segue uma estrutura em camadas bem definida:

```
config/          → configurações gerais (Swagger, seed de dados)
controllers/     → recebem as requisições HTTP e devolvem respostas
dtos/            → definem o formato dos dados que entram e saem da API
entities/        → representam as tabelas do banco de dados
enums/           → valores fixos como status e tipos
exceptions/      → tratamento centralizado de erros
repositories/    → fazem as consultas no banco
services/        → onde fica a lógica de negócio de verdade
```

### O caminho de uma requisição

Quando o frontend faz uma chamada, ela passa por essas camadas nessa ordem:

```
Requisição HTTP
      ↓
  Controller   →  recebe a requisição, chama o Service
      ↓
   Service     →  valida os dados, aplica as regras de negócio
      ↓
 Repository    →  consulta ou salva no banco via JPA
      ↓
   Banco MySQL →  onde os dados ficam guardados de verdade
      ↓
  DTO Response →  o Controller transforma o resultado num formato limpo
      ↓
Resposta JSON  →  vai de volta pro frontend
```

### Exemplo prático — criar uma venda

1. O frontend manda `POST /vendas` com um JSON contendo o clienteId e os itens
2. `VendaController` recebe e passa pro `VendaService`
3. `VendaService` verifica se o cliente existe, se tem estoque disponível, calcula o total
4. Se tudo certo, salva a venda no banco via `VendaRepository`
5. O `VendaService` também chama o `EstoqueService` pra baixar a quantidade de cada produto vendido
6. Devolve um `VendaResponseDTO` com os dados da venda criada

### Entidades (tabelas do banco)

Cada entidade vira uma tabela no MySQL automaticamente pelo Hibernate:

| Entidade | O que representa |
|---|---|
| Cliente | Pessoas que compram no mercado |
| Produto | Itens à venda com nome, preço, validade e categoria |
| Estoque | Controla a quantidade de cada produto e onde ele está |
| Venda | Registro de uma venda feita no PDV |
| ItemVenda | Cada produto dentro de uma venda |
| Pagamento | O pagamento associado a uma venda |
| Compra | Pedido feito a um fornecedor |
| ItemCompra | Cada produto dentro de uma compra |
| Fornecedor | Empresas que fornecem os produtos |
| Funcionario | Classe base para os tipos de usuário |
| Gerente | Tem acesso total ao sistema |
| Operador | Faz vendas e atende clientes |
| Estoquista | Gerencia estoque e fornecedores |
| MovimentacaoEstoque | Log de todas as entradas e saídas do estoque |

### Herança de Funcionário

`Funcionario` é a classe base. `Gerente`, `Operador` e `Estoquista` herdam dela usando a estratégia `JOINED` do JPA — isso significa que cada tipo tem sua própria tabela no banco, mas todas compartilham a tabela `funcionario` com os campos em comum (nome, email, senha, cargo).

### Autenticação

Não tem Spring Security. A autenticação é simples:

1. O frontend manda `POST /auth/login` com email e senha
2. O `AuthController` chama o `FuncionarioService` que busca o funcionário pelo email e compara a senha
3. Se bater, devolve os dados do funcionário (incluindo o cargo)
4. O frontend guarda isso no `sessionStorage` do browser

### Os DTOs

DTOs (Data Transfer Objects) são records Java que definem exatamente o que entra e sai da API. Isso evita que a entidade JPA seja exposta diretamente (o que poderia vazar dados internos ou causar loops infinitos na serialização).

Por exemplo, `ProdutoResponseDTO` tem só os campos que o frontend precisa ver — não expõe campos internos da entidade.

### Tratamento de erros

Toda exceção lançada nos services é capturada pelo `ResourceExceptionHandler` (anotado com `@ControllerAdvice`). Ele transforma a exceção num JSON padronizado com código, mensagem e timestamp — assim o frontend sempre sabe o que deu errado.

### Swagger

Após rodar o projeto, dá pra testar todos os endpoints pelo navegador em:
```
http://localhost:8080/swagger-ui/index.html
```

---

## O Frontend

O frontend é um SPA (Single Page Application) feito sem nenhum framework — só HTML, CSS e JavaScript puro. Tem duas telas: o login e o painel principal.

### Arquivos

```
static/
├── index.html    →  tela de login
├── app.html      →  painel principal (todas as seções ficam aqui)
├── app.js        →  toda a lógica do frontend
└── styles.css    →  estilos visuais
```

### Como o SPA funciona

O `app.html` tem várias `<section>` com IDs como `page-dashboard`, `page-pdv`, `page-estoque`, etc. Só uma delas fica visível por vez (com a classe `active`). Quando o usuário clica num item do menu, o JavaScript esconde a seção atual e mostra a nova — sem recarregar a página.

```javascript
function navigate(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById('page-' + page).classList.add('active');
    // carrega os dados da página
    loaders[page]?.();
}
```

### Como o frontend chama a API

Tem uma função `api()` que centraliza todas as chamadas HTTP:

```javascript
async function api(method, path, body) {
    const res = await fetch(path, {
        method,
        headers: { 'Content-Type': 'application/json' },
        body: body ? JSON.stringify(body) : undefined
    });
    if (!res.ok) throw new Error(await res.text());
    return res.json();
}
```

Toda função que precisa de dados usa ela. Por exemplo:
```javascript
const produtos = await api('GET', '/produtos');
const venda = await api('POST', '/vendas', { clienteId, itens });
```

### Controle de acesso por cargo

O frontend sabe quais páginas e quais ações cada cargo pode fazer:

```javascript
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
```

Itens de menu que o cargo não pode acessar ficam ocultos. Botões de ação (editar, criar, deletar) só aparecem se `canWrite(pagina)` retornar verdadeiro.

### Módulos do frontend

Cada seção do sistema tem suas próprias funções de carregamento e renderização:

| Seção | Função principal | O que faz |
|---|---|---|
| Dashboard | `loadDashboard()` | Carrega estatísticas, gráfico e últimas vendas |
| PDV | `loadPDV()` | Carrega produtos, monta o carrinho, finaliza a venda |
| Produtos | `loadProdutos()` | Lista produtos, abre modal de criação/edição |
| Clientes | `loadClientes()` | CRUD de clientes |
| Fornecedores | `loadFornecedores()` | Lista fornecedores em cards |
| Estoque | `loadEstoque()` | Mostra estoque + produtos sem estoque com botão de cadastrar |
| Relatórios | `loadRelatorios()` | Consultas de vendas e movimentações |
| Usuários | `loadUsuarios()` | Gerencia funcionários |

### Notificações (toasts)

Quando uma ação termina, aparece uma notificação no canto da tela:

```javascript
function toast(msg, type = 'default') {
    const el = document.createElement('div');
    el.className = `toast ${type}`;
    el.textContent = msg;
    document.body.appendChild(el);
    setTimeout(() => el.remove(), 3000);
}
```

Verde pra sucesso, vermelho pra erro.

### Sessão do usuário

Depois do login, os dados do funcionário ficam no `sessionStorage`:

```javascript
sessionStorage.setItem('em_user', JSON.stringify({
    idFuncionario: data.idFuncionario,
    email: data.email,
    name: data.nome,
    cargo: data.cargo,
    role: cargoLabels[data.cargo]
}));
```

O `app.js` lê isso logo no começo. Se não tiver ninguém logado, redireciona pra `index.html`.

---

## Frontend + Backend juntos

### Como eles se comunicam

O frontend e o backend rodam no mesmo servidor (porta 8080). O Spring Boot serve os arquivos estáticos (`index.html`, `app.html`, etc.) direto da pasta `static/`. Quando o JavaScript faz um `fetch('/produtos')`, ele bate no controller do Spring Boot no mesmo servidor — sem CORS, sem complicação.

```
Browser
  └── GET http://localhost:8080/app.html  →  Spring serve o HTML
  └── GET http://localhost:8080/app.js    →  Spring serve o JS
  └── GET http://localhost:8080/produtos  →  Spring processa e devolve JSON
  └── POST http://localhost:8080/vendas   →  Spring salva e devolve JSON
```

### Fluxo completo de uma venda no PDV

```
1. Usuário abre o PDV no browser
        ↓
2. loadPDV() chama GET /produtos  →  lista produtos na tela
        ↓
3. Usuário clica num produto   →  vai pro carrinho
        ↓
4. Usuário clica em Finalizar  →  savePDV() monta o JSON da venda
        ↓
5. POST /vendas  →  VendaController → VendaService
        ↓
6. VendaService verifica estoque, cria a venda, baixa o estoque
        ↓
7. Resposta JSON com a venda criada
        ↓
8. Frontend limpa o carrinho, mostra toast "Venda finalizada!"
```

### Fluxo de login

```
1. Usuário digita email e senha no index.html
        ↓
2. POST /auth/login  →  AuthController → FuncionarioService
        ↓
3. FuncionarioService busca pelo email, compara senha
        ↓
4. Devolve FuncionarioResponseDTO com cargo
        ↓
5. Frontend salva no sessionStorage e redireciona pro app.html
        ↓
6. app.js lê o cargo, esconde os menus que o usuário não pode acessar
```

### Criação de produto + estoque automático

Quando o gerente ou estoquista cria um produto novo, o frontend pede localização e quantidade máxima junto. Depois de criar o produto, automaticamente chama `POST /estoques` pra já deixar o produto no estoque — sem precisar fazer isso em duas etapas.

---

## Como rodar o projeto

### Pré-requisitos

- Java 21
- Maven
- MySQL rodando na porta 3306

### Banco de dados

```sql
CREATE DATABASE easy_market CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Configuração

Verifique o arquivo `src/main/resources/application.properties` e ajuste usuário/senha se necessário:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/easy_market
spring.datasource.username=root
spring.datasource.password=root
```

### Rodando

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

Acesse: `http://localhost:8080`

Login padrão: `admin@easymarket.com` / `admin1234`

---

## Estrutura de pastas resumida

```
WebProject/
├── src/
│   ├── main/
│   │   ├── java/.../course/
│   │   │   ├── config/          controllers/    dtos/
│   │   │   ├── entities/        exceptions/     repositories/
│   │   │   └── services/        CourseApplication.java
│   │   └── resources/
│   │       ├── static/          (index.html, app.html, app.js, styles.css)
│   │       └── application.properties
│   └── test/
├── docs/                        (diagramas e requisitos)
├── postman/                     (collection pra testar a API)
├── pom.xml
└── README.md
```
