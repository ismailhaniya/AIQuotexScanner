// ================================
// AIQuotexScanner V16 Core Engine
// ================================

let scanning = false;
let timer = 180;
let countdown = null;

const history = [];

const signals = ["UP", "DOWN", "WAIT"];
const trends = ["BULLISH", "BEARISH", "SIDEWAYS"];
const patterns = [
  "Bullish Engulfing",
  "Bearish Engulfing",
  "Doji",
  "Hammer",
  "Shooting Star",
  "Inside Candle"
];

function startScan() {

  if (scanning) return;

  scanning = true;
  timer = 180;

  setStatus("SCANNING...");
  setOverlay("CONNECTED");
  setScanner("ACTIVE");

  document.getElementById("captureStatus").innerText = "Permission Granted";
  document.getElementById("chartStatus").innerText = "Chart Detected";

  runAnalysis();

  countdown = setInterval(() => {

    timer--;

    updateTimer();

    if (timer % 10 === 0) {
      runAnalysis();
    }

    if (timer <= 0) {

      clearInterval(countdown);

      scanning = false;

      setStatus("SCAN COMPLETE");
      setScanner("IDLE");

      document.getElementById("captureStatus").innerText = "Waiting";
    }

  },1000);
}

function runAnalysis(){

  const signal = random(signals);
  const trend = random(trends);
  const pattern = random(patterns);

  const confidence = randomNumber(70,96);
  const rsi = randomNumber(25,75);
  const ema9 = randomPrice();
  const ema21 = randomPrice();
  const momentum = randomNumber(-100,100);

  document.getElementById("signal").innerText = signal;
  document.getElementById("confidence").innerText =
      confidence + "% Confidence";

  document.getElementById("rsiValue").innerText = rsi;
  document.getElementById("ema9Value").innerText = ema9;
  document.getElementById("ema21Value").innerText = ema21;
  document.getElementById("momentumValue").innerText = momentum;
  document.getElementById("trendValue").innerText = trend;
  document.getElementById("patternValue").innerText = pattern;

  document.getElementById("candleStatus").innerText =
      randomNumber(18,40) + " Candles";

  addHistory(signal, confidence, trend);
}

function addHistory(signal, confidence, trend){

  const market =
      document.getElementById("market").value;

  history.unshift({
    signal,
    confidence,
    trend,
    market,
    time:new Date().toLocaleTimeString()
  });

  if(history.length > 8){
    history.pop();
  }

  renderHistory();
}

function renderHistory(){

  const box =
      document.getElementById("signalHistory");

  box.innerHTML = "";

  history.forEach(item=>{

    const div =
        document.createElement("div");

    div.className = "history-item";

    div.innerHTML = `
      <b>${item.signal}</b>
      • ${item.market}<br>
      Confidence : ${item.confidence}%<br>
      Trend : ${item.trend}<br>
      ${item.time}
    `;

    box.appendChild(div);
  });

}

function updateTimer(){

  const m =
      String(Math.floor(timer/60)).padStart(2,"0");

  const s =
      String(timer%60).padStart(2,"0");

  document.getElementById("timer").innerText =
      m + ":" + s;

  document.getElementById("nextCandleTimer").innerText =
      (timer % 60) + " sec";
}

function setStatus(text){
  document.getElementById("status").innerText = text;
}

function setOverlay(text){
  document.getElementById("overlayStatus").innerText =
      "Overlay : " + text;
}

function setScanner(text){
  document.getElementById("engineStatus").innerText = text;
}

function random(list){
  return list[Math.floor(Math.random()*list.length)];
}

function randomNumber(min,max){
  return Math.floor(Math.random()*(max-min+1))+min;
}

function randomPrice(){
  return (1 + Math.random()).toFixed(5);
}

// Ready State
setStatus("READY");
setOverlay("DISCONNECTED");
setScanner("OFFLINE");
updateTimer();