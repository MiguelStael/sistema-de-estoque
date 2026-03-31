import http from 'k6/http';
import { check } from 'k6';

export const options = {
    scenarios: {
        rate_limiting_test: {
            executor: 'constant-arrival-rate',
            rate: 200,
            timeUnit: '1m',
            duration: '30s',
            preAllocatedVUs: 10,
            maxVUs: 20,
            exec: 'testRateLimiting',
        },
        concurrency_test: {
            executor: 'per-vu-iterations',
            vus: 50,
            iterations: 1,
            startTime: '5s',
            exec: 'testConcurrency',
        }
    },
    thresholds: {
        http_req_failed: ['rate<0.80'],
    }
};

const BASE_URL = 'http://localhost:8080';
const PRODUTO_TESTE_ID = 1;

export function testRateLimiting() {
    const res = http.get(`${BASE_URL}/estoque/produtos/cardapio`);
    
    check(res, {
        'rateLimiting_status_200_or_429': (r) => r.status === 200 || r.status === 429,
        'rateLimiting_received_429': (r) => r.status === 429,
    });
}

export function testConcurrency() {
    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const payload = JSON.stringify({
        identificacao: 'K6 Concurrency Test',
        observacao: 'K6 Stress Test',
        tipoPedido: 'PRESENCIAL',
        itens: [
            {
                produtoId: PRODUTO_TESTE_ID,
                quantidade: 1
            }
        ]
    });

    const res = http.post(`${BASE_URL}/api/pedidos`, payload, params);
    
    check(res, {
        'concurrency_status_valid': (r) => [200, 400, 404, 409].includes(r.status),
        'concurrency_conflict_409': (r) => r.status === 409,
    });
}
