// Validaciones del cliente
document.addEventListener("DOMContentLoaded", () => {
    const roleField = document.querySelector("[data-role-toggle]");
    const graduateFields = document.querySelector("[data-graduate-fields]");
    if (!roleField || !graduateFields) {
        return;
    }

    const syncRole = () => {
        graduateFields.style.display = roleField.value === "graduate" ? "flex" : "none";
    };

    roleField.addEventListener("change", syncRole);
    syncRole();
});
