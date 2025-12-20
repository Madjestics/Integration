import React from 'react';
import { Form, Input, Button, message } from 'antd';
import { useAuth } from '../services/auth.jsx';
import { useNavigate } from 'react-router-dom';


export default function Login(){
    const { login } = useAuth();
    const navigate = useNavigate();


    async function onFinish(values){
        try{
            await login(values.username, values.password);
            message.success('Logged in');
            navigate('/');
        }catch(e){ message.error(e.message); }
    }


    return (
        <div style={{ maxWidth: 420 }}>
            <h2>Login</h2>
            <Form onFinish={onFinish} layout="vertical">
                <Form.Item name="username" label="Username" rules={[{required:true}]}><Input/></Form.Item>
                <Form.Item name="password" label="Password" rules={[{required:true}]}><Input.Password/></Form.Item>
                <Form.Item><Button type="primary" htmlType="submit">Login</Button></Form.Item>
            </Form>
        </div>
    );
}