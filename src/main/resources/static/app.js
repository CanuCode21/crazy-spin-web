const CONFIG = {
    spinDurationMs: 7000,
    wheelTurns: 5,
    defaultChip: 10,
    canvasSize: 500,
    outerRadiusOffset: 8,
    innerRadius: 78,
    pointerAngleDeg: 270,
    gridRows: 12,
    gridCols: 9,
    targetCount: 108,
    bonusKeywords: ["COIN", "PACHIN", "TARGET", "CRAZY"],
    endpoints: {
        spin: "/api/ruota-principale/gira",
        CRAZY_SPIN: "/api/crazy-time/gioca",
        COIN_CLASH: "/api/coin-flip/gioca",
        PACHIN_SPIN: "/api/pachinko/gioca",
        TARGET_HUNT: "/api/cash-hunt/gioca"
    }
};

const WHEEL_SECTORS = [
    "1", "2", "5", "1", "2", "10", "1", "2", "1", "5", "1", "2", "COIN_CLASH",
    "1", "2", "5", "1", "2", "10", "1", "2", "1", "TARGET_HUNT", "1", "2", "5",
    "1", "2", "PACHIN_SPIN", "1", "2", "1", "5", "1", "2", "10", "1", "2", "1",
    "5", "1", "2", "COIN_CLASH", "1", "2", "5", "1", "2", "TARGET_HUNT", "1",
    "2", "PACHIN_SPIN", "1", "5", "CRAZY_SPIN"
];

const SECTOR_COLORS = {
    "1": "#00897b",
    "2": "#f57c00",
    "5": "#d81b60",
    "10": "#5e35b1",
    "COIN_CLASH": "#0288d1",
    "PACHIN_SPIN": "#ad1457",
    "TARGET_HUNT": "#2e7d32",
    "CRAZY_SPIN": "#d32f2f"
};

const NUMBER_BET_KEYS = ["p1", "p2", "p5", "p10"];
const BONUS_BET_KEYS = ["pCF", "pPC", "pCH", "pCT"];

function createEmptyBets() {
    return {
        p1: 0, p2: 0, p5: 0, p10: 0,
        pCF: 0, pPC: 0, pCH: 0, pCT: 0
    };
}

const state = {
    activeBonus: "",
    selectedChip: CONFIG.defaultChip,
    wheelAngleDeg: 0,
    isSpinning: false,
    selectedTarget: null,
    bets: createEmptyBets(),
    lastRoundBets: {}
};

// --- DOM & SALDO ---

function getSaldo() {
    const input = document.getElementById("saldo");
    return input ? (parseInt(input.value, 10) || 0) : 0;
}

function setSaldo(nuovoSaldo) {
    const input = document.getElementById("saldo");
    if (input) input.value = Math.max(0, nuovoSaldo);
}

function showNotice(message) {
    const box = document.getElementById("risultato");
    const text = document.getElementById("txtEsito");
    if (box && text) {
        box.style.display = "block";
        text.innerHTML = `<span class="avviso">${message}</span>`;
    }
}

function getTotalBets(betsObj = state.bets) {
    return Object.values(betsObj).reduce((sum, amount) => sum + amount, 0);
}

function renderBetAmounts() {
    Object.entries(state.bets).forEach(([key, amount]) => {
        const el = document.getElementById(`amt-${key}`);
        if (el) el.innerText = `${amount} €`;
    });
}

function updateRepeatButtonState() {
    const btnRepeat = document.getElementById("btnRipeti");
    if (!btnRepeat) return;
    const hasPreviousBet = getTotalBets(state.lastRoundBets) > 0;
    btnRepeat.disabled = state.isSpinning || !hasPreviousBet;
}

function setBettingControlsLocked(locked) {
    state.isSpinning = locked;
    const lockableSelectors = [".quick-bet-bar", ".bet-grid", ".chip-bar", ".btn-clear", "#btnRipeti"];

    lockableSelectors.forEach(selector => {
        const el = document.querySelector(selector);
        if (el) el.classList.toggle("betting-locked", locked);
    });

    const btnClear = document.querySelector(".btn-clear");
    const btnSpin = document.getElementById("btnGira");
    if (btnClear) btnClear.disabled = locked;
    if (btnSpin) btnSpin.disabled = locked;

    updateRepeatButtonState();
}

// --- DISEGNO RUOTA (CANVAS) ---

