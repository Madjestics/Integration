import React, { createContext, useContext, useEffect, useState } from 'react';
import { apiFetch } from './api';


const AuthContext = createContext();


export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);


    useEffect(() => {
        const token = localStorage.getItem('token');
        if (!token) return;
        apiFetch('/auth/info')
            .then(u => setUser(u))
            .catch(() => {
                localStorage.removeItem('token');
                localStorage.removeItem('userId');
                setUser(null);
            });
    }, []);


    async function login(username, password) {
        const data = await apiFetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password }),
        });
        localStorage.setItem('token', data.token);
        localStorage.setItem('userId', data.userId);
        document.cookie = `token=${data.token}; Path=/; SameSite=Lax`;
        const info = await apiFetch('/auth/info');
        setUser(info);
        return info;
    }


    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('userId');
        setUser(null);
    }


    return (
        <AuthContext.Provider value={{ user, setUser, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
}


export function useAuth() {
    return useContext(AuthContext);
}