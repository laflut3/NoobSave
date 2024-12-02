/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/**/*.{js,jsx,ts,tsx}", // Ajuste selon la structure de ton projet
        "./public/index.html",
    ],
    theme: {
        extend: {
            colors: {
                primary: "#1E3A8A", // Bleu foncé
                secondary: "#9333EA", // Violet
                accent: "#F59E0B", // Jaune
            },
            fontFamily: {
                sans: ["Inter", "sans-serif"], // Ajoute une police moderne
                mono: ["Roboto Mono", "monospace"],
            },
            container: {
                center: true,
                padding: "1rem",
            },
        },
    },
    plugins: [],
};
