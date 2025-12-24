import React from 'react';
import { Menu, Row, Col } from 'antd';
import { Link, useLocation } from 'react-router-dom';
import UserMenu from './UserMenu';

export default function AppMenu() {
    const location = useLocation();

    const items = [
        { key: '/', label: <Link to="/">Movies</Link> },
        { key: '/directors', label: <Link to="/directors">Directors</Link> },
        { key: '/recommendations', label: <Link to="/recommendations">Recommendations</Link> },
        { key: '/prefs', label: <Link to="/prefs">Preferences</Link> },
    ];

    return (
        <Row align="middle" justify="space-between" style={{ padding: '0 16px' }}>
            {/* flex="auto" чтобы меню занимало всё доступное место и не сворачивалось в ... */}
            <Col flex="auto" style={{ minWidth: 0 }}>
                <Menu
                    theme="dark"
                    mode="horizontal"
                    selectedKeys={[location.pathname]}
                    items={items}
                    style={{ display: 'flex', alignItems: 'center' }}
                />
            </Col>

            <Col>
                <UserMenu />
            </Col>
        </Row>
    );
}