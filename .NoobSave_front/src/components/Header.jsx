import { Link } from "react-router-dom";
import { useState } from "react";

function Header() {
    const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);

    return (
        <header className="bg-gradient-to-r from-blue-600 via-indigo-500 to-purple-600 text-white shadow-md">
            <nav className="container mx-auto flex justify-between items-center p-4">
                {/* Logo */}
                <div className="text-3xl font-extrabold tracking-tight">
                    <Link
                        to="/"
                        className="hover:text-yellow-300 transition duration-300"
                    >
                        NoobSave
                    </Link>
                </div>

                {/* Desktop Menu */}
                <ul className="hidden md:flex space-x-8 text-lg font-medium">
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

                {/* Search Bar */}
                <div className="hidden md:block">
                    <input
                        type="text"
                        placeholder="Rechercher..."
                        className="py-2 px-4 rounded-full border border-transparent focus:border-yellow-300 focus:outline-none transition duration-300"
                    />
                </div>

                {/* Mobile Menu Button */}
                <button
                    className="md:hidden focus:outline-none"
                    onClick={() => setMobileMenuOpen(!isMobileMenuOpen)}
                >
                    <span className="material-icons text-3xl">
                        {isMobileMenuOpen ? "close" : "menu"}
                    </span>
                </button>
            </nav>

            {/* Mobile Menu */}
            {isMobileMenuOpen && (
                <ul className="md:hidden bg-purple-700 text-center space-y-4 py-4">
                    <li>
                        <Link
                            to="/"
                            className="hover:text-yellow-300 text-lg transition duration-300"
                        >
                            Accueil
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="/documents"
                            className="hover:text-yellow-300 text-lg transition duration-300"
                        >
                            Mes Documents
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="/guide"
                            className="hover:text-yellow-300 text-lg transition duration-300"
                        >
                            Guide d'Utilisation
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="/detail"
                            className="hover:text-yellow-300 text-lg transition duration-300"
                        >
                            Détails Techniques
                        </Link>
                    </li>
                </ul>
            )}
        </header>
    );
}

export default Header;
