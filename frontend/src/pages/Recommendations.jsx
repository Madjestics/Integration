import React, { useEffect, useState } from 'react';
import {message, Table, Tabs} from 'antd';
import { apiFetch } from '../services/api';


export default function Recommendations(){
    const [recs, setRecs] = useState([]);
    const [watched, setWatched] = useState([]);
    const [loading, setLoading] = useState(false);


    async function load(){
        const userId = localStorage.getItem('userId');
        if (!userId) return message.warn('Login to see recommendations');
        setLoading(true);
        try{
            const [recsData, watchedData] = await Promise.all([
                apiFetch(`/api/preferences/recommendations?userId=${userId}`, {}, 'prefs'),
                apiFetch(`/api/preferences/watched?userId=${userId}`, {}, 'prefs')
            ]);
            setRecs(recsData ?? []);
            setWatched(watchedData ?? []);
        } catch(e) {
            message.error(e.message);
        } finally {
            setLoading(false);
        }
    }


    useEffect(()=>{ load(); },[]);

    const columns = [
        { title: 'Id', dataIndex: 'id', key: 'id' },
        { title: 'Title', dataIndex: 'title', key: 'title' },
        { title: 'Year', dataIndex: 'year', key: 'year' },
        { title: 'Director', dataIndex: 'director', key: 'director'}
    ];

    return (
        <Tabs
            defaultActiveKey="1"
            items={[
                {
                    key: '1',
                    label: 'Recommendations',
                    children: (
                        <Table
                            rowKey="id"
                            dataSource={recs}
                            loading={loading}
                            columns={columns}
                            pagination={{ pageSize: 5 }}
                        />
                    )
                },
                {
                    key: '2',
                    label: 'Watched movies',
                    children: (
                        <Table
                            rowKey="id"
                            dataSource={watched}
                            loading={loading}
                            columns={columns}
                            pagination={{ pageSize: 5 }}
                        />
                    )
                }
            ]}
        />
    );
}