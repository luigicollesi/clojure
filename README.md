# Calculadora de Trastes

Monorepo minimalista com dois projetos independentes:

- **`api/`** — API REST em Clojure (Ring/Jetty), com cálculos e validação.
- **`web/`** — frontend em Node.js e HTML/CSS/JavaScript, sem dependências npm.

## Executar localmente

Requisitos: Java 17+, [Clojure CLI](https://clojure.org/guides/install_clojure) e Node.js 22+ (recomendado: versão LTS).

Terminal 1 — API Clojure (porta 3000):

```bash
cd api
clojure -M:run
```

Terminal 2 — frontend Node.js (porta 3001):

```bash
cd web
npm start
```

Abra **http://localhost:3001**. O frontend chama a API pelo proxy Node, sem depender de CORS no navegador.

## Configuração

- `PORT`: porta do servidor Clojure (padrão `3000`).
- `WEB_PORT`: porta do servidor Node (padrão `3001`).
- `API_URL`: URL do backend acessível pelo Node (padrão `http://127.0.0.1:3000`).

## API

| Método | Rota | Parâmetros |
| --- | --- | --- |
| GET | `/api/health` | Nenhum |
| GET | `/api/trastes?escala=650&trastes=22` | `escala` obrigatória, `trastes` opcional |
| POST | `/api/trastes` | JSON `{"escala":650,"trastes":22}` |

Escala: 100–1500 mm. Trastes: 1–36, padrão 22. As medidas são retornadas em milímetros com duas casas decimais.

O proxy do frontend expõe apenas os GETs usados pela interface; o POST continua disponível diretamente na API Clojure.

**Nota:** hospedar os arquivos no GitHub não coloca os servidores no ar. É necessário executar cada serviço para disponibilizar a aplicação.
