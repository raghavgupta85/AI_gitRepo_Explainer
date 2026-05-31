import { useEffect, useState } from "react";

function useTheme() {

    const [darkMode, setDarkMode] =
        useState(

            JSON.parse(
                localStorage.getItem(
                    "darkMode"
                )
            ) ?? true
        );

    useEffect(() => {

        document.body.className =
            darkMode
                ? "dark-theme"
                : "light-theme";

        localStorage.setItem(
            "darkMode",
            JSON.stringify(darkMode)
        );

    }, [darkMode]);

    return {
        darkMode,
        setDarkMode
    };
}

export default useTheme;