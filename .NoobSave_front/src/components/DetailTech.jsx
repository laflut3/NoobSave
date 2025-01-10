import React from "react";
import {Link} from "react-router-dom";

function DetailTech() {
    return (
        <section className="bg-gray-100 min-h-screen flex flex-col justify-center p-32">
            <div className="container mx-auto px-6 lg:px-16">
                {/* Titre Principal */}
                <h1 className="text-4xl font-extrabold text-gray-800 mb-10 text-center">
                    Détails Techniques
                </h1>

                {/* Section 1 : Technologies Utilisées */}
                <div className="bg-white shadow-md rounded-lg p-8 mb-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">
                        Technologies Utilisées
                    </h2>
                    <ul className="list-disc list-inside text-lg text-gray-600 leading-relaxed">
                        <li>
                            <strong>Frontend :</strong> React.js avec TailwindCSS pour un design
                            moderne, responsive et rapide à développer.
                        </li>
                        <li>
                            <strong>Backend :</strong> Spring Boot, un framework Java robuste pour
                            gérer les API RESTful et l'accès à la base de données.
                        </li>
                        <li>
                            <strong>Base de données :</strong> MySQL pour stocker les métadonnées et
                            les fichiers de manière structurée.
                        </li>
                        <li>
                            <strong>Gestion des fichiers :</strong> Stockage local sur le serveur
                            avec des fonctionnalités pour synchroniser les fichiers du disque avec
                            la base de données.
                        </li>
                        <li>
                            <strong>Bibliothèques supplémentaires :</strong> Axios pour les appels
                            API dans le frontend, React Icons pour les icônes et FontAwesome pour les
                            visuels.
                        </li>
                        <li>
                            <strong>Planification des tâches :</strong> Utilisation de
                            `@Scheduled` dans Spring Boot pour exécuter des tâches de synchronisation
                            automatiquement toutes les minutes.
                        </li>
                    </ul>
                </div>

                {/* Section 2 : Fonctionnement de l'application */}
                <div className="bg-white shadow-md rounded-lg p-8 mb-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">
                        Fonctionnement de l'application
                    </h2>
                    <ol className="list-decimal list-inside text-lg text-gray-600 leading-relaxed">
                        <li>
                            <strong>Upload des fichiers :</strong> Les fichiers sont déposés dans un
                            répertoire local sur le serveur (appelé <em>archive</em>), où ils sont
                            détectés et synchronisés avec la base de données via une tâche planifiée
                            exécutée automatiquement toutes les minutes.
                        </li>
                        <li>
                            <strong>Sauvegarde des métadonnées :</strong> Pour chaque fichier, ses
                            métadonnées (nom, type MIME, date d'ajout et contenu binaire) sont
                            sauvegardées dans MySQL via une entité JPA (Java Persistence API).
                        </li>
                        <li>
                            <strong>Affichage :</strong> Les fichiers sont affichés dans le frontend
                            en temps réel via des appels API. Les utilisateurs peuvent télécharger ou
                            supprimer des fichiers directement depuis l'interface.
                        </li>
                        <li>
                            <strong>Suppression des fichiers :</strong> Une fois qu'un fichier est
                            supprimé, il est retiré à la fois du répertoire local et de la base de
                            données pour une gestion cohérente.
                        </li>
                    </ol>
                </div>

                {/* Section 3 : Avantages Techniques */}
                <div className="bg-white shadow-md rounded-lg p-8 mb-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">
                        Avantages Techniques
                    </h2>
                    <ul className="list-disc list-inside text-lg text-gray-600 leading-relaxed">
                        <li>
                            <strong>Modularité :</strong> L'architecture découplée permet de
                            maintenir et d'étendre facilement les fonctionnalités.
                        </li>
                        <li>
                            <strong>Performance :</strong> Spring Boot et React.js offrent une
                            combinaison puissante pour des temps de réponse rapides et une UX
                            fluide.
                        </li>
                        <li>
                            <strong>Sécurité :</strong> Les fichiers sont traités en toute sécurité,
                            avec une gestion stricte des types MIME pour éviter les fichiers
                            malveillants.
                        </li>
                        <li>
                            <strong>Scalabilité :</strong> Grâce à Spring Boot, l'application peut
                            facilement évoluer pour supporter des volumes élevés de fichiers ou être
                            migrée vers des solutions de stockage cloud.
                        </li>
                    </ul>
                </div>

                {/* Section 4 : Évolutions Futures */}
                <div className="bg-white shadow-md rounded-lg p-8">
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">
                        Évolutions Futures
                    </h2>
                    <ul className="list-disc list-inside text-lg text-gray-600 leading-relaxed">
                        <li>
                            Intégration d'une solution de stockage cloud (comme AWS S3 ou Google
                            Cloud Storage) pour une gestion des fichiers plus évolutive.
                        </li>
                        <li>
                            Ajout d'une authentification utilisateur pour sécuriser l'accès aux
                            fichiers.
                        </li>
                        <li>
                            Fonctionnalité de prévisualisation des fichiers directement depuis le
                            frontend (par exemple, PDF, images).
                        </li>
                        <li>
                            Intégration avec des outils tiers (par exemple, envoi des fichiers par
                            email ou API d'analyse de contenu).
                        </li>
                    </ul>
                </div>
            </div>
            <div className="flex w-full justify-center p-8">
                <Link
                    to="http://localhost:8080/swagger-ui/index.html#/"
                    className="inline-flex items-center px-6 py-3 bg-blue-600 hover:bg-blue-700 text-white text-xl font-semibold rounded-lg shadow-md transition-colors duration-300"
                >
                    <svg
                        className="w-6 h-6 mr-2"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        viewBox="0 0 24 24"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    >
                        <path
                            d="M13 16h-1v-4h-1m2-4h.01M12 7c1.657 0 3 .895 3 2v6c0 1.105-1.343 2-3 2s-3-.895-3-2V9c0-1.105 1.343-2 3-2z"/>
                    </svg>
                    Wiki Doc (endpoints & détails)
                </Link>
            </div>

        </section>
    );
}

export default DetailTech;
