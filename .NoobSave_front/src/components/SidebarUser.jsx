import React, { useState } from "react";
import axios from "axios";

const Sidebar = ({ isOpen, toggleSidebar }) => {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [userInfo, setUserInfo] = useState(null);

    // États pour les formulaires
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoginForm, setIsLoginForm] = useState(true); // Pour basculer entre login et signup

    // Gestion de la connexion
    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post(
                "http://localhost:8080/api/users/login",
                { username, password },
                { withCredentials: true } // Inclure les cookies
            );
            setUserInfo(response.data); // Enregistrer les informations utilisateur
            setIsLoggedIn(true); // Marquer comme connecté
            toggleSidebar(); // Fermer la barre latérale
        } catch (error) {
            if (error.response && error.response.status === 401) {
                alert("Nom d'utilisateur ou mot de passe incorrect.");
            } else {
                console.error("Erreur de connexion :", error);
                alert("Erreur de connexion.");
            }
        }
    };

    // Gestion de la création de compte
    const handleSignup = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post("http://localhost:8080/api/users", {
                username,
                password,
            });
            alert("Compte créé avec succès. Connectez-vous !");
            setIsLoginForm(true); // Revenir au formulaire de connexion
        } catch (error) {
            alert("Erreur lors de la création du compte.");
        }
    };

    // Gestion de la déconnexion
    const handleLogout = async () => {
        try {
            await axios.post("http://localhost:8080/api/users/logout", {}, { withCredentials: true });
            setIsLoggedIn(false);
            setUserInfo(null);
            alert("Déconnecté avec succès.");
        } catch (error) {
            alert("Erreur lors de la déconnexion.");
        }
    };

    return (
        <div
            className={`fixed top-0 left-0 h-screen w-80 bg-gray-100 shadow-lg transition-transform duration-300 ${
                isOpen ? "translate-x-0" : "-translate-x-full"
            }`}
        >
            <div className="p-6">
                {isLoggedIn ? (
                    // Si connecté
                    <div>
                        <h2 className="text-xl font-bold text-gray-700 mb-4">
                            Bienvenue, {userInfo.username}
                        </h2>
                        <p className="text-sm text-gray-600">Email : {userInfo.email}</p>
                        <button
                            className="mt-4 w-full bg-red-500 text-white font-semibold py-2 rounded-md hover:bg-red-600 transition"
                            onClick={handleLogout}
                        >
                            Déconnexion
                        </button>
                    </div>
                ) : (
                    // Formulaires de connexion et d'inscription
                    <div>
                        <h2 className="text-xl font-bold text-gray-700 mb-4">
                            {isLoginForm ? "Connexion" : "Créer un compte"}
                        </h2>
                        <form onSubmit={isLoginForm ? handleLogin : handleSignup} className="space-y-4">
                            <div className="flex flex-col">
                                <label
                                    htmlFor="username"
                                    className="text-sm text-gray-600 mb-1"
                                >
                                    Nom d'utilisateur
                                </label>
                                <input
                                    type="text"
                                    id="username"
                                    placeholder="Entrez votre nom d'utilisateur"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-300"
                                />
                            </div>
                            {!isLoginForm && (
                                <div className="flex flex-col">
                                    <label
                                        htmlFor="email"
                                        className="text-sm text-gray-600 mb-1"
                                    >
                                        Email
                                    </label>
                                    <input
                                        type="email"
                                        id="email"
                                        placeholder="Entrez votre e-mail"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                        className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-300"
                                    />
                                </div>
                            )}
                            <div className="flex flex-col">
                                <label
                                    htmlFor="password"
                                    className="text-sm text-gray-600 mb-1"
                                >
                                    Mot de passe
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    placeholder="Entrez votre mot de passe"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    className="p-2 border border-gray-300 rounded-md focus:outline-none focus:ring focus:ring-blue-300"
                                />
                            </div>
                            <button
                                type="submit"
                                className="w-full bg-blue-500 text-white font-semibold py-2 rounded-md hover:bg-blue-600 transition"
                            >
                                {isLoginForm ? "Se connecter" : "Créer un compte"}
                            </button>
                        </form>
                        <button
                            className="mt-4 w-full bg-gray-300 text-gray-700 font-semibold py-2 rounded-md hover:bg-gray-400 transition"
                            onClick={() => setIsLoginForm(!isLoginForm)}
                        >
                            {isLoginForm ? "Créer un compte" : "Retour à la connexion"}
                        </button>
                    </div>
                )}
                <button
                    className="mt-4 w-full bg-gray-500 text-white font-semibold py-2 rounded-md hover:bg-gray-600 transition"
                    onClick={toggleSidebar}
                >
                    Fermer
                </button>
            </div>
        </div>
    );
};

export default Sidebar;
