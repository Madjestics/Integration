import React, { useEffect, useState } from 'react';
import { List, Card, message } from 'antd';
import { apiFetch } from '../services/api';


export default function Recommendations(){
    const [recs, setRecs] = useState([]);
    const [loading, setLoading] = useState(false);


    async function load(){
        const userId = localStorage.getItem('userId');
        if(!userId) return message.warn('Please login to see recommendations');
        setLoading(true);
        try{
            const data = await apiFetch(`/api/preferences/recommendations?userId=${userId}`, {}, 'prefs');
            setRecs(data);
        }catch(e){ message.error(e.message); }
        setLoading(false);
    }


    useEffect(()=>{ load(); },[]);


    return (
        <List grid={{ gutter: 16, column: 3 }} dataSource={recs} loading={loading} renderItem={item => (
            <List.Item>
                <Card title={item.title}>{item.description}</Card>
            </List.Item>
        )} />
    );
}