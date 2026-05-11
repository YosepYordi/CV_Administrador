// Constructor de CV
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".mono-area").forEach((textarea) => {
        textarea.addEventListener("input", () => {
            textarea.style.height = "auto";
            textarea.style.height = `${textarea.scrollHeight}px`;
        });
        textarea.dispatchEvent(new Event("input"));
    });
});
