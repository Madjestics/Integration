import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import { Upload, Button, message } from 'antd';
import { UploadOutlined } from '@ant-design/icons';
import {API_BASE_MAIN, apiFetch} from '../services/api';

export default function UploadMovie(){
    const { id } = useParams();
    const [fileList, setFileList] = useState([]);

    const props = {
        beforeUpload: (file) => { setFileList([file]); return false; },
        fileList,
        onRemove: () => setFileList([])
    };

    async function submit() {
        if (!fileList[0]) return message.warn('Choose a file');
        const form = new FormData();
        form.append('movie', fileList[0]);

        try {
            const token = localStorage.getItem('token');
            const res = await fetch(API_BASE_MAIN + `/api/movie/upload/${id}`, {
                method: 'POST',
                headers: {
                    ...(token ? { Authorization: `Bearer ${token}` } : {})
                },
                body: form
            });

            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || 'Upload failed');
            }

            message.success('Uploaded');
            setFileList([]);
        } catch (e) {
            message.error(e.message);
        }
    }

    return (
        <div>
            <Upload {...props} accept="video/*">
                <Button icon={<UploadOutlined/>}>Select File</Button>
            </Upload>
            <div style={{ marginTop: 12 }}>
                <Button type="primary" onClick={submit}>Upload</Button>
            </div>
        </div>
    );
}