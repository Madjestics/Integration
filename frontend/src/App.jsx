import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import AppLayout from './components/AppLayout';

import Home from './pages/Home';
import MovieDetails from './pages/MovieDetails';
import WatchMovie from './pages/WatchMovie';
import UploadMovie from './pages/UploadMovie';
import Directors from './pages/Directors';
import Recommendations from './pages/Recommendations';
import Preferences from './pages/Preferences';
import Login from './pages/Login';
import Register from './pages/Register';

export default function App() {
    return (
        <BrowserRouter>
            <AppLayout>
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/movie/:id" element={<MovieDetails />} />
                    <Route path="/movie/:id/watch" element={<WatchMovie />} />
                    <Route path="/movie/:id/upload" element={<UploadMovie />} />
                    <Route path="/directors" element={<Directors />} />
                    <Route path="/recommendations" element={<Recommendations />} />
                    <Route path="/prefs" element={<Preferences />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                </Routes>
            </AppLayout>
        </BrowserRouter>
    );
}