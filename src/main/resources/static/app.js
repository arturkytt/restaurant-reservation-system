const statusBadge = document.getElementById("statusBadge");
const floorGrid = document.getElementById("floorGrid");
const summary = document.getElementById("summary");

const dateInput = document.getElementById("dateInput");
const timeInput = document.getElementById("timeInput");
const partySizeInput = document.getElementById("partySizeInput");
const zoneSelect = document.getElementById("zoneSelect");

const refreshBtn = document.getElementById("refreshBtn");
const recommendBtn = document.getElementById("recommendBtn");

const recommendationBox = document.getElementById("recommendationBox");
const topCandidates = document.getElementById("topCandidates");

let tables = [];
let availability = [];
let recommendedId = null;

function todayISO() {
    const d = new Date();
    const yyyy = d.getFullYear();
    const mm = String(d.getMonth() + 1).padStart(2, "0");
    const dd = String(d.getDate()).padStart(2, "0");
    return `${yyyy}-${mm}-${dd}`;
}

function defaultTime() {
    return "18:00";
}

function apiUrl(path) {
    return `${path}`;
}

async function fetchJson(url) {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
    return res.json();
}

function buildAvailabilityUrl() {
    const date = dateInput.value;
    const time = timeInput.value;
    const partySize = partySizeInput.value;
    const zone = zoneSelect.value;

    const params = new URLSearchParams({ date, time, partySize });
    if (zone) params.append("zone", zone);

    return apiUrl(`/api/availability?${params.toString()}`);
}

function selectedFeaturesCsv() {
    return Array.from(document.querySelectorAll('.checks input[type="checkbox"]:checked'))
        .map(cb => cb.value)
        .join(",");
}

function buildRecommendationUrl() {
    const date = dateInput.value;
    const time = timeInput.value;
    const partySize = partySizeInput.value;
    const zone = zoneSelect.value;

    const features = selectedFeaturesCsv();

    const params = new URLSearchParams({ date, time, partySize });
    if (zone) params.append("zone", zone);
    if (features) params.append("features", features);

    return apiUrl(`/api/recommendation?${params.toString()}`);
}

function availabilityById(id) {
    return availability.find(a => a.id === id);
}
function displayZone(zone) {
    const map = {
        MAIN_HALL: "MAIN",
        TERRACE: "TERRACE",
        PRIVATE_ROOM: "PRIVATE"
    };
    return map[zone] || zone;
}

function renderFloor() {
    floorGrid.innerHTML = "";

    // grid position: we’ll map x,y to CSS grid columns
    // (simple approach: x->col, y->row using inline style)
    tables.forEach(t => {
        const av = availabilityById(t.id);
        const occupied = av ? av.occupied : false;
        const suitable = av ? av.suitable : true;

        const tile = document.createElement("div");
        tile.className = "table-tile";
        tile.style.gridColumn = `${t.x} / span 2`;
        tile.style.gridRow = `${t.y} / span 2`;

        if (occupied) tile.classList.add("occupied");
        else tile.classList.add("free");

        if (!occupied && !suitable) tile.classList.add("unsuitable");
        if (recommendedId && t.id === recommendedId) tile.classList.add("recommended");

        const features = (t.features || []).join(", ");
        tile.innerHTML = `
      <div class="top">
        <div class="code">${t.code}</div>
        <div class="kv">${displayZone(t.zone)}</div>
      </div>
      <div class="meta">cap: ${t.capacity} · (${t.x},${t.y})</div>
      <div class="meta">${features || "—"}</div>
    `;

        floorGrid.appendChild(tile);
    });

    const freeCount = availability.filter(a => !a.occupied).length;
    const occCount = availability.filter(a => a.occupied).length;
    summary.textContent = `Tables: ${tables.length} · Free: ${freeCount} · Occupied: ${occCount}`;
}

function renderRecommendation(resp) {
    if (!resp || !resp.recommended) {
        recommendationBox.innerHTML = `<div class="muted">No suitable table found.</div>`;
        topCandidates.innerHTML = `<div class="muted">—</div>`;
        recommendedId = null;
        renderFloor();
        return;
    }

    const r = resp.recommended;
    recommendedId = r.id;

    recommendationBox.innerHTML = `
    <div><strong>${r.code}</strong> <span class="kv">(${displayZone(r.zone)})</span></div>
    <div class="muted">capacity: ${r.capacity} · score: <span class="score">${r.score}</span></div>
    <div class="muted">features: ${(r.features || []).join(", ") || "—"}</div>
  `;

    const list = resp.topCandidates || [];
    topCandidates.innerHTML = list.length ? "" : `<div class="muted">—</div>`;
    list.forEach(c => {
        const row = document.createElement("div");
        row.className = "row";
        row.innerHTML = `
      <div>
        <div><strong>${c.code}</strong> <span class="kv">${displayZone(c.zone)}</span></div>
        <div class="kv">cap ${c.capacity} · ${(c.features || []).join(", ") || "—"}</div>
      </div>
      <div class="score">${c.score}</div>
    `;
        topCandidates.appendChild(row);
    });

    renderFloor();
}

async function refreshAll() {
    try {
        statusBadge.textContent = "Loading…";
        statusBadge.style.opacity = "1";

        tables = await fetchJson(apiUrl("/api/tables"));
        availability = await fetchJson(buildAvailabilityUrl());

        statusBadge.textContent = "Connected";
        renderFloor();
    } catch (e) {
        statusBadge.textContent = "Error";
        recommendationBox.innerHTML = `<div class="muted">${e.message}</div>`;
    }
}

async function recommend() {
    try {
        const resp = await fetchJson(buildRecommendationUrl());
        renderRecommendation(resp);
    } catch (e) {
        recommendationBox.innerHTML = `<div class="muted">${e.message}</div>`;
    }
}

function init() {
    dateInput.value = todayISO();
    timeInput.value = defaultTime();

    refreshBtn.addEventListener("click", async () => {
        recommendedId = null;
        await refreshAll();
    });

    recommendBtn.addEventListener("click", async () => {
        recommendedId = null;
        await refreshAll();
        await recommend();
    });

    refreshAll();
}

init();