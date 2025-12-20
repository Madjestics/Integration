export const API_BASE_MAIN = import.meta.env.VITE_API_BASE_MAIN || 'http://localhost:8080';

export const API_BASE_PREFS = import.meta.env.VITE_API_BASE_PREFS || 'http://localhost:8081';


export async function apiFetch(path, opts = {}, service = 'main') {
    const token = localStorage.getItem('token');
    const headers = opts.headers ? { ...opts.headers } : {};
    if (token) headers['Authorization'] = `Bearer ${token}`;


    const base = service === 'prefs' ? API_BASE_PREFS : API_BASE_MAIN;
    const res = await fetch(base + path, { ...opts, headers });
    if (!res.ok) {
        const txt = await res.text().catch(() => null);
        const err = txt || res.statusText || 'Request failed';
        throw new Error(err);
    }
    const ct = res.headers.get('content-type') || '';
    if (ct.includes('application/json')) return res.json();
    return res.blob ? res.blob() : res;
}