import {Link} from "react-router-dom";
import {useEffect, useState} from "react";
import {FaUser} from "react-icons/fa";
import {IoMdSettings} from "react-icons/io";

function Header({toggleSidebar}) {
    const token = localStorage.getItem("token");
    const [isAdmin, setIsAdmin] = useState(null);

    useEffect(() => {
        if (token) {
            fetch("http://localhost:8080/api/users/isAdmin", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
                .then((res) => res.json())
                .then((data) => {
                    console.log(data);
                    setIsAdmin(data.admin);
                })
        }
    })

    const [isMobileMenuOpen, setMobileMenuOpen] = useState(false);

    const handleBurgerClick = () => {
        setMobileMenuOpen(!isMobileMenuOpen);
    };

    return (
        // On enveloppe toute la NavBar dans un <header> avec un gradient plein écran
        <header
            className="w-full fixed top-0 left-0 z-10 bg-gradient-to-r from-purple-600 to-pink-500 text-white shadow-md">

            {/* Barre horizontale "contenant" : elle est centrée, mais le gradient s'étend en pleine largeur */}
            <nav className="max-w-7xl mx-auto p-4 flex justify-between items-center">
                {/* Logo */}
                <div className="text-3xl font-extrabold tracking-tight">
                    <Link to="/"
                          className="hover:text-yellow-300 transition-colors pr-8 duration-300 text-lg md:text-2xl">
                        NoobSave
                    </Link>
                </div>

                {/* Menu desktop */}
                <ul className="hidden md:flex space-x-8 text-lg font-medium justify-center text-center">
                    <li className={"flex justify-center text-center items-center"}>
                        <Link to="/" className="hover:text-yellow-300 transition-colors duration-300">
                            Accueil
                        </Link>
                    </li>
                    <li className={"flex justify-center text-center items-center"}>
                        <Link to="/documents" className="hover:text-yellow-300 transition-colors duration-300">
                            Mes Documents
                        </Link>
                    </li>
                    <li className={"flex justify-center text-center items-center"}>
                        <Link to="/guide" className="hover:text-yellow-300 transition-colors duration-300">
                            Guide d'Utilisation
                        </Link>
                    </li>
                    <li className={"flex justify-center text-center items-center"}>
                        <Link to="/detail" className="hover:text-yellow-300 transition-colors duration-300">
                            Détails Techniques
                        </Link>
                    </li>
                </ul>

                {/* Icônes (côté droit, desktop) */}
                <div className="hidden md:flex space-x-4 items-center">
                    <button
                        className="
              bg-purple-700 text-white font-medium px-4 py-2 rounded-md shadow-md
              hover:bg-purple-800 hover:scale-105 transition-transform duration-300
            "
                        onClick={toggleSidebar}
                    >
                        <FaUser className="inline-block"/>
                    </button>
                    {token && isAdmin ? (
                        <Link
                            to="/parametre"
                            className="
              bg-purple-700 text-white font-medium px-4 py-2 rounded-md shadow-md
              hover:bg-purple-800 hover:scale-105 transition-transform duration-300
            "
                        >
                            <IoMdSettings className="inline-block"/>
                        </Link>
                    ) : null}
                </div>

                {/* Bouton burger (mobile) */}
                <button
                    className="md:hidden focus:outline-none"
                    onClick={handleBurgerClick}
                    aria-label="Toggle menu"
                >
                    <div className="flex flex-col space-y-1">
            <span
                className={`
                block h-1 w-6 bg-white transition-transform duration-300
                ${isMobileMenuOpen ? "rotate-45 translate-y-2" : ""}
              `}
            />
                        <span
                            className={`
                block h-1 w-6 bg-white transition-opacity duration-300
                ${isMobileMenuOpen ? "opacity-0" : ""}
              `}
                        />
                        <span
                            className={`
                block h-1 w-6 bg-white transition-transform duration-300
                ${isMobileMenuOpen ? "-rotate-45 -translate-y-2" : ""}
              `}
                        />
                    </div>
                </button>
            </nav>

            {/* Menu mobile (affiché en accordéon) */}
            {/*
         On réutilise EXACTEMENT le même gradient de fond
         pour ne pas créer de coupure de couleur.
      */}
            <div
                className={`
          md:hidden transition-all duration-300 overflow-hidden
          ${isMobileMenuOpen ? "max-h-80" : "max-h-0"}
        `}
                style={{background: "inherit"}}
                // "inherit" => on reprend le gradient du parent, vous pouvez aussi refaire un bg-gradient identique
            >
                <ul className="flex flex-col items-center text-white text-center space-y-4 py-4">
                    <li>
                        <Link
                            to="/"
                            className="hover:text-yellow-300 text-lg transition-colors duration-300"
                            onClick={() => setMobileMenuOpen(false)}
                        >
                            Accueil
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="/documents"
                            className="hover:text-yellow-300 text-lg transition-colors duration-300"
                            onClick={() => setMobileMenuOpen(false)}
                        >
                            Mes Documents
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="/guide"
                            className="hover:text-yellow-300 text-lg transition-colors duration-300"
                            onClick={() => setMobileMenuOpen(false)}
                        >
                            Guide d'Utilisation
                        </Link>
                    </li>
                    <li>
                        <Link
                            to="/detail"
                            className="hover:text-yellow-300 text-lg transition-colors duration-300"
                            onClick={() => setMobileMenuOpen(false)}
                        >
                            Détails Techniques
                        </Link>
                    </li>
                    {/* Icônes (mobile) */}
                    <li className="flex space-x-4">
                        <button
                            className="
                bg-purple-700 text-white font-medium px-4 py-2 rounded-md shadow-md
                hover:bg-purple-800 hover:scale-105 transition-transform duration-300
              "
                            onClick={() => {
                                toggleSidebar();
                                setMobileMenuOpen(false);
                            }}
                        >
                            <FaUser/>
                        </button>
                        {token && isAdmin ? (
                            <Link
                                to="/parametre"
                                className="
                bg-purple-700 text-white font-medium px-4 py-2 rounded-md shadow-md
                hover:bg-purple-800 hover:scale-105 transition-transform duration-300
              "
                                onClick={() => setMobileMenuOpen(false)}
                            >
                                <IoMdSettings/>
                            </Link>
                        ) : null}
                    </li>
                </ul>
            </div>
        </header>
    );
}

export default Header;