function drawWheelSectors(ctx, center, outerRadius) {
    const count = WHEEL_SECTORS.length;
    const arc = (2 * Math.PI) / count;

    WHEEL_SECTORS.forEach((sector, index) => {
        const startAngle = index * arc;
        const endAngle = startAngle + arc;

        ctx.beginPath();
        ctx.fillStyle = SECTOR_COLORS[sector] || "#333";
        ctx.moveTo(center, center);
        ctx.arc(center, center, outerRadius, startAngle, endAngle);
        ctx.lineTo(center, center);
        ctx.fill();

        ctx.lineWidth = 1;
        ctx.strokeStyle = "rgba(255, 255, 255, 0.25)";
        ctx.stroke();

        ctx.save();
        ctx.translate(center, center);
        ctx.rotate(startAngle + arc / 2);
        ctx.textAlign = "right";
        ctx.fillStyle = "#fff";
        ctx.shadowColor = "rgba(0,0,0,0.8)";
        ctx.shadowBlur = 3;
        ctx.font = "900 11px 'Segoe UI', sans-serif";
        ctx.fillText(sector.replace("_", " "), outerRadius - 18, 4);
        ctx.restore();
    });
}

function drawWheelCenter(ctx, center, outerRadius, innerRadius) {
    ctx.beginPath();
    ctx.arc(center, center, outerRadius, 0, 2 * Math.PI);
    ctx.lineWidth = 10;
    ctx.strokeStyle = "#ffd700";
    ctx.stroke();

    ctx.beginPath();
    ctx.arc(center, center, innerRadius, 0, 2 * Math.PI);
    const gradient = ctx.createRadialGradient(center, center, 10, center, center, innerRadius);
    gradient.addColorStop(0, "#ff1744");
    gradient.addColorStop(1, "#7f0000");
    ctx.fillStyle = gradient;
    ctx.fill();
    ctx.lineWidth = 5;
    ctx.strokeStyle = "#ffd700";
    ctx.stroke();

    ctx.save();
    ctx.textAlign = "center";
    ctx.textBaseline = "middle";
    ctx.shadowColor = "rgba(0,0,0,0.9)";
    ctx.shadowBlur = 5;

    ctx.fillStyle = "#fff59d";
    ctx.font = "900 17px 'Segoe UI', sans-serif";
    ctx.fillText("CRAZY", center, center - 10);

    ctx.fillStyle = "#ffffff";
    ctx.font = "900 20px 'Segoe UI', sans-serif";
    ctx.fillText("SPIN", center, center + 12);
    ctx.restore();
}

function disegnaRuota() {
    const canvas = document.getElementById("ruotaCanvas");
    if (!canvas) return;

    const ctx = canvas.getContext("2d");
    const center = canvas.width / 2;
    const outerRadius = center - CONFIG.outerRadiusOffset;

    ctx.clearRect(0, 0, canvas.width, canvas.height);
    drawWheelSectors(ctx, center, outerRadius);
    drawWheelCenter(ctx, center, outerRadius, CONFIG.innerRadius);
}

// --- AZIONI DI PUNTATA ---

function selezionaChip(value, clickedElement) {
    if (state.isSpinning) return;
    state.selectedChip = value;

    const label = document.getElementById("labelChipSelezionata");
    if (label) label.innerText = `${value} €`;

    document.querySelectorAll(".chip").forEach(chip => chip.classList.remove("selected"));
    if (clickedElement) clickedElement.classList.add("selected");
}

function aggiungiPuntata(betKey) {
    if (state.isSpinning) return;
    const currentBalance = getSaldo();

    if (currentBalance < state.selectedChip) {
        showNotice("Saldo insufficiente per questa puntata.");
        return;
    }

    state.bets[betKey] += state.selectedChip;
    setSaldo(currentBalance - state.selectedChip);
    renderBetAmounts();
}

function applyGroupBet(keys) {
    if (state.isSpinning) return;
    const totalCost = keys.length * state.selectedChip;
    const currentBalance = getSaldo();

    if (currentBalance < totalCost) {
        showNotice("Saldo insufficiente per completare la puntata rapida.");
        return;
    }

    keys.forEach(key => {
        state.bets[key] += state.selectedChip;
    });
    setSaldo(currentBalance - totalCost);
    renderBetAmounts();
}

function caricaTuttiNumeri() {
    applyGroupBet(NUMBER_BET_KEYS);
}

function caricaTuttiBonus() {
    applyGroupBet(BONUS_BET_KEYS);
}

function pulisciPuntate() {
    if (state.isSpinning) return;
    setSaldo(getSaldo() + getTotalBets());
    state.bets = createEmptyBets();
    renderBetAmounts();
}

function ripetiPuntata() {
    if (state.isSpinning) return;
    const totalToRepeat = getTotalBets(state.lastRoundBets);
    if (totalToRepeat === 0) return;

    const availableBalance = getSaldo() + getTotalBets();
    if (availableBalance < totalToRepeat) {
        showNotice("Saldo insufficiente per ripetere l'ultima puntata.");
        return;
    }

    Object.keys(state.bets).forEach(key => {
        state.bets[key] = state.lastRoundBets[key] || 0;
    });

    setSaldo(availableBalance - totalToRepeat);
    renderBetAmounts();
}

