import React from 'react';
import { Form, Input, Button, message } from 'antd';
import { apiFetch } from '../services/api';
import { useNavigate } from 'react-router-dom';


export default function Register(){
    const navigate = useNavigate();
    async function onFinish(values){
        try{
            await apiFetch('/auth/register', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(values) });
            message.success('Registered');
            navigate('/login');
        }catch(e){ message.error(e.message); }
    }


    return (
        <div style={{ maxWidth: 480 }}>
            <h2>Register</h2>
            <Form onFinish={onFinish} layout="vertical">
                <Form.Item name="username" label="Username" rules={[{required:true}]}><Input/></Form.Item>
                <Form.Item name="password" label="Password" rules={[{required:true}]}><Input.Password/></Form.Item>
                <Form.Item name="email" label="Email"><Input/></Form.Item>
                <Form.Item><Button type="primary" htmlType="submit">Register</Button></Form.Item>
            </Form>
        </div>
    );
}