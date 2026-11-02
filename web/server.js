import { createServer } from 'node:http';
import { readFile } from 'node:fs/promises';

const port = Number(process.env.WEB_PORT || 3001);
const apiUrl = process.env.API_URL || 'http://127.0.0.1:3000';
const html = await readFile(new URL('./public/index.html', import.meta.url));

createServer(async (req, res) => {
  const url = new URL(req.url, 'http://localhost');
  if (req.method === 'GET' && url.pathname === '/') {
    res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' }).end(html);
    return;
  }

  if (req.method === 'GET' && ['/api/trastes', '/api/health'].includes(url.pathname)) {
    try {
      const upstream = await fetch(new URL(url.pathname + url.search, apiUrl), {
        signal: AbortSignal.timeout(5000)
      });
      res.writeHead(upstream.status, {
        'Content-Type': upstream.headers.get('content-type') || 'application/json; charset=utf-8',
        'Cache-Control': 'no-store'
      }).end(await upstream.text());
    } catch {
      res.writeHead(502, { 'Content-Type': 'application/json; charset=utf-8' })
        .end(JSON.stringify({ erro: 'API indisponível. Inicie o servidor Clojure.' }));
    }
    return;
  }

  res.writeHead(404, { 'Content-Type': 'text/plain; charset=utf-8' }).end('Página não encontrada.');
}).listen(port, '0.0.0.0', () => console.log('Frontend em http://localhost:' + port));
