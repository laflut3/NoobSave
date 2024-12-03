import {Link} from "react-router-dom";
import {useState} from "react";

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
                {/* Mobile Menu Button */}
                <button
                    className="md:hidden focus:outline-none"
                    onClick={() => setMobileMenuOpen(!isMobileMenuOpen)}
                >
                    <div className="flex flex-col space-y-1">
                        <span
                            className={`block h-1 w-6 bg-white transition transform ${
                                isMobileMenuOpen ? "rotate-45 translate-y-2" : ""
                            }`}
                        />
                        <span
                            className={`block h-1 w-6 bg-white transition ${
                                isMobileMenuOpen ? "opacity-0" : ""
                            }`}
                        />
                        <span
                            className={`block h-1 w-6 bg-white transition transform ${
                                isMobileMenuOpen ? "-rotate-45 -translate-y-2" : ""
                            }`}
                        />
                    </div>
                </button>
            </nav>

            {/* Mobile Menu */}
            <div
                className={`md:hidden transition-all duration-300 overflow-hidden ${
                    isMobileMenuOpen ? "max-h-96" : "max-h-0"
                }`}
            >
                <ul className="bg-gradient-to-r from-blue-600 via-indigo-500 to-purple-600 text-white text-center space-y-4 py-4">
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
            </div>
        </header>
    );
}

export default Header;
