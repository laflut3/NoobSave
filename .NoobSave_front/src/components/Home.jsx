export default function Home() {
    return (
        <div className="min-h-screen bg-gradient-to-br from-blue-500 via-purple-500 to-pink-500 text-white flex flex-col items-center justify-center px-6">
            {/* Titre Principal */}
            <h1 className="text-5xl md:text-7xl font-extrabold text-center mb-6">
                Bienvenue sur <span className="text-yellow-300">NoobSave</span>
            </h1>

            {/* Description */}
            <p className="text-lg md:text-xl text-center max-w-3xl mb-10">
                Gérez et téléchargez facilement vos fichiers avec notre application moderne et intuitive.
                NoobSave offre une interface simple et efficace pour organiser vos documents en toute sérénité.
            </p>

            {/* Section Fonctionnalités */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-5xl w-full">
                <div className="bg-white text-gray-800 p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300">
                    <h2 className="text-xl font-semibold mb-2">Gérez vos fichiers</h2>
                    <p className="text-sm">
                        Consultez et organisez vos documents en toute simplicité avec notre interface intuitive.
                    </p>
                </div>
                <div className="bg-white text-gray-800 p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300">
                    <h2 className="text-xl font-semibold mb-2">Téléchargement rapide</h2>
                    <p className="text-sm">
                        Téléchargez vos fichiers instantanément en un seul clic grâce à notre système optimisé.
                    </p>
                </div>
                <div className="bg-white text-gray-800 p-6 rounded-lg shadow-lg hover:shadow-xl transition-shadow duration-300">
                    <h2 className="text-xl font-semibold mb-2">Interface moderne</h2>
                    <p className="text-sm">
                        Profitez d'un design épuré et responsive, conçu pour vous offrir la meilleure expérience utilisateur.
                    </p>
                </div>
            </div>

            {/* Bouton d'Action */}
            <div className="mt-10">
                <a
                    href="/documents"
                    className="px-6 py-3 bg-yellow-300 text-gray-800 font-semibold rounded-lg shadow-md hover:bg-yellow-400 transition duration-200"
                >
                    Accéder à mes fichiers
                </a>
            </div>
        </div>
    );
}
