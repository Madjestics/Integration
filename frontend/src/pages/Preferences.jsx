import React from 'react';
import { Form, Input, Button, message } from 'antd';
import { apiFetch } from '../services/api';


export default function Preferences(){
    const [form] = Form.useForm();


    async function onSave(values){
        try{
            const userId = localStorage.getItem('userId') || values.userId;
            const body = { userId, preferredGenres: values.preferredGenres.split(',').map(s=>s.trim()) };
            const res = await apiFetch('/api/preferences', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) }, 'prefs');
            message.success('Saved');
            localStorage.setItem('userId', res.userId || userId);
        }catch(e){ message.error(e.message); }
    }


    return (
        <Form form={form} layout="vertical" onFinish={onSave} initialValues={{ userId: localStorage.getItem('userId') || '' }}>
            <Form.Item name="userId" label="User ID"><Input/></Form.Item>
            <Form.Item name="preferredGenres" label="Prefered genres (comma separated)"><Input/></Form.Item>
            <Form.Item><Button type="primary" htmlType="submit">Save</Button></Form.Item>
        </Form>
    );
}