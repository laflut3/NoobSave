import { Link } from "react-router-dom";
import { FaFacebook, FaTwitter, FaInstagram } from "react-icons/fa";

function Footer() {
    return (
        <footer className="bg-gray-800 text-white py-12">
            <div className="container mx-auto px-4 text-center md:text-left">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {/* Section À Propos */}
                    <div>
                        <h3 className="text-xl font-bold mb-4">À propos de NoobSave</h3>
                        <p className="text-sm">
                            NoobSave est une plateforme moderne pour stocker et gérer vos documents
                            de manière efficace et sécurisée. Développée avec passion pour simplifier
                            votre gestion de fichiers.
                        </p>
                    </div>

                    {/* Liens Rapides */}
                    <div className="flex flex-col items-center">
                        <h3 className="text-xl font-bold mb-4">Liens rapides</h3>
                        <ul className="space-y-2 text-sm">
                            <li>
                                <Link
                                    to="/"
                                    className="hover:text-yellow-300 transition duration-300"
                                >
                                    Accueil
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/documents"
                                    className="hover:text-yellow-300 transition duration-300"
                                >
                                    Mes Documents
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/guide"
                                    className="hover:text-yellow-300 transition duration-300"
                                >
                                    Guide d'Utilisation
                                </Link>
                            </li>
                            <li>
                                <Link
                                    to="/detail"
                                    className="hover:text-yellow-300 transition duration-300"
                                >
                                    Détails Techniques
                                </Link>
                            </li>
                        </ul>
                    </div>

                    {/* Contact */}
                    <div className="flex flex-col items-center">
                        <h3 className="text-xl font-bold mb-4">Contact</h3>
                        <p className="text-sm">
                            Email :{" "}
                            <a
                                href="mailto:support@noobsave.com"
                                className="hover:text-yellow-300 transition duration-300"
                            >
                                support@noobsave.com
                            </a>
                        </p>
                        <p className="text-sm mb-4">Téléphone : +33 6 12 34 56 78</p>
                        <div className="flex space-x-4">
                            <a
                                href="https://facebook.com"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="hover:text-yellow-300 transition duration-300"
                            >
                                <FaFacebook size={24} />
                            </a>
                            <a
                                href="https://twitter.com"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="hover:text-yellow-300 transition duration-300"
                            >
                                <FaTwitter size={24} />
                            </a>
                            <a
                                href="https://instagram.com"
                                target="_blank"
                                rel="noopener noreferrer"
                                className="hover:text-yellow-300 transition duration-300"
                            >
                                <FaInstagram size={24} />
                            </a>
                        </div>
                    </div>
                </div>

                {/* Copyright */}
                <div className="mt-12 text-center text-sm text-gray-300">
                    © {new Date().getFullYear()} NoobSave. Tous droits réservés.
                </div>
            </div>
        </footer>
    );
}

export default Footer;
