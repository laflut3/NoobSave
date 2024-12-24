import React, { useState } from "react";
import axios from "axios";
import {
    FaUser,
    FaSignOutAlt,
    FaTimes,
    FaUserPlus,
    FaSignInAlt,
} from "react-icons/fa";

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
            await axios.post("http://localhost:8080/api/users", {
                username,
                email,
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
            className={`
        fixed top-0 left-0 h-screen w-80 
        transform transition-transform duration-300 z-50
        bg-white shadow-xl border-r border-gray-200
        flex flex-col
        ${isOpen ? "translate-x-0" : "-translate-x-full"}
      `}
        >
            {/* Header de la Sidebar */}
            <div className="flex items-center justify-between px-4 py-3 bg-gray-100 border-b border-gray-200">
                <div className="flex items-center space-x-2">
                    <FaUser className="text-blue-500" />
                    <h2 className="text-lg font-semibold text-gray-700">
                        {isLoggedIn
                            ? `Bonjour, ${userInfo?.username}`
                            : "Bienvenue sur NoobSave"}
                    </h2>
                </div>
                <button
                    className="text-gray-600 hover:text-gray-900 transition-colors"
                    onClick={toggleSidebar}
                    aria-label="Fermer la sidebar"
                >
                    <FaTimes size={20} />
                </button>
            </div>

            {/* Contenu principal */}
            <div className="flex-1 overflow-y-auto p-6">
                {isLoggedIn ? (
                    // Si connecté
                    <div className="space-y-4">
                        <div className="bg-blue-50 p-4 rounded-md border border-blue-100">
                            <p className="text-sm text-gray-700">
                                <strong>Nom d'utilisateur :</strong> {userInfo.username}
                            </p>
                            <p className="text-sm text-gray-700">
                                <strong>Email :</strong>{" "}
                                {userInfo.email ? userInfo.email : "non renseigné"}
                            </p>
                        </div>
                        <button
                            className="w-full flex items-center justify-center space-x-2 bg-red-500 text-white font-semibold py-2 rounded-md hover:bg-red-600 transition"
                            onClick={handleLogout}
                        >
                            <FaSignOutAlt />
                            <span>Déconnexion</span>
                        </button>
                    </div>
                ) : (
                    // Formulaires de connexion et d'inscription
                    <div>
                        <h2 className="flex items-center space-x-2 text-xl font-bold text-gray-700 mb-4">
                            {isLoginForm ? (
                                <>
                                    <FaSignInAlt />
                                    <span>Connexion</span>
                                </>
                            ) : (
                                <>
                                    <FaUserPlus />
                                    <span>Créer un compte</span>
                                </>
                            )}
                        </h2>

                        <form
                            onSubmit={isLoginForm ? handleLogin : handleSignup}
                            className="space-y-4"
                        >
                            <div className="flex flex-col">
                                <label htmlFor="username" className="text-sm text-gray-600 mb-1">
                                    Nom d'utilisateur
                                </label>
                                <input
                                    type="text"
                                    id="username"
                                    placeholder="Entrez votre nom d'utilisateur"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    required
                                    className="
                    p-2 border border-gray-300 rounded-md focus:outline-none
                    focus:ring focus:ring-blue-300 transition
                  "
                                />
                            </div>

                            {/* Champ Email uniquement si on est en mode Signup */}
                            {!isLoginForm && (
                                <div className="flex flex-col">
                                    <label htmlFor="email" className="text-sm text-gray-600 mb-1">
                                        Adresse e-mail
                                    </label>
                                    <input
                                        type="email"
                                        id="email"
                                        placeholder="Entrez votre e-mail"
                                        value={email}
                                        onChange={(e) => setEmail(e.target.value)}
                                        required
                                        className="
                      p-2 border border-gray-300 rounded-md focus:outline-none
                      focus:ring focus:ring-blue-300 transition
                    "
                                    />
                                </div>
                            )}

                            <div className="flex flex-col">
                                <label htmlFor="password" className="text-sm text-gray-600 mb-1">
                                    Mot de passe
                                </label>
                                <input
                                    type="password"
                                    id="password"
                                    placeholder="Entrez votre mot de passe"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    required
                                    className="
                    p-2 border border-gray-300 rounded-md focus:outline-none
                    focus:ring focus:ring-blue-300 transition
                  "
                                />
                            </div>

                            <button
                                type="submit"
                                className="
                  w-full flex items-center justify-center space-x-2
                  bg-blue-500 text-white font-semibold py-2 rounded-md
                  hover:bg-blue-600 transition
                "
                            >
                                {isLoginForm ? (
                                    <>
                                        <FaSignInAlt />
                                        <span>Se connecter</span>
                                    </>
                                ) : (
                                    <>
                                        <FaUserPlus />
                                        <span>Créer un compte</span>
                                    </>
                                )}
                            </button>
                        </form>

                        <button
                            className="
                mt-4 w-full bg-gray-200 text-gray-700 font-semibold
                py-2 rounded-md hover:bg-gray-300 transition
              "
                            onClick={() => setIsLoginForm(!isLoginForm)}
                        >
                            {isLoginForm ? "Créer un compte" : "Retour à la connexion"}
                        </button>
                    </div>
                )}
            </div>

            {/* Bouton Fermer (optionnel si on veut un second bouton en bas) */}
            {/*
      <div className="p-4 border-t border-gray-200">
        <button
          className="w-full flex items-center justify-center space-x-2 bg-gray-500 text-white font-semibold py-2 rounded-md hover:bg-gray-600 transition"
          onClick={toggleSidebar}
        >
          <FaTimes />
          <span>Fermer</span>
        </button>
      </div>
      */}
        </div>
    );
};

export default Sidebar;