import React, { useState } from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Documents from "./components/Documents";
import Header from "./components/AllPage/Header";
import Home from "./components/Home";
import Guide from "./components/Guide";
import Footer from "./components/AllPage/Footer";
import DetailTech from "./components/DetailTech";
import SidebarUser from "./components/AllPage/SidebarUser";
import Parametre from "./components/Parametre";

const App = () => {
    const [isSidebarOpen, setSidebarOpen] = useState(false);

    const toggleSidebar = () => {
        setSidebarOpen(!isSidebarOpen);
    };

    return (
        <Router>
            {/* Pass the toggleSidebar function and isSidebarOpen state to components */}
            <Header toggleSidebar={toggleSidebar} />
            <SidebarUser isOpen={isSidebarOpen} toggleSidebar={toggleSidebar} />
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/documents" element={<Documents />} />
                <Route path="/guide" element={<Guide />} />
                <Route path="/detail" element={<DetailTech />} />
                <Route path="/parametre" element={<Parametre/>}/>
            </Routes>
            <Footer />
        </Router>
    );
};

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <App />
    </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