// --- GIRO RUOTA & BONUS ---

function normalizeBonusSector(sectorName) {
    if (sectorName.includes("COIN")) return "COIN_CLASH";
    if (sectorName.includes("PACHIN")) return "PACHIN_SPIN";
    if (sectorName.includes("TARGET")) return "TARGET_HUNT";
    if (sectorName.includes("CRAZY")) return "CRAZY_SPIN";
    return "";
}

function renderMainSpinResult(data, bonusType) {
    const box = document.getElementById("risultato");
    const text = document.getElementById("txtEsito");
    if (!box || !text) return;

    box.style.display = "block";
    let html = `<strong>Risultato:</strong> ${data.spicchioUscito}`;

    if (!bonusType) {
        html += ` &nbsp;|&nbsp; <strong>Vincita:</strong> <span class="win-highlight">${data.vincita}€</span>`;
    }
    if (data.messaggio) {
        html += `<span class="avviso">${data.messaggio}</span>`;
    }

    text.innerHTML = html;
}

function selezionaBersaglio(index) {
    document.querySelectorAll(".bersaglio").forEach(cell => cell.classList.remove("scelto"));
    const selectedCell = document.getElementById(`cell-${index}`);
    if (selectedCell) selectedCell.classList.add("scelto");

    state.selectedTarget = index;
    const btnBonus = document.getElementById("btnGiocaBonus");
    if (btnBonus) btnBonus.style.display = "block";
}

function renderBonusSetup(bonusType) {
    const section = document.getElementById("sezione-bonus");
    const btnBonus = document.getElementById("btnGiocaBonus");
    const form = document.getElementById("form-scelta-bonus");
    const info = document.getElementById("txtInfoBonus");
    const title = document.getElementById("titolo-bonus");
    const resultBox = document.getElementById("risultato-bonus");

    if (resultBox) resultBox.innerHTML = "";

    if (!bonusType) {
        if (section) section.style.display = "none";
        return;
    }

    section.style.display = "block";
    btnBonus.style.display = "block";
    title.innerText = `Bonus: ${bonusType.replace("_", " ")}!`;
    form.innerHTML = "";
    state.selectedTarget = null;

    if (bonusType === "CRAZY_SPIN") {
        info.innerText = "Scegli il tuo flapper e avvia la ruota bonus!";
        form.innerHTML = `
            <label for="sceltaFlapper" class="flapper-label">Scegli il Colore del Flapper:</label>
            <select id="sceltaFlapper" class="flapper-select">
                <option value="VERDE">Verde</option>
                <option value="BLU">Blu</option>
                <option value="GIALLO">Giallo</option>
            </select>
        `;
    } else if (bonusType === "COIN_CLASH") {
        info.innerText = "Coin Clash attivato! Lancia la moneta.";
    } else if (bonusType === "PACHIN_SPIN") {
        info.innerText = "Pachin Spin attivato! Fai cadere il disco.";
    } else if (bonusType === "TARGET_HUNT") {
        info.innerText = "Seleziona un bersaglio sulla griglia per sparare:";
        btnBonus.style.display = "none";

        let gridHtml = `<div id="griglia-cash-hunt">`;
        for (let i = 0; i < CONFIG.targetCount; i++) {
            gridHtml += `<button type="button" class="bersaglio" id="cell-${i}" onclick="selezionaBersaglio(${i})">${i}</button>`;
        }
        gridHtml += `</div>`;
        form.innerHTML = gridHtml;
    }
}

function computeWheelTargetAngle(winningIndex) {
    const degreesPerSlice = 360 / WHEEL_SECTORS.length;
    const targetDeg = CONFIG.pointerAngleDeg - (winningIndex * degreesPerSlice) - (degreesPerSlice / 2);
    const normalizedCurrent = state.wheelAngleDeg % 360;
    return state.wheelAngleDeg + (360 * CONFIG.wheelTurns) + (targetDeg - normalizedCurrent);
}

async function giraRuota() {
    if (state.isSpinning) return;

    const currentBalance = getSaldo();
    const totalBet = getTotalBets();

    setBettingControlsLocked(true);
    state.lastRoundBets = { ...state.bets };

    const payload = {
        saldoCorrente: currentBalance + totalBet,
        puntataUno: state.bets.p1,
        puntataDue: state.bets.p2,
        puntataCinque: state.bets.p5,
        puntataDieci: state.bets.p10,
        puntataCF: state.bets.pCF,
        puntataPC: state.bets.pPC,
        puntataCH: state.bets.pCH,
        puntataCT: state.bets.pCT
    };

    try {
        const response = await fetch(CONFIG.endpoints.spin, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();
        state.wheelAngleDeg = computeWheelTargetAngle(data.indiceUscito);

        const canvas = document.getElementById("ruotaCanvas");
        if (canvas) canvas.style.transform = `rotate(${state.wheelAngleDeg}deg)`;

        setTimeout(() => {
            setSaldo(data.nuovoSaldo);
            state.bets = createEmptyBets();
            renderBetAmounts();

            state.activeBonus = normalizeBonusSector(data.spicchioUscito);
            renderMainSpinResult(data, state.activeBonus);
            renderBonusSetup(state.activeBonus);
            setBettingControlsLocked(false);
        }, CONFIG.spinDurationMs);

    } catch (error) {
        console.error("Errore durante il giro:", error);
        showNotice("Impossibile contattare il backend Spring Boot.");
        setBettingControlsLocked(false);
    }
}

function buildBonusPayload(bonusType) {
    switch (bonusType) {
        case "CRAZY_SPIN":
            return {
                coloreScelto: document.getElementById("sceltaFlapper")?.value || "VERDE",
                puntata: state.lastRoundBets.pCT || 0
            };
        case "COIN_CLASH":
            return { puntata: state.lastRoundBets.pCF || 0 };
        case "PACHIN_SPIN":
            return { puntata: state.lastRoundBets.pPC || 0 };
        case "TARGET_HUNT":
            return {
                bersaglioScelto: state.selectedTarget ?? 0,
                puntata: state.lastRoundBets.pCH || 0
            };
        default:
            return null;
    }
}

function revealTargetMatrix(matrix, chosenIndex) {
    if (!Array.isArray(matrix)) return;
    let index = 0;

    for (let r = 0; r < CONFIG.gridRows; r++) {
        for (let c = 0; c < CONFIG.gridCols; c++) {
            const cell = document.getElementById(`cell-${index}`);
            if (cell && matrix[r] && matrix[r][c] !== undefined) {
                cell.innerText = `${matrix[r][c]}x`;
                cell.classList.add("rivelato");
                if (index === chosenIndex) {
                    cell.classList.add("target-hit");
                }
            }
            index++;
        }
    }
}

function formatBonusResultHtml(bonusType, data) {
    const winTag = `<span class="win-highlight">${data.vincita}€</span>`;

    if (bonusType === "CRAZY_SPIN") {
        return `<div class="bonus-result-box">Verde: ${data.moltiplicatoreVerde}x | Blu: ${data.moltiplicatoreBlu}x | Giallo: ${data.moltiplicatoreGiallo}x<br>` +
            `Scelto (${data.coloreScelto}): <strong>${data.moltiplicatoreVinto}x</strong> &rarr; ${winTag}</div>`;
    }
    if (bonusType === "COIN_CLASH") {
        return `<div class="bonus-result-box">Uscito: <strong>${data.coloreUscito || "N/D"}</strong> ` +
            `(Rosso: ${data.moltiplicatoreRosso || "N/D"}x | Blu: ${data.moltiplicatoreBlu || "N/D"}x) &rarr; ${winTag}</div>`;
    }
    if (bonusType === "PACHIN_SPIN") {
        return `<div class="bonus-result-box">Moltiplicatore: <strong>${data.moltiplicatore || "N/D"}x</strong> &rarr; ${winTag}</div>`;
    }
    if (bonusType === "TARGET_HUNT") {
        revealTargetMatrix(data.matrice, data.bersaglioSelezionato);
        return `<div class="bonus-result-box">Bersaglio #${data.bersaglioScelto ?? "N/D"}: ` +
            `<strong>${data.moltiplicatoreTrovato || "N/D"}x</strong> &rarr; ${winTag}</div>`;
    }

    return `<div class="bonus-result-box"><strong>Vincita Bonus:</strong> ${winTag}</div>`;
}

async function giocaBonusCorrente() {
    const url = CONFIG.endpoints[state.activeBonus];
    const payload = buildBonusPayload(state.activeBonus);
    if (!url || !payload) return;

    try {
        const response = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();
        setSaldo(getSaldo() + (data.vincita || 0));

        const resultBox = document.getElementById("risultato-bonus");
        if (resultBox) {
            resultBox.innerHTML = formatBonusResultHtml(state.activeBonus, data);
        }

        const btnBonus = document.getElementById("btnGiocaBonus");
        if (btnBonus) btnBonus.style.display = "none";
    } catch (error) {
        console.error("Errore minigioco bonus:", error);
        showNotice("Errore durante l'esecuzione del bonus.");
    }
}

window.onload = () => {
    disegnaRuota();
    renderBetAmounts();
    updateRepeatButtonState();
};