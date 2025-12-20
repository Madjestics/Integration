import React from 'react';
import { Layout } from 'antd';
import AppMenu from './AppMenu';


const { Header, Content, Footer } = Layout;


export default function AppLayout({ children }) {
    return (
        <Layout style={{ minHeight: '100vh' }}>
            <Header style={{ padding: 0 }}>
                <AppMenu />
            </Header>
            <Content className="app-content">{children}</Content>
            <Footer style={{ textAlign: 'center' }}>Movie Platform ©2025</Footer>
        </Layout>
    );
}