import React from 'react';
import { Dropdown, Avatar, Menu, Button, Space } from 'antd';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../services/auth';

export default function UserMenu() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    if (!user) {
        return (
            <Space>
                <Button type="default" onClick={() => navigate('/register')}>Register</Button>
                <Button type="primary" onClick={() => navigate('/login')}>Login</Button>
            </Space>
        );
    }

    const items = [
        {
            key: 'profile',
            label: 'Profile',
            onClick: () => navigate('/profile'),
        },
        {
            key: 'logout',
            label: 'Logout',
            onClick: () => { logout(); navigate('/'); },
        },
    ];

    return (
        <Dropdown menu={{ items }} placement="bottomRight">
            <div style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
                <Avatar>{user.username?.[0]?.toUpperCase() || 'U'}</Avatar>
                <span style={{ color: '#fff' }}>{user.username}</span>
            </div>
        </Dropdown>
    );
}