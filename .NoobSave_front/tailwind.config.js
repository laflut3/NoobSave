/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        "./src/**/*.{js,jsx,ts,tsx}",
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
                sans: ["Inter", "sans-serif"],
                mono: ["Roboto Mono", "monospace"],
            },
            container: {
                center: true,
                padding: "1rem",
            },
        },
        keyframes: {
            fadeIn: {
                '0%': {opacity: 0, transform: 'translateY(10px)'},
                '100%': {opacity: 1, transform: 'translateY(0)'},
            },
        },
        animation: {
            fadeIn: 'fadeIn 0.8s ease-out',
        },

    },
    plugins: [],
};
