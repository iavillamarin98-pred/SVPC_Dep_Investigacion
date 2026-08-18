document.addEventListener("DOMContentLoaded", () => {

    fetch("/api/auth/me")
        .then(res => {
            if (!res.ok) throw new Error("No autenticado");
            return res.json();
        })
        .then(usuario => {
            const nombreEl = document.getElementById("nombreUsuarioSesion");
            const rolEl = document.getElementById("rolUsuarioSesion");
            if (nombreEl) nombreEl.textContent = usuario.nombre;
            if (rolEl) rolEl.textContent = usuario.rol;
        })
        .catch(err => console.error("No se pudo cargar el usuario actual:", err));

    const btnLogout = document.getElementById("btnCerrarSesion");
    if (btnLogout) {
        btnLogout.addEventListener("click", () => {
            window.location.href = "/logout";
        });
    }

});