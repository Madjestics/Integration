import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, message } from 'antd';
import { apiFetch } from '../services/api';

export default function Directors(){
    const [list, setList] = useState([]);
    const [loading, setLoading] = useState(true);
    const [open, setOpen] = useState(false);
    const [form] = Form.useForm();

    async function load(){
        setLoading(true);
        try{
            const data = await apiFetch('/api/director', {}, 'main');
            setList(Array.isArray(data) ? data : []);
        }catch(e){
            message.error(e.message);
        }
        setLoading(false);
    }
    useEffect(()=>{ load(); },[]);

    async function onCreate(values){
        try{
            await apiFetch('/api/director', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(values)
            }, 'main');
            message.success('Created');
            setOpen(false);
            load();
        } catch(e) {
            message.error(e.message);
        }
    }

    const columns = [
        { title: 'Id', dataIndex: 'id', key: 'id' },
        { title: 'Fio', dataIndex: 'fio', key: 'fio', ellipsis: true },
    ];

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                <h2>Directors</h2>
                <Button type="primary" onClick={()=>setOpen(true)}>Add Director</Button>
            </div>

            <Table
                rowKey={record => record.id}
                dataSource={list}
                loading={loading}
                columns={columns}
                pagination={{ pageSize: 10 }}
            />

            <Modal title="Add Director" open={open} onCancel={()=>setOpen(false)} footer={null}>
                <Form form={form} layout="vertical" onFinish={onCreate}>
                    <Form.Item name="id" label="id" rules={[{required:true}]}><Input/></Form.Item>
                    <Form.Item name="fio" label="Fio"><Input.TextArea rows={4}/></Form.Item>
                    <Form.Item>
                        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 8 }}>
                            <Button onClick={()=>setOpen(false)}>Cancel</Button>
                            <Button htmlType="submit" type="primary">Save</Button>
                        </div>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
}