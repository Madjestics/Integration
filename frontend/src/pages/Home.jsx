import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Form, Input, message, Select } from 'antd';
import { apiFetch } from '../services/api';
import { useNavigate } from 'react-router-dom';

export default function Home() {
    const [movies, setMovies] = useState([]);
    const [loading, setLoading] = useState(false);
    const [showCreate, setShowCreate] = useState(false);
    const [directors, setDirectors] = useState([]);
    const [form] = Form.useForm();
    const navigate = useNavigate();

    async function loadMovies() {
        setLoading(true);
        try {
            const data = await apiFetch('/api/movie', {}, 'main');
            setMovies(data);
        } catch (e) {
            message.error(e.message || 'Failed to load movies');
        }
        setLoading(false);
    }

    async function loadDirectors() {
        try {
            const d = await apiFetch('/api/director', {}, 'main');
            setDirectors(d || []);
        } catch (e) {
            message.error('Failed to load directors: ' + e.message);
        }
    }

    useEffect(() => {
        loadMovies();
    }, []);

    async function openCreate() {
        await loadDirectors();
        form.resetFields();
        setShowCreate(true);
    }

    async function onCreate(values) {
        try {
            const payload = {
                title: values.title,
                year: values.year,
                description: values.description,
                duration: values.duration,
                genre: values.genre,
                director: values.director,
                rating: values.rating
            };
            await apiFetch('/api/movie', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload),
            }, 'main');
            message.success('Movie created');
            setShowCreate(false);
            loadMovies();
        } catch (e) {
            message.error(e.message || 'Create failed');
        }
    }

    const columns = [
        { title: 'Title', dataIndex: 'title', key: 'title', sorter: (a,b)=> (a.title||'').localeCompare(b.title||'') },
        { title: 'Year', dataIndex: 'year', key: 'year', width: 100 },
        { title: 'Duration', dataIndex: 'duration', key: 'duration', sorter: (a,b)=> (a.title||'').localeCompare(b.title||'') },
        { title: 'Genre', dataIndex: 'genre', key: 'genre', width: 100 },
        { title: 'Director', dataIndex: 'director', key: 'director', render: (v, row) => v || row.director || '-' },
        { title: 'Rating', dataIndex: 'rating', key: 'rating', width: 100 },
        {
            title: 'Actions',
            key: 'actions',
            width: 220,
            render: (_, row) => (
                <div style={{ display: 'flex', gap: 8 }}>
                    <Button type="default" onClick={() => navigate(`/movie/${row.id}/upload`)}>Upload movie</Button>
                    <Button type="primary" onClick={() => navigate(`/movie/${row.id}/watch`)}>Watch movie</Button>
                </div>
            ),
        },
    ];

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                <h2>Movies</h2>
                <Button type="primary" onClick={openCreate}>Add Movie</Button>
            </div>

            <Table
                dataSource={movies}
                columns={columns}
                loading={loading}
                rowKey="id"
                pagination={{ pageSize: 10 }}
            />

            <Modal title="Create movie" open={showCreate} onCancel={() => setShowCreate(false)} footer={null}>
                <Form form={form} layout="vertical" onFinish={onCreate}>
                    <Form.Item name="title" label="Title" rules={[{ required: true }]}><Input /></Form.Item>
                    <Form.Item name="year" label="Year"><Input /></Form.Item>
                    <Form.Item name="genre" label="Genre"><Input /></Form.Item>
                    <Form.Item name="director" label="Director" rules={[{ required: true, message: 'Choose a director' }]}>
                        <Select
                            placeholder="Select director"
                            options={directors.map(d => ({ label: d.fio, value: d.fio }))}
                            showSearch
                            filterOption={(input, option) => (option?.label || '').toLowerCase().includes(input.toLowerCase())}
                        />
                    </Form.Item>
                    <Form.Item name="description" label="Description"><Input.TextArea rows={4} /></Form.Item>
                    <Form.Item>
                        <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                            <Button onClick={() => setShowCreate(false)}>Cancel</Button>
                            <Button htmlType="submit" type="primary">Create</Button>
                        </div>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
}