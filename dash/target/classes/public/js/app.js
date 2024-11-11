let shiftStartTime = null;
let shiftDuration = 8 * 60 * 60 * 1000; // 8 часов в миллисекундах
let shiftTimer = null;
let isPaused = false;
let pauseStartTime = null;
let totalPauseTime = 0;
const pauseLimit = 75 * 60 * 1000; // 75 минут в миллисекундах
let pauseTimer = null;

function startShift() {
    shiftStartTime = Date.now();
    isPaused = false;
    totalPauseTime = 0;
    document.getElementById("shift-status").innerText = "Смена активна";
    document.getElementById("pause-remaining").innerText = ""; // Очистить оставшееся время паузы
    shiftTimer = setInterval(updateShiftTime, 1000);
}

function endShift() {
    clearInterval(shiftTimer);
    clearInterval(pauseTimer);
    document.getElementById("shift-status").innerText = "Смена завершена";
    document.getElementById("time-passed").innerText = "Прошло: 00:00:00";
    document.getElementById("time-remaining").innerText = "Осталось: 00:00:00";
    document.getElementById("pause-remaining").innerText = ""; // Очистить оставшееся время паузы
}

function pauseShift() {
    if (!isPaused) {
        isPaused = true;
        pauseStartTime = Date.now();
        document.getElementById("shift-status").innerText = "Смена на паузе";
        pauseTimer = setInterval(updatePauseTime, 1000);
    } else {
        isPaused = false;
        const pauseEndTime = Date.now();
        totalPauseTime += pauseEndTime - pauseStartTime;
        document.getElementById("shift-status").innerText = "Смена активна";
        clearInterval(pauseTimer);
        document.getElementById("pause-remaining").innerText = ""; // Очистить оставшееся время паузы

        if (totalPauseTime >= pauseLimit) {
            alert("Пауза превышает лимит в 75 минут. Смена завершена.");
            endShift();
        }
    }
}

function updateShiftTime() {
    if (!isPaused) {
        const now = Date.now();
        const elapsed = now - shiftStartTime - totalPauseTime;
        const remaining = shiftDuration - elapsed;

        document.getElementById("time-passed").innerText = `Прошло: ${formatTime(elapsed)}`;
        document.getElementById("time-remaining").innerText = `Осталось: ${formatTime(remaining)}`;

        if (remaining <= 0) {
            endShift();
            alert("Смена завершена!");
        }
    }
}

function updatePauseTime() {
    const now = Date.now();
    const pausedTime = now - pauseStartTime + totalPauseTime;
    const remainingPauseTime = pauseLimit - pausedTime;

    document.getElementById("pause-remaining").innerText = `Осталось на паузе: ${formatTime(remainingPauseTime)}`;

    if (remainingPauseTime <= 0) {
        alert("Пауза превышает лимит в 75 минут. Смена завершена.");
        endShift();
    }
}

function formatTime(milliseconds) {
    const totalSeconds = Math.floor(milliseconds / 1000);
    const hours = Math.floor(totalSeconds / 3600).toString().padStart(2, '0');
    const minutes = Math.floor((totalSeconds % 3600) / 60).toString().padStart(2, '0');
    const seconds = (totalSeconds % 60).toString().padStart(2, '0');
    return `${hours}:${minutes}:${seconds}`;
}
