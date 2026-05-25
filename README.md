# Easy Market

API REST para gerenciamento de um mercadinho/PDV, desenvolvida em Java 21 com Spring Boot.

## 1. Descrição do projeto

O Easy Market resolve a dor de pequenos mercadinhos que ainda controlam produtos, vendas, compras, pagamentos e estoque de forma manual ou espalhada em planilhas. A solução centraliza o cadastro de produtos, clientes, fornecedores, funcionários, vendas, pagamentos, compras e movimentações de estoque em uma API REST organizada em camadas.

## 2. Integrantes do grupo

- Marcos Vinicius
- Lucas Guilherme
- Silvano Antonio
- Gabriel Cavalcante
- Hewerton Manuel

## 3. Tecnologias utilizadas

- Java 21
- Spring Boot 3.3.0
- Spring Web
- Spring Data JPA
- MySQL
- Maven
- Springdoc OpenAPI / Swagger

## 4. Arquitetura

O projeto segue arquitetura em camadas:

```text
src/main/java/com/aprendendoSpring/course
├── config
├── controllers
├── dtos
├── entities
├── exceptions
├── repositories
└── services
```

## 5. Entidades principais

As entidades seguem o diagrama de classes enviado:

- Cliente
- Produto
- Funcionario
- Estoque
- Fornecedor
- Venda
- ItemVenda
- Pagamento
- Compra
- ItemCompra
- MovimentacaoEstoque
- Estoquista
- Operador
- Gerente

## 6. Features entregues

- CRUD completo de Produto
- CRUD completo de Cliente
- CRUD completo de Funcionário
- CRUD completo de Fornecedor
- CRUD completo de Estoque
- Registro e finalização/cancelamento de Venda
- Registro e processamento de Pagamento
- Registro e finalização de Compra
- Entrada e saída de Estoque
- DTOs para entrada e saída dos endpoints
- Validações na camada Service
- Tratamento centralizado de exceções
- Swagger configurado
- Consultas nativas relevantes com `@Query(nativeQuery = true)`
- Collection do Postman em `/postman`

## 7. Consultas nativas implementadas

As consultas nativas estão documentadas em:

```text
docs/consultas-nativas.md
```

Foram implementadas consultas para:

- produtos próximos do vencimento;
- produtos sem estoque;
- vendas por status;
- pagamentos pendentes;
- compras por fornecedor;
- movimentações por tipo.

## 8. Como executar localmente

### 8.1. Criar banco de dados

No MySQL Workbench, execute:

```sql
CREATE DATABASE easy_market
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

### 8.2. Conferir configuração do banco

Arquivo:

```text
src/main/resources/application.properties
```

Configuração padrão:

```properties
spring.datasource.url=jdbc:mysql://127.0.0.1:3307/easy_market?useSSL=false&serverTimezone=America/Recife&allowPublicKeyRetrieval=true
spring.datasource.username=ProjetoFacol
spring.datasource.password=
```

Ajuste usuário, senha e porta conforme o seu MySQL local.

### 8.3. Rodar o projeto

No terminal, dentro da pasta do projeto:

```bash
mvn spring-boot:run
```

Ou execute a classe:

```text
CourseApplication.java
```

## 9. Swagger

Depois de rodar o projeto, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

## 10. Postman

A collection está em:

```text
postman/easy-market.postman_collection.json
```

Ela usa a variável:

```text
{{base_url}}
```

Valor sugerido:

```text
http://localhost:8080
```

## 11. Diagramas

Os arquivos estão na pasta `/docs`:

- `DIAGRAMA_DE_CLASSE_EASY_MARKET.pdf`
- `diagrama-casos-uso.puml`

## 12. Observação importante

As entidades seguem o diagrama de classes. Já DTOs, Controllers, Services, Repositories, Swagger e queries nativas foram mantidos porque fazem parte dos requisitos técnicos da API REST.
