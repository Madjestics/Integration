import React, { useRef } from 'react';
import { useParams } from 'react-router-dom';
import {API_BASE_MAIN} from "../services/api.js";

export default function WatchMovie() {
    const { id } = useParams();
    const videoRef = useRef();

    return (
        <div style={{ width: '100%', maxWidth: 800, margin: '0 auto' }}>
            <h2>Watch Movie</h2>
            <video
                ref={videoRef}
                controls
                style={{ width: '100%', maxHeight: 640 }}
            >
                <source
                    src={`${API_BASE_MAIN}/api/movie/watch/${id}`}
                    type="video/mp4"
                />
                Your browser does not support the video tag.
            </video>
        </div>
    );
}