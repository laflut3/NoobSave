import React, { useState, useEffect } from "react";
import axios from "axios";

const Documents = () => {
    const [fichiers, setFichiers] = useState([]);

    // Fonction pour charger les fichiers
    const chargerFichiers = () => {
        axios
            .get("http://localhost:8080/api/fichiers")
            .then((response) => setFichiers(response.data))
            .catch((error) => console.error(error));
    };

    // Chargement initial des fichiers
    useEffect(() => {
        chargerFichiers();
    }, []);

    const telechargerFichier = (id, nom) => {
        axios
            .get(`http://localhost:8080/api/fichiers/${id}`, { responseType: "blob" })
            .then((response) => {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement("a");
                link.href = url;
                link.setAttribute("download", nom);
                document.body.appendChild(link);
                link.click();
            })
            .catch((error) => console.error(error));
    };

    const save = () => {
        axios
            .get("http://localhost:8080/api/fichiers/save")
            .then((response) => console.log(response))
            .catch((error) => console.error(error));
    };

    const supprimerFichier = (id) => {
        if (window.confirm("Êtes-vous sûr de vouloir supprimer ce fichier ?")) {
            axios
                .delete(`http://localhost:8080/api/fichiers/${id}`, { withCredentials: true })
                .then(() => {
                    console.log("Fichier supprimé !");
                    // Met à jour l'état en supprimant le fichier supprimé
                    setFichiers((prevFichiers) =>
                        prevFichiers.filter((fichier) => fichier.id !== id)
                    );
                })
                .catch((error) => {
                    console.error("Erreur lors de la suppression du fichier :", error);
                });
        }
    };

    return (
        <section className="container min-h-screen mx-auto py-10 px-4">
            {/* Titre Principal */}
            <h1 className="text-5xl font-extrabold mb-10 text-center text-gradient bg-gradient-to-r from-blue-500 via-purple-500 to-pink-500 text-transparent bg-clip-text">
                Mes Fichiers
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {fichiers.length > 0 ? (
                    fichiers.map((fichier) => (
                        <div
                            key={fichier.id}
                            className="bg-white rounded-lg shadow-lg p-6 hover:shadow-xl transition-shadow duration-300"
                        >
                            {/* Nom du Fichier */}
                            <h2 className="text-2xl font-semibold text-gray-800 mb-2">
                                {fichier.nom}
                            </h2>

                            {/* Date de Dernière Modification */}
                            <p className="text-gray-500 text-sm mb-4">
                                Dernière modification :{" "}
                                {new Date(fichier.dateModification).toLocaleString()}
                            </p>

                            {/* Bouton Télécharger */}
                            <button
                                className="px-5 py-2 bg-blue-500 text-white rounded-lg shadow-md hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-300 transition duration-200"
                                onClick={() => telechargerFichier(fichier.id, fichier.nom)}
                            >
                                Télécharger
                            </button>

                            {/* Bouton Supprimer */}
                            <button
                                className="px-5 py-2 bg-red-500 text-white rounded-lg shadow-md hover:bg-red-600 focus:outline-none focus:ring-2 focus:ring-red-300 transition duration-200 ml-2"
                                onClick={() => supprimerFichier(fichier.id)}
                            >
                                Supprimer
                            </button>
                        </div>
                    ))
                ) : (
                    <div className="col-span-full text-center">
                        <p className="text-gray-500 text-lg font-medium">
                            Aucun fichier disponible
                        </p>
                        <p className="text-sm text-gray-400">
                            Ajoutez des fichiers pour les voir ici.
                        </p>
                    </div>
                )}
            </div>
            <div className={"w-full flex justify-center p-10 text-center items-center gap-6"}>
                <button onClick={save} className={"p-4 text-white font-bold flex text-center justify-center shadow-lg hover:shadow-xl bg-blue-500 hover:bg-blue-600 rounded-full"}>Déclencher la sauvegarde</button>
            </div>
        </section>
    );
};

export default Documents;
