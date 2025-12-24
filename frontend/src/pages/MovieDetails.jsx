import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Card, message } from 'antd';
import { apiFetch } from '../services/api';


export default function MovieDetails(){
    const { id } = useParams();
    const [movie, setMovie] = useState(null);


    useEffect(()=>{
        apiFetch(`/api/movie/${id}`).then(setMovie).catch(e => message.error(e.message));
    },[id]);


    if(!movie) return <div>Loading...</div>;
    return (
        <Card title={movie.title}>
            <p><strong>Year:</strong> {movie.year}</p>
            <p><strong>Duration:</strong> {movie.duration}</p>
            <p><strong>Genre:</strong> {movie.genre}</p>
            <p><strong>Director:</strong> {movie.director}</p>
        </Card>
    );
}