export default function GuideUtilisation() {
    return (
        <section className="min-h-screen bg-gray-100 py-10 px-6">
            {/* Titre Principal */}
            <h1 className="text-5xl font-extrabold text-center text-blue-600 mb-10">
                Guide d'Utilisation de <span className="text-purple-500">NoobSave</span>
            </h1>

            {/* Contenu */}
            <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-lg p-6 space-y-8">
                {/* Section 1 */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">1. Accéder à vos fichiers</h2>
                    <p className="text-gray-600">
                        Cliquez sur l'onglet <span className="font-semibold">"Mes documents"</span> dans le menu pour
                        afficher la liste de vos fichiers. Vous pourrez voir le nom, le type et la dernière date de
                        modification de chaque fichier.
                    </p>
                </div>

                {/* Section 2 */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">2. Télécharger un fichier</h2>
                    <p className="text-gray-600">
                        Sur la page des documents, cliquez sur le bouton <span className="font-semibold">"Télécharger"</span> à côté du fichier souhaité. Le fichier sera téléchargé directement sur votre appareil.
                    </p>
                </div>

                {/* Section 3 */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">3. Ajouter des fichiers</h2>
                    <p className="text-gray-600">
                        Pour ajouter de nouveaux fichiers, placez-les dans le dossier <code className="bg-gray-200 px-2 py-1 rounded">archive</code>. Ils seront synchronisés automatiquement toutes les minutes.
                    </p>
                </div>

                {/* Section 4 */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">4. Supprimer un fichier</h2>
                    <p className="text-gray-600">
                        La suppression des fichiers se fait via la base de données. Les fichiers supprimés ne seront
                        plus affichés après la prochaine synchronisation.
                    </p>
                </div>

                {/* Section 5 */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">5. Utiliser l'interface</h2>
                    <p className="text-gray-600">
                        Naviguez facilement grâce au menu en haut de la page. Chaque section de l'application est
                        accessible via un simple clic.
                    </p>
                </div>
            </div>
        </section>
    );
}
