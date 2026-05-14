// Constructor de CV
function bootCvConstructor() {
    document.querySelectorAll(".mono-area").forEach((textarea) => {
        if (textarea.dataset.autoSizeReady === "true") return;
        textarea.dataset.autoSizeReady = "true";
        textarea.addEventListener("input", () => {
            textarea.style.height = "auto";
            textarea.style.height = `${textarea.scrollHeight}px`;
        });
        textarea.dispatchEvent(new Event("input"));
    });

    document.querySelectorAll("[data-cv-builder]").forEach(initCvBuilder);
    document.querySelectorAll("[data-cv-edit-form]").forEach((form) => {
        if (form.dataset.cvSubmitReady === "true") return;
        form.dataset.cvSubmitReady = "true";
        form.addEventListener("submit", () => {
            form.querySelectorAll("[data-cv-builder]").forEach(serializeCvBuilder);
        });
    });
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", bootCvConstructor);
} else {
    bootCvConstructor();
}

window.addEventListener("pageshow", bootCvConstructor);

const cvBuilderConfigs = {
    education: ["institution", "degree", "field", "start", "end", "description"],
    experience: ["company", "position", "start", "end", "type", "responsibilities", "achievements"],
    skills: ["name", "category", "level"],
    languages: ["name", "level", "certification"],
    certifications: ["name", "issuer", "issueDate", "expirationDate", "credentialId", "credentialUrl"]
};

function initCvBuilder(section) {
    if (section.dataset.builderReady === "true") {
        serializeCvBuilder(section);
        return;
    }
    section.dataset.builderReady = "true";

    const type = section.dataset.cvBuilder;
    const fields = cvBuilderConfigs[type];
    const rawField = section.querySelector("[data-raw-field]");
    const list = section.querySelector("[data-builder-list]");
    const template = section.querySelector("[data-row-template]");
    const addButton = section.querySelector("[data-add-row]");
    const clearButton = section.querySelector("[data-clear-rows]");

    if (!fields || !rawField || !list || !template) return;

    parseCvRows(rawField.value, fields, type).forEach((values) => addCvRow(section, values));
    updateCvBuilderState(section);

    if (addButton) {
        addButton.addEventListener("click", () => {
            addCvRow(section, {});
            updateCvBuilderState(section);
            const lastRow = list.querySelector("[data-builder-row]:last-child input, [data-builder-row]:last-child select, [data-builder-row]:last-child textarea");
            if (lastRow) lastRow.focus();
        });
    }

    if (clearButton) {
        clearButton.addEventListener("click", () => {
            list.innerHTML = "";
            rawField.value = "";
            updateCvBuilderState(section);
        });
    }
}

function addCvRow(section, values) {
    const type = section.dataset.cvBuilder;
    const fields = cvBuilderConfigs[type];
    const list = section.querySelector("[data-builder-list]");
    const template = section.querySelector("[data-row-template]");
    const fragment = template.content.cloneNode(true);
    const row = fragment.querySelector("[data-builder-row]");

    fields.forEach((field) => {
        const control = row.querySelector(`[data-field="${field}"]`);
        if (!control) return;
        control.value = values[field] || defaultCvValue(type, field);
        control.addEventListener("input", () => serializeCvBuilder(section));
        control.addEventListener("change", () => serializeCvBuilder(section));
    });

    const removeButton = row.querySelector("[data-remove-row]");
    if (removeButton) {
        removeButton.addEventListener("click", () => {
            row.remove();
            serializeCvBuilder(section);
            updateCvBuilderState(section);
        });
    }

    list.appendChild(fragment);
    serializeCvBuilder(section);
    updateCvBuilderState(section);
}

function parseCvRows(text, fields, type) {
    return (text || "")
        .split(/\r?\n/)
        .map((line) => line.trim())
        .filter(Boolean)
        .map((line) => {
            const parts = line.split("|");
            return fields.reduce((row, field, index) => {
                row[field] = cleanCvValue(parts[index]);
                return row;
            }, {});
        })
        .filter((row) => usefulCvRow(type, row));
}

function serializeCvBuilder(section) {
    const type = section.dataset.cvBuilder;
    const fields = cvBuilderConfigs[type];
    const rawField = section.querySelector("[data-raw-field]");
    const rows = Array.from(section.querySelectorAll("[data-builder-row]"))
        .map((row) => fields.map((field) => cleanCvValue(fieldValue(row, field))))
        .filter((parts) => usefulCvParts(type, fields, parts));

    rawField.value = rows.map((parts) => parts.join("|")).join("\n");
    updateCvBuilderState(section);
}

function fieldValue(row, field) {
    const control = row.querySelector(`[data-field="${field}"]`);
    return control ? control.value : "";
}

function cleanCvValue(value) {
    const normalized = (value || "").trim();
    return normalized.toLowerCase() === "null" || normalized === "/" ? "" : normalized;
}

function defaultCvValue(type, field) {
    if (type === "skills" && field === "category") return "technical";
    if (type === "skills" && field === "level") return "3";
    if (type === "languages" && field === "level") return "basic";
    return "";
}

function usefulCvRow(type, row) {
    return usefulCvParts(type, cvBuilderConfigs[type], cvBuilderConfigs[type].map((field) => row[field] || ""));
}

function usefulCvParts(type, fields, parts) {
    const value = (field) => parts[fields.indexOf(field)] || "";
    const hasAny = parts.some(Boolean);
    if (!hasAny) return false;

    if (type === "education") {
        return Boolean(value("institution") && (value("degree") || value("field") || value("start") || value("end") || value("description")));
    }
    if (type === "experience") {
        const company = value("company").toLowerCase();
        return Boolean(value("company") && value("position") && !company.includes("sin experiencia") && !company.includes("no aplica"));
    }
    if (type === "skills") return Boolean(value("name"));
    if (type === "languages") return Boolean(value("name"));
    if (type === "certifications") {
        return Boolean(value("name") && (value("issuer") || value("issueDate") || value("expirationDate") || value("credentialId") || value("credentialUrl")));
    }
    return hasAny;
}

function updateCvBuilderState(section) {
    const emptyState = section.querySelector("[data-empty-state]");
    const hasRows = Boolean(section.querySelector("[data-builder-row]"));
    if (emptyState) emptyState.classList.toggle("d-none", hasRows);
}
