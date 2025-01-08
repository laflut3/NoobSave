import React from "react";

export default function Home() {
    return (
        <section className="min-h-screen px-6 flex flex-col items-center justify-center bg-gradient-to-br from-indigo-600 via-purple-500 to-pink-500 text-white">
            {/* Conteneur global avec une animation d'apparition (fade in) */}
            <div className="max-w-4xl w-full flex flex-col items-center text-center space-y-8 animate-fadeIn">

                {/* Titre principal */}
                <h1 className="text-5xl md:text-7xl font-extrabold">
                    Bienvenue sur <span className="text-yellow-300">NoobSave</span>
                </h1>

                {/* Description */}
                <p className="text-lg md:text-xl max-w-3xl">
                    Gérez et téléchargez facilement vos fichiers avec notre application moderne et
                    intuitive. NoobSave offre une interface simple et efficace pour organiser vos
                    documents en toute sérénité.
                </p>

                {/* Section Fonctionnalités */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6 w-full">
                    <div className="bg-white text-gray-800 p-6 rounded-xl shadow-lg transform hover:scale-105 transition-transform duration-300">
                        <h2 className="text-xl md:text-2xl font-semibold mb-2">Gérez vos fichiers</h2>
                        <p className="text-sm md:text-base">
                            Consultez et organisez vos documents en toute simplicité
                            grâce à notre interface intuitive.
                        </p>
                    </div>
                    <div className="bg-white text-gray-800 p-6 rounded-xl shadow-lg transform hover:scale-105 transition-transform duration-300">
                        <h2 className="text-xl md:text-2xl font-semibold mb-2">Téléchargement rapide</h2>
                        <p className="text-sm md:text-base">
                            Téléchargez vos fichiers instantanément en un seul clic grâce
                            à notre système optimisé.
                        </p>
                    </div>
                    <div className="bg-white text-gray-800 p-6 rounded-xl shadow-lg transform hover:scale-105 transition-transform duration-300">
                        <h2 className="text-xl md:text-2xl font-semibold mb-2">Interface moderne</h2>
                        <p className="text-sm md:text-base">
                            Profitez d'un design épuré et responsive, conçu pour vous offrir
                            la meilleure expérience utilisateur.
                        </p>
                    </div>
                </div>

                {/* Bouton d'Action */}
                <a
                    href="/documents"
                    className="inline-block px-6 py-3 bg-yellow-300 text-gray-800 font-semibold rounded-lg shadow-md
                     hover:bg-yellow-400 transition duration-200"
                >
                    Accéder à mes fichiers
                </a>
            </div>
        </section>
    );
}
