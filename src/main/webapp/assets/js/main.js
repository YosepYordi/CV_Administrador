// Script principal
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".auto-dismiss").forEach((element) => {
        window.setTimeout(() => element.remove(), 4500);
    });

    const currentPath = window.location.pathname.replace(/\/$/, "");
    document.querySelectorAll("[data-nav-link]").forEach((link) => {
        const linkPath = new URL(link.href, window.location.origin).pathname.replace(/\/$/, "");
        const isActive = currentPath === linkPath || currentPath.startsWith(linkPath + "/");
        link.classList.toggle("is-active", isActive);
        if (isActive) {
            link.setAttribute("aria-current", "page");
        }
    });
});
