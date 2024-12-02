import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Documents from "./components/Documents";
import Header from "./components/Header";
import Home from "./components/Home";
import Guide from "./components/Guide";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
      <Router>
          <Header/>
          <Routes>
              <Route path="/" element={<Home/>} />
              <Route path="/documents" element={<Documents/>} />
              <Route path="/guide" element={<Guide/>} />
          </Routes>
      </Router>
  </React.StrictMode>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
