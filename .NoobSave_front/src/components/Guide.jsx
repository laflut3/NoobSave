export default function GuideUtilisation() {
    return (
        <section className="min-h-screen pt-32 bg-gray-50 py-10 px-6">
            {/* Titre Principal */}
            <h1 className="text-5xl font-extrabold text-center text-blue-600 mb-10">
                Guide d'Utilisation de <span className="text-purple-500">NoobSave</span>
            </h1>

            {/* Contenu */}
            <div className="max-w-4xl mx-auto bg-white shadow-lg rounded-lg p-6 space-y-10">
                {/* Introduction */}
                <div>
                    <h2 className="text-3xl font-bold text-gray-800 mb-4">Introduction</h2>
                    <p className="text-gray-600 leading-relaxed">
                        Bienvenue sur <span className="font-semibold">NoobSave</span>, une application
                        conçue pour faciliter la gestion, la sauvegarde et le partage de vos fichiers.
                        Ce guide d’utilisation vous accompagnera pour découvrir les principales fonctions
                        de NoobSave et pour tirer le meilleur parti de notre interface.
                    </p>
                </div>

                {/* Section 1 : Navigation */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">1. Navigation générale</h2>
                    <p className="text-gray-600 leading-relaxed">
                        L’interface de <span className="font-semibold">NoobSave</span> est divisée en
                        différentes sections accessibles via la barre de navigation en haut de l’écran :
                    </p>
                    <ul className="list-disc list-inside mt-2 text-gray-600">
                        <li><strong>Accueil</strong> : présentation générale de l’application.</li>
                        <li><strong>Mes Documents</strong> : affiche la liste de vos fichiers enregistrés.</li>
                        <li><strong>Guide d’Utilisation</strong> : vous y êtes actuellement !</li>
                        <li><strong>Détails Techniques</strong> : informations plus poussées sur le fonctionnement interne.</li>
                    </ul>
                    <p className="text-gray-600 mt-2">
                        Sur mobile, le menu peut se présenter sous forme d’un <em>burger</em> à ouvrir/fermer
                        d’un simple clic, tandis qu’en mode desktop, vous verrez les onglets alignés horizontalement.
                    </p>
                </div>

                {/* Section 2 : Afficher et Télécharger */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">2. Afficher et télécharger vos fichiers</h2>
                    <p className="text-gray-600 leading-relaxed">
                        Dans la section <span className="font-semibold">"Mes Documents"</span>, vous trouvez une
                        liste de toutes vos ressources enregistrées dans <span className="font-semibold">NoobSave</span>.
                        Chaque carte de fichier contient :
                    </p>
                    <ul className="list-disc list-inside mt-2 text-gray-600">
                        <li><strong>Nom du Fichier</strong> : le nom actuel, avec extension.</li>
                        <li><strong>Date de dernière modification</strong> : pour suivre les mises à jour.</li>
                        <li><strong>Bouton “Télécharger”</strong> : pour obtenir instantanément le fichier.</li>
                        <li><strong>Bouton “Supprimer”</strong> : pour retirer le fichier de la base de données.</li>
                    </ul>
                    <p className="text-gray-600 mt-2">
                        Pour <em>télécharger</em> un fichier, cliquez simplement sur
                        le bouton <span className="font-semibold">"Télécharger"</span> et le fichier se
                        téléchargera sur votre appareil.
                    </p>
                </div>

                {/* Section 3 : Ajouter des fichiers */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">3. Ajouter de nouveaux fichiers</h2>
                    <p className="text-gray-600 leading-relaxed">
                        L’ajout de fichiers se fait en plaçant les documents directement
                        dans le répertoire <code className="bg-gray-200 px-2 py-1 rounded">
                        archive
                    </code> (ou le dossier désigné dans votre configuration). Par défaut,
                        <span className="font-semibold">NoobSave</span> est configuré pour synchroniser
                        automatiquement ces fichiers selon un intervalle donné (toutes les X minutes, par exemple).
                    </p>
                    <p className="text-gray-600 mt-2">
                        Vous pouvez aussi déclencher manuellement la synchronisation depuis
                        la page <span className="font-semibold">Mes Documents</span>, en cliquant sur
                        <span className="font-semibold">“Déclencher la sauvegarde”</span>.
                    </p>
                </div>

                {/* Section 4 : Sauvegarde automatique et Intervalle */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">4. Sauvegarde automatique</h2>
                    <p className="text-gray-600 leading-relaxed">
                        <span className="font-semibold">NoobSave</span> propose une
                        sauvegarde automatique, activée par défaut. Selon la configuration,
                        elle se déclenche régulièrement pour vérifier les changements
                        (ajout/modification). Vous pouvez :
                    </p>
                    <ul className="list-disc list-inside mt-2 text-gray-600">
                        <li>
                            <strong>Activer / désactiver</strong> la sauvegarde automatique via
                            la page <code className="bg-gray-200 px-2 py-1 rounded">Paramètres</code>.
                        </li>
                        <li>
                            <strong>Modifier la fréquence</strong> (ex. toutes les 5 minutes, 10 minutes, etc.).
                        </li>
                        <li>
                            <strong>Choisir le type de fichier a sauvegarder</strong> (ex:.pdf , .txt , .svg ...)
                        </li>
                    </ul>
                </div>

                {/* Section 5 : Supprimer un fichier */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">5. Supprimer un fichier</h2>
                    <p className="text-gray-600 leading-relaxed">
                        Pour supprimer un fichier, rendez-vous sur <span className="font-semibold">"Mes Documents"</span>
                        et cliquez sur <span className="font-semibold">"Supprimer"</span>. La suppression se fait
                        à la fois en base de données et (si présent) sur votre répertoire local.
                    </p>
                    <p className="text-gray-600 mt-2">
                        Une fois la suppression confirmée, le fichier n’apparaîtra plus dans
                        votre liste. Si vous supprimez le fichier localement, vous pouvez
                        utiliser la fonction <span className="font-semibold">“Restaurer les fichiers manquants”</span>
                        si vous souhaitez le récupérer depuis la base.
                    </p>
                </div>

                {/* Section 6 : Conseils et Astuces */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">6. Conseils & Astuces</h2>
                    <ul className="list-disc list-inside mt-2 text-gray-600 space-y-2">
                        <li>
                            <strong>Extensions prises en charge :</strong> Vérifiez dans
                            la page <code className="bg-gray-200 px-2 py-1 rounded">Paramètres</code>
                            quelles extensions sont autorisées (ex. .pdf, .docx, .txt) et adaptez votre liste
                            si nécessaire.
                        </li>
                        <li>
                            <strong>Intervalle minimal de sauvegarde :</strong> Pour éviter
                            de surcharger le système, l’intervalle ne peut pas descendre
                            en dessous de 5000ms (5 s). Définissez une valeur raisonnable
                            (ex. 1 minute) pour un compromis entre réactivité et performances.
                        </li>
                        <li>
                            <strong>Organisation des fichiers :</strong> Vous pouvez créer
                            des sous-dossiers dans <code className="bg-gray-200 px-2 py-1 rounded">archive</code>.
                            <span className="font-semibold">NoobSave</span> scanne récursivement ces répertoires
                            lors de la sauvegarde automatique (selon votre configuration).
                        </li>
                    </ul>
                </div>

                {/* Conclusion */}
                <div>
                    <h2 className="text-2xl font-bold text-gray-800 mb-4">Conclusion</h2>
                    <p className="text-gray-600 leading-relaxed">
                        Nous espérons que ce guide vous aidera à exploiter pleinement
                        <span className="font-semibold"> NoobSave</span>. Pour toute question
                        ou problème, n’hésitez pas à consulter la section
                        <span className="font-semibold">"Détails Techniques"</span> ou à contacter
                        votre administrateur.
                    </p>
                </div>
            </div>
        </section>
    );
}
