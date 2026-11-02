# Calculadora de trastes — API Clojure

API REST para calcular a posição dos trastes de um violão a partir do comprimento da escala (em milímetros), usando temperamento igual de 12 semitons.

## Requisitos

- Java 17 ou superior
- [Clojure CLI](https://clojure.org/guides/install_clojure)

## Executar

```bash
clojure -M:run
```

O servidor inicia em `http://localhost:3000`. A porta pode ser alterada com a variável de ambiente `PORT`.

## Endpoints

| Método | Rota | Descrição |
| --- | --- | --- |
| GET | `/api/health` | Verifica se a API está disponível |
| GET | `/api/trastes?escala=650&trastes=22` | Calcula os trastes via parâmetros na URL |
| POST | `/api/trastes` | Calcula os trastes via JSON |

`escala` é obrigatória (100–1500 mm). `trastes` é opcional (1–36, padrão 22). Valores com vírgula decimal também são aceitos como texto.

### Requisição GET

```bash
curl "http://localhost:3000/api/trastes?escala=650&trastes=22"
```

### Requisição POST

```bash
curl -X POST "http://localhost:3000/api/trastes" \
  -H "Content-Type: application/json" \
  -d '{"escala":650,"trastes":22}'
```

### Resposta

```json
{
  "escala_mm": 650.0,
  "formula": "d_n = L * (1 - 1 / 2^(n/12))",
  "quantidade_trastes": 22,
  "trastes": [
    {
      "traste": 1,
      "espacamento_mm": 36.48,
      "distancia_ponte_mm": 613.52,
      "distancia_pestana_mm": 36.48
    }
  ]
}
```

O exemplo foi abreviado: a resposta real inclui todos os trastes solicitados.

Retornos de erro são JSON: `400` para entrada inválida, `404` para rota inexistente e `500` para erro interno. A API permite CORS de qualquer origem (sem credenciais).

**Observação:** o GitHub armazena o código; para disponibilizar uma URL pública da API, é necessário executar o servidor em uma máquina ou plataforma com suporte a Java.
