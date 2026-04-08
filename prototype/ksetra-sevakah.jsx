import { useState, useEffect, useRef } from "react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, BarChart, Bar, PieChart, Pie, Cell, Legend, ComposedChart, ReferenceLine } from "recharts";

/* ─── Design Tokens ─── */
const T = {
  bg: "#0F1A12", bgCard: "#1A2B1E", bgCardHover: "#223826", bgSurface: "#142219",
  accent: "#4ADE80", accentDim: "#22623D", accentGlow: "rgba(74,222,128,0.15)",
  amber: "#F59E0B", amberDim: "#78500A",
  red: "#EF4444", redDim: "#7F1D1D",
  purple: "#A78BFA", purpleDim: "#4C1D95",
  blue: "#60A5FA", blueDim: "#1E3A5F",
  white: "#F0FFF4", whiteDim: "#A3C4AE",
  border: "#2D4A35", borderLight: "#3A5C44",
  font: `'DM Sans', sans-serif`, fontDisplay: `'Playfair Display', serif`, fontMono: `'JetBrains Mono', monospace`,
};

const fontLink = document.createElement("link");
fontLink.href = "https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;600;700&family=Playfair+Display:wght@600;700;800&family=JetBrains+Mono:wght@500;700&display=swap";
fontLink.rel = "stylesheet";
document.head.appendChild(fontLink);

/* ─── Mock Data ─── */
const PHASE_DATA = Array.from({ length: 24 }, (_, i) => ({
  hour: `${String(i).padStart(2, "0")}:00`,
  R: +(3.8 + Math.sin(i * 0.5) * 0.6 + Math.random() * 0.3).toFixed(2),
  Y: +(3.7 + Math.cos(i * 0.4) * 0.5 + Math.random() * 0.3).toFixed(2),
  B: +(3.9 + Math.sin(i * 0.3 + 1) * 0.4 + Math.random() * 0.3).toFixed(2),
}));

const UPTIME_DATA = [
  { day: "Mon", uptime: 14.2, downtime: 9.8 }, { day: "Tue", uptime: 18.1, downtime: 5.9 },
  { day: "Wed", uptime: 12.5, downtime: 11.5 }, { day: "Thu", uptime: 16.8, downtime: 7.2 },
  { day: "Fri", uptime: 19.3, downtime: 4.7 }, { day: "Sat", uptime: 15.0, downtime: 9.0 },
  { day: "Sun", uptime: 10.2, downtime: 13.8 },
];

const FAULT_DATA = [
  { name: "Dry Run", value: 12, color: "#EF4444" }, { name: "Overload", value: 8, color: "#F59E0B" },
  { name: "Phase Fail", value: 5, color: "#A78BFA" }, { name: "Low Voltage", value: 3, color: "#60A5FA" },
];

const WORKER_HISTORY = [
  { date: "Mar 26", onTime: "06:12", offTime: "18:45", duration: 12.55 },
  { date: "Mar 27", onTime: "06:08", offTime: "19:02", duration: 12.9 },
  { date: "Mar 28", onTime: "06:22", offTime: "18:38", duration: 12.27 },
  { date: "Mar 29", onTime: "06:15", offTime: "23:48", duration: 17.55, forgot: true },
  { date: "Mar 30", onTime: "06:05", offTime: "18:55", duration: 12.83 },
  { date: "Mar 31", onTime: "06:18", offTime: "18:42", duration: 12.4 },
  { date: "Apr 1", onTime: "06:10", offTime: "18:50", duration: 12.67 },
  { date: "Apr 2", onTime: "06:25", offTime: "19:10", duration: 12.75 },
  { date: "Apr 3", onTime: "06:08", offTime: "22:15", duration: 16.12, forgot: true },
  { date: "Apr 4", onTime: "06:20", offTime: "18:48", duration: 12.47 },
  { date: "Apr 5", onTime: "06:12", offTime: "18:55", duration: 12.72 },
  { date: "Apr 6", onTime: "06:30", offTime: "19:05", duration: 12.58 },
  { date: "Apr 7", onTime: "06:14", offTime: "18:40", duration: 12.43 },
  { date: "Apr 8", onTime: "06:18", offTime: null, duration: null, today: true },
];

const POWER_TREND = Array.from({ length: 48 }, (_, i) => {
  const hour = i * 0.5;
  const isEvening = hour >= 17 && hour <= 22;
  const isPredicted = i >= 34;
  const base = isEvening ? 210 - (hour - 17) * 4 : 228;
  return {
    time: `${String(Math.floor(hour) % 24).padStart(2, "0")}:${i % 2 === 0 ? "00" : "30"}`,
    voltage: isPredicted ? null : +(base + Math.random() * 6 - 3).toFixed(1),
    predicted: isPredicted ? +(base - (i - 34) * 2.5 + Math.random() * 3).toFixed(1) : null,
    danger: isPredicted && hour >= 19 ? +(190 + Math.random() * 5).toFixed(1) : null,
  };
});

const CHAT_THREADS = [
  { id: 1, title: "Morning pump analysis", date: "Apr 7", preview: "Phase currents were stable..." },
  { id: 2, title: "Power failure prediction", date: "Apr 6", preview: "Evening dip expected at 19:30..." },
  { id: 3, title: "Forgot-to-OFF investigation", date: "Apr 3", preview: "Motor ran 16h, worker forgot..." },
];

/* ─── Utility Components ─── */
const Glow = ({ color = T.accent, size = 200, top, left, right, bottom, opacity = 0.08 }) => (
  <div style={{ position: "absolute", top, left, right, bottom, width: size, height: size, background: `radial-gradient(circle, ${color} 0%, transparent 70%)`, opacity, pointerEvents: "none", filter: "blur(40px)" }} />
);

const Icon = ({ name, size = 20, color = T.whiteDim }) => {
  const p = { width: size, height: size, viewBox: "0 0 24 24", fill: "none", stroke: color, strokeWidth: "2" };
  const pf = { ...p, fill: color, stroke: "none" };
  const icons = {
    pump: <svg {...p}><circle cx="12" cy="12" r="3"/><path d="M12 1v4M12 19v4M4.22 4.22l2.83 2.83M16.95 16.95l2.83 2.83M1 12h4M19 12h4M4.22 19.78l2.83-2.83M16.95 7.05l2.83-2.83"/></svg>,
    chat: <svg {...p}><path d="M21 15a2 2 0 01-2 2H7l-4 4V5a2 2 0 012-2h14a2 2 0 012 2z"/></svg>,
    settings: <svg {...p}><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 00.33 1.82l.06.06a2 2 0 010 2.83 2 2 0 01-2.83 0l-.06-.06a1.65 1.65 0 00-1.82-.33 1.65 1.65 0 00-1 1.51V21a2 2 0 01-4 0v-.09A1.65 1.65 0 009 19.4a1.65 1.65 0 00-1.82.33l-.06.06a2 2 0 01-2.83-2.83l.06-.06A1.65 1.65 0 004.68 15a1.65 1.65 0 00-1.51-1H3a2 2 0 010-4h.09A1.65 1.65 0 004.6 9a1.65 1.65 0 00-.33-1.82l-.06-.06a2 2 0 012.83-2.83l.06.06A1.65 1.65 0 009 4.68a1.65 1.65 0 001-1.51V3a2 2 0 014 0v.09a1.65 1.65 0 001 1.51 1.65 1.65 0 001.82-.33l.06-.06a2 2 0 012.83 2.83l-.06.06A1.65 1.65 0 0019.4 9a1.65 1.65 0 001.51 1H21a2 2 0 010 4h-.09a1.65 1.65 0 00-1.51 1z"/></svg>,
    back: <svg {...{...p, strokeWidth: "2.5"}}><path d="M19 12H5M12 19l-7-7 7-7"/></svg>,
    send: <svg {...pf}><path d="M2.01 21L23 12 2.01 3 2 10l15 2-15 2z"/></svg>,
    mic: <svg {...p}><rect x="9" y="1" width="6" height="12" rx="3"/><path d="M19 10v2a7 7 0 01-14 0v-2M12 19v4M8 23h8"/></svg>,
    cloud: <svg {...p}><path d="M18 10h-1.26A8 8 0 109 20h9a5 5 0 000-10z"/></svg>,
    menu: <svg {...p}><path d="M3 12h18M3 6h18M3 18h18"/></svg>,
    play: <svg {...pf}><path d="M8 5v14l11-7z"/></svg>,
    stop: <svg {...pf}><rect x="6" y="6" width="12" height="12" rx="1"/></svg>,
    zap: <svg {...pf}><path d="M13 2L3 14h9l-1 8 10-12h-9l1-8z"/></svg>,
    leaf: <svg {...p}><path d="M17 8C8 10 5.9 16.17 3.82 21.34M17 8A9.78 9.78 0 0120 4.3C20 4.3 21 12 17 17s-13 3-13 3"/></svg>,
    grid: <svg {...p}><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>,
    activity: <svg {...p}><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>,
    brain: <svg {...p}><path d="M12 2a7 7 0 017 7c0 2.38-1.19 4.47-3 5.74V17a2 2 0 01-2 2h-4a2 2 0 01-2-2v-2.26C6.19 13.47 5 11.38 5 9a7 7 0 017-7z"/><path d="M9 21h6M10 17v4M14 17v4"/></svg>,
    user: <svg {...p}><path d="M20 21v-2a4 4 0 00-4-4H8a4 4 0 00-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>,
    alert: <svg {...p}><path d="M10.29 3.86L1.82 18a2 2 0 001.71 3h16.94a2 2 0 001.71-3L13.71 3.86a2 2 0 00-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>,
    eye: <svg {...p}><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>,
    shield: <svg {...p}><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/></svg>,
  };
  return icons[name] || null;
};

const RiskBadge = ({ level, small }) => {
  const c = { low: { bg: T.accent+"20", color: T.accent, border: T.accent+"44" }, medium: { bg: T.amber+"20", color: T.amber, border: T.amber+"44" }, high: { bg: T.red+"20", color: T.red, border: T.red+"44" }, critical: { bg: T.red+"30", color: "#FF6B6B", border: T.red+"66" } }[level] || { bg: T.accent+"20", color: T.accent, border: T.accent+"44" };
  return <span style={{ padding: small ? "2px 8px" : "4px 12px", borderRadius: 20, background: c.bg, color: c.color, border: `1px solid ${c.border}`, fontSize: small ? 9 : 11, fontWeight: 700, textTransform: "uppercase", letterSpacing: 1 }}>{level}</span>;
};

/* ════════════════════════════════════════════════════════════
   SCREEN 1: Hub
   ════════════════════════════════════════════════════════════ */
function HubScreen({ onNavigate }) {
  const modules = [
    { id: "pumpiq", name: "PumpIQ", desc: "Intelligent Pump Manager", icon: "pump", ready: true, accent: T.accent },
    { id: "cropdoc", name: "CropDoctor", desc: "Disease Detection AI", icon: "leaf", ready: false, accent: "#A78BFA" },
    { id: "soil", name: "SoilAnalytics", desc: "Soil Health Monitor", icon: "activity", ready: false, accent: "#60A5FA" },
  ];
  return (
    <div style={{ minHeight: "100vh", background: T.bg, fontFamily: T.font, color: T.white, position: "relative", overflow: "hidden" }}>
      <Glow color={T.accent} size={400} top={-100} right={-100} opacity={0.06} />
      <div style={{ padding: "48px 24px 16px", position: "relative", zIndex: 1 }}>
        <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
          <div style={{ width: 40, height: 40, borderRadius: 12, background: `linear-gradient(135deg, ${T.accent}, ${T.accentDim})`, display: "flex", alignItems: "center", justifyContent: "center" }}><Icon name="grid" size={20} color={T.bg} /></div>
          <div>
            <h1 style={{ fontFamily: T.fontDisplay, fontSize: 26, fontWeight: 700, margin: 0, background: `linear-gradient(135deg, ${T.white}, ${T.accent})`, WebkitBackgroundClip: "text", WebkitTextFillColor: "transparent" }}>Kṣetra Sevakaḥ</h1>
            <p style={{ fontSize: 12, color: T.whiteDim, margin: 0, letterSpacing: 1.5, textTransform: "uppercase" }}>Field Servant • AI Platform</p>
          </div>
        </div>
      </div>
      <div style={{ padding: "16px 24px" }}>
        <p style={{ fontSize: 11, color: T.whiteDim, textTransform: "uppercase", letterSpacing: 2, marginBottom: 16, fontWeight: 600 }}>Modules</p>
        <div style={{ display: "flex", flexDirection: "column", gap: 14 }}>
          {modules.map((m) => (
            <div key={m.id} onClick={() => m.ready && onNavigate("dashboard")} style={{ background: m.ready ? T.bgCard : T.bgSurface, border: `1px solid ${m.ready ? T.border : "transparent"}`, borderRadius: 16, padding: 20, cursor: m.ready ? "pointer" : "default", opacity: m.ready ? 1 : 0.45, position: "relative", overflow: "hidden" }}>
              {m.ready && <Glow color={m.accent} size={120} top={-30} right={-30} opacity={0.12} />}
              <div style={{ display: "flex", alignItems: "center", gap: 16, position: "relative", zIndex: 1 }}>
                <div style={{ width: 52, height: 52, borderRadius: 14, background: m.ready ? `${m.accent}12` : "rgba(255,255,255,0.03)", border: `1px solid ${m.ready ? m.accent + "33" : "rgba(255,255,255,0.06)"}`, display: "flex", alignItems: "center", justifyContent: "center" }}><Icon name={m.icon} size={24} color={m.ready ? m.accent : T.whiteDim} /></div>
                <div style={{ flex: 1 }}>
                  <h3 style={{ margin: 0, fontSize: 18, fontWeight: 700, color: m.ready ? T.white : T.whiteDim }}>{m.name}</h3>
                  <p style={{ margin: "2px 0 0", fontSize: 13, color: T.whiteDim }}>{m.desc}</p>
                </div>
                <div style={{ padding: "5px 12px", borderRadius: 20, background: m.ready ? m.accent+"18" : "rgba(255,255,255,0.04)", color: m.ready ? m.accent : T.whiteDim, fontSize: 11, fontWeight: 700, textTransform: "uppercase", letterSpacing: 1 }}>{m.ready ? "Active" : "Soon"}</div>
              </div>
            </div>
          ))}
        </div>
      </div>
      <div style={{ padding: "24px 24px" }}>
        <p style={{ fontSize: 11, color: T.whiteDim, textTransform: "uppercase", letterSpacing: 2, marginBottom: 16, fontWeight: 600 }}>System</p>
        <div style={{ display: "flex", gap: 12 }}>
          {[{ icon: "cloud", label: "Backup", sub: "Last: 2h ago", c: T.accent }, { icon: "settings", label: "Settings", sub: "Panel & AI", c: T.whiteDim }].map((s) => (
            <div key={s.label} style={{ flex: 1, background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 14, padding: 16, display: "flex", flexDirection: "column", alignItems: "center", gap: 8, cursor: "pointer" }}>
              <Icon name={s.icon} size={22} color={s.c} /><span style={{ fontSize: 12, fontWeight: 600 }}>{s.label}</span><span style={{ fontSize: 10, color: T.whiteDim, textAlign: "center" }}>{s.sub}</span>
            </div>
          ))}
        </div>
      </div>
      <div style={{ padding: "12px 24px 40px", textAlign: "center" }}><p style={{ fontSize: 10, color: T.whiteDim+"88", letterSpacing: 1 }}>XIAOMI 14 CIVI • SNAPDRAGON 8s GEN 3 • MLC-LLM</p></div>
    </div>
  );
}

/* ════════════════════════════════════════════════════════════
   SCREEN 2: PumpIQ Dashboard + AI Predictions
   ════════════════════════════════════════════════════════════ */
function DashboardScreen({ onNavigate }) {
  const [motorState, setMotorState] = useState("OFF");
  const [elapsed, setElapsed] = useState(0);
  const [activeChart, setActiveChart] = useState("phase");
  const timerRef = useRef(null);

  useEffect(() => {
    if (motorState === "ON") { timerRef.current = setInterval(() => setElapsed(e => e + 1), 1000); }
    else { clearInterval(timerRef.current); }
    return () => clearInterval(timerRef.current);
  }, [motorState]);

  const handleMotorAction = () => {
    if (motorState === "OFF") { setMotorState("PENDING_START"); setTimeout(() => { setMotorState("ON"); setElapsed(0); }, 2800); }
    else if (motorState === "ON") { setMotorState("PENDING_STOP"); setTimeout(() => { setMotorState("OFF"); setElapsed(0); }, 2200); }
  };

  const isPending = motorState === "PENDING_START" || motorState === "PENDING_STOP";
  const isOn = motorState === "ON";
  const mc = { OFF: { bg: T.accentDim, border: T.accent, text: T.white, label: "START PUMP", icon: "play" }, PENDING_START: { bg: T.amberDim, border: T.amber, text: T.amber, label: "SENDING SMS...", icon: "zap" }, ON: { bg: T.redDim, border: T.red, text: T.white, label: "STOP PUMP", icon: "stop" }, PENDING_STOP: { bg: T.amberDim, border: T.amber, text: T.amber, label: "SENDING SMS...", icon: "zap" } }[motorState];
  const fmt = (s) => `${String(Math.floor(s/3600)).padStart(2,"0")}:${String(Math.floor((s%3600)/60)).padStart(2,"0")}:${String(s%60).padStart(2,"0")}`;

  return (
    <div style={{ minHeight: "100vh", background: T.bg, fontFamily: T.font, color: T.white, position: "relative", overflow: "hidden", paddingBottom: 24 }}>
      <Glow color={isOn ? T.accent : T.red} size={300} top={-80} left="30%" opacity={isOn ? 0.08 : 0.04} />

      {/* Header */}
      <div style={{ padding: "48px 20px 12px", display: "flex", alignItems: "center", gap: 12 }}>
        <div onClick={() => onNavigate("hub")} style={{ cursor: "pointer", padding: 4 }}><Icon name="back" size={22} color={T.whiteDim} /></div>
        <div style={{ flex: 1 }}>
          <h2 style={{ margin: 0, fontSize: 20, fontWeight: 700, fontFamily: T.fontDisplay }}>PumpIQ</h2>
          <p style={{ margin: 0, fontSize: 11, color: T.whiteDim, letterSpacing: 1 }}>TARO SMART PANEL</p>
        </div>
        <div onClick={() => onNavigate("chat")} style={{ width: 40, height: 40, borderRadius: 12, cursor: "pointer", background: T.bgCard, border: `1px solid ${T.border}`, display: "flex", alignItems: "center", justifyContent: "center" }}><Icon name="chat" size={18} color={T.accent} /></div>
      </div>

      {/* Motor Control Card */}
      <div style={{ padding: "12px 20px" }}>
        <div style={{ background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 20, padding: "24px 20px", position: "relative", overflow: "hidden" }}>
          {isOn && <Glow color={T.accent} size={200} top={-60} right={-40} opacity={0.15} />}
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", marginBottom: 20, position: "relative", zIndex: 1 }}>
            <div>
              <div style={{ display: "flex", alignItems: "center", gap: 8, marginBottom: 6 }}>
                <div style={{ width: 10, height: 10, borderRadius: "50%", background: isOn ? T.accent : isPending ? T.amber : T.red, boxShadow: isOn ? `0 0 12px ${T.accent}` : isPending ? `0 0 12px ${T.amber}` : "none", animation: isPending ? "pulse 1.2s ease-in-out infinite" : "none" }} />
                <span style={{ fontSize: 13, fontWeight: 700, textTransform: "uppercase", letterSpacing: 1.5, color: isOn ? T.accent : isPending ? T.amber : T.whiteDim }}>{isOn ? "Running" : isPending ? "Pending" : "Offline"}</span>
              </div>
              <p style={{ margin: 0, fontSize: 11, color: T.whiteDim }}>SMS: 070936 52065</p>
            </div>
            {isOn && <div style={{ textAlign: "right" }}><p style={{ margin: 0, fontSize: 28, fontWeight: 700, fontFamily: T.fontMono, color: T.accent, letterSpacing: 1 }}>{fmt(elapsed)}</p><p style={{ margin: 0, fontSize: 10, color: T.whiteDim, letterSpacing: 1 }}>SESSION TIME</p></div>}
          </div>
          {isOn && (
            <div style={{ display: "flex", gap: 10, marginBottom: 20 }}>
              {[{ l: "R", v: "3.82A", c: "#EF4444" }, { l: "Y", v: "3.71A", c: "#F59E0B" }, { l: "B", v: "3.89A", c: "#60A5FA" }].map(p => (
                <div key={p.l} style={{ flex: 1, background: "rgba(255,255,255,0.03)", borderRadius: 10, padding: "10px 12px", border: `1px solid ${p.c}22` }}>
                  <p style={{ margin: 0, fontSize: 10, color: T.whiteDim }}>{p.l} Phase</p>
                  <p style={{ margin: "2px 0 0", fontSize: 18, fontWeight: 700, color: p.c, fontFamily: T.fontMono }}>{p.v}</p>
                </div>
              ))}
            </div>
          )}
          <button onClick={handleMotorAction} disabled={isPending} style={{ width: "100%", padding: 16, borderRadius: 14, border: `2px solid ${mc.border}`, background: `linear-gradient(135deg, ${mc.bg}, ${mc.bg}dd)`, color: mc.text, fontSize: 16, fontWeight: 800, fontFamily: T.font, letterSpacing: 2, textTransform: "uppercase", cursor: isPending ? "wait" : "pointer", display: "flex", alignItems: "center", justifyContent: "center", gap: 10, opacity: isPending ? 0.7 : 1, transition: "all 0.3s", boxShadow: `0 0 20px ${mc.border}22` }}>
            <Icon name={mc.icon} size={20} color={mc.text} />{mc.label}
          </button>
          {isPending && <p style={{ margin: "10px 0 0", fontSize: 11, color: T.amber, textAlign: "center", fontStyle: "italic" }}>Waiting for SMS confirmation from Taro Panel...</p>}
        </div>
      </div>

      {/* ═══ AI PREDICTION CARDS (2×2 Grid) ═══ */}
      <div style={{ padding: "8px 20px" }}>
        <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", marginBottom: 12 }}>
          <div style={{ display: "flex", alignItems: "center", gap: 6 }}><Icon name="brain" size={14} color={T.purple} /><span style={{ fontSize: 11, fontWeight: 700, color: T.purple, textTransform: "uppercase", letterSpacing: 1.5 }}>AI Predictions</span></div>
          <span style={{ fontSize: 10, color: T.whiteDim }}>14-day model</span>
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 10 }}>
          {[
            { icon: "zap", iconColor: T.red, label: "Power Fail", value: "~19:30", sub: "Evening voltage dip", risk: "high", grad: T.redDim, border: T.red, query: "Predict power failure" },
            { icon: "alert", iconColor: T.amber, label: "Next Fault", value: "+8h", sub: "Dry run risk rising", risk: "medium", grad: T.amberDim, border: T.amber, query: "Predict next fault" },
            { icon: "user", iconColor: T.accent, label: "Tomorrow ON", value: "06:15", sub: "Worker avg start", risk: "low", grad: T.accentDim, border: T.accent, query: "When will worker turn ON tomorrow" },
            { icon: "eye", iconColor: T.purple, label: "Forgot OFF?", value: "14%", sub: "Today's risk score", risk: "low", grad: T.purpleDim, border: T.purple, query: "Forgot to turn OFF risk" },
          ].map((card) => (
            <div key={card.label} onClick={() => onNavigate("chat", card.query)} style={{ background: `linear-gradient(135deg, ${card.grad}44, ${T.bgCard})`, border: `1px solid ${card.border}22`, borderRadius: 14, padding: 14, cursor: "pointer", position: "relative", overflow: "hidden" }}>
              <Glow color={card.border} size={80} top={-20} right={-20} opacity={0.15} />
              <div style={{ display: "flex", alignItems: "center", gap: 5, marginBottom: 8 }}><Icon name={card.icon} size={12} color={card.iconColor} /><span style={{ fontSize: 9, fontWeight: 700, color: card.iconColor, textTransform: "uppercase", letterSpacing: 1 }}>{card.label}</span></div>
              <p style={{ margin: 0, fontSize: 22, fontWeight: 800, color: T.white, fontFamily: T.fontMono }}>{card.value}</p>
              <p style={{ margin: "4px 0 0", fontSize: 10, color: T.whiteDim }}>{card.sub}</p>
              <div style={{ marginTop: 8 }}><RiskBadge level={card.risk} small /></div>
            </div>
          ))}
        </div>
      </div>

      {/* AI Summary */}
      <div style={{ padding: "12px 20px" }}>
        <div style={{ background: `linear-gradient(135deg, ${T.accentDim}44, ${T.bgCard})`, border: `1px solid ${T.accent}22`, borderRadius: 14, padding: "14px 16px" }}>
          <div style={{ display: "flex", alignItems: "center", gap: 6, marginBottom: 6 }}><Icon name="brain" size={12} color={T.accent} /><span style={{ fontSize: 10, fontWeight: 700, color: T.accent, textTransform: "uppercase", letterSpacing: 1.5 }}>AI Daily Summary</span></div>
          <p style={{ margin: 0, fontSize: 13, color: T.whiteDim, lineHeight: 1.5 }}>{isOn ? "Motor running. Phases balanced at 3.8A. AI predicts voltage dip ~19:30 — stop before then. Worker usually shuts off ~18:45." : "Motor offline. Last: 4h 22m, avg 3.7A. Worker forgot OFF on Mar 29 & Apr 3 — watchdog active. Tomorrow predicted start: 06:15 AM."}</p>
        </div>
      </div>

      {/* Chart Tabs */}
      <div style={{ padding: "8px 20px 4px" }}>
        <div style={{ display: "flex", gap: 6, overflowX: "auto", scrollbarWidth: "none" }}>
          {[{ id: "phase", label: "Phase Currents" }, { id: "uptime", label: "Grid" }, { id: "faults", label: "Faults" }, { id: "worker", label: "Worker Log" }, { id: "power", label: "Power AI" }].map(tab => (
            <button key={tab.id} onClick={() => setActiveChart(tab.id)} style={{ padding: "7px 14px", borderRadius: 20, border: "none", flexShrink: 0, background: activeChart === tab.id ? T.accent+"22" : "transparent", color: activeChart === tab.id ? T.accent : T.whiteDim, fontSize: 12, fontWeight: 600, fontFamily: T.font, cursor: "pointer" }}>{tab.label}</button>
          ))}
        </div>
      </div>

      {/* Charts */}
      <div style={{ padding: "4px 12px 12px" }}>
        <div style={{ background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 16, padding: "16px 8px 8px 0" }}>
          {activeChart === "phase" && (
            <ResponsiveContainer width="100%" height={200}>
              <LineChart data={PHASE_DATA}><CartesianGrid strokeDasharray="3 3" stroke={T.border} /><XAxis dataKey="hour" tick={{ fill: T.whiteDim, fontSize: 10 }} interval={5} /><YAxis tick={{ fill: T.whiteDim, fontSize: 10 }} domain={[2.5, 5]} /><Tooltip contentStyle={{ background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 8, fontSize: 12, color: T.white }} /><Line type="monotone" dataKey="R" stroke="#EF4444" strokeWidth={2} dot={false} /><Line type="monotone" dataKey="Y" stroke="#F59E0B" strokeWidth={2} dot={false} /><Line type="monotone" dataKey="B" stroke="#60A5FA" strokeWidth={2} dot={false} /></LineChart>
            </ResponsiveContainer>
          )}
          {activeChart === "uptime" && (
            <ResponsiveContainer width="100%" height={200}>
              <BarChart data={UPTIME_DATA}><CartesianGrid strokeDasharray="3 3" stroke={T.border} /><XAxis dataKey="day" tick={{ fill: T.whiteDim, fontSize: 11 }} /><YAxis tick={{ fill: T.whiteDim, fontSize: 10 }} /><Tooltip contentStyle={{ background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 8, fontSize: 12, color: T.white }} /><Bar dataKey="uptime" stackId="a" fill={T.accent} name="Uptime" /><Bar dataKey="downtime" stackId="a" fill={T.red+"88"} radius={[4,4,0,0]} name="Down" /></BarChart>
            </ResponsiveContainer>
          )}
          {activeChart === "faults" && (
            <ResponsiveContainer width="100%" height={200}>
              <PieChart><Pie data={FAULT_DATA} cx="50%" cy="50%" innerRadius={50} outerRadius={80} paddingAngle={3} dataKey="value">{FAULT_DATA.map((e,i) => <Cell key={i} fill={e.color} />)}</Pie><Legend wrapperStyle={{ fontSize: 11, color: T.whiteDim }} formatter={v => <span style={{ color: T.whiteDim }}>{v}</span>} /><Tooltip contentStyle={{ background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 8, fontSize: 12, color: T.white }} /></PieChart>
            </ResponsiveContainer>
          )}
          {activeChart === "worker" && (
            <div style={{ padding: "0 12px" }}>
              <p style={{ fontSize: 11, color: T.whiteDim, margin: "0 0 8px 8px" }}>Daily ON/OFF — last 14 days</p>
              <div style={{ maxHeight: 180, overflowY: "auto", scrollbarWidth: "none" }}>
                {WORKER_HISTORY.map((w, i) => (
                  <div key={i} style={{ display: "flex", alignItems: "center", gap: 10, padding: "7px 8px", borderBottom: `1px solid ${T.border}22`, background: w.forgot ? T.red+"08" : "transparent", borderRadius: 6 }}>
                    <span style={{ fontSize: 11, color: T.whiteDim, width: 48, flexShrink: 0 }}>{w.date}</span>
                    <span style={{ fontSize: 12, fontWeight: 600, color: T.accent, fontFamily: T.fontMono, width: 42 }}>{w.onTime}</span>
                    <div style={{ flex: 1, height: 4, borderRadius: 2, background: T.border, position: "relative", overflow: "hidden" }}>
                      <div style={{ position: "absolute", left: 0, top: 0, height: "100%", borderRadius: 2, width: `${Math.min((w.duration||12)/24*100, 100)}%`, background: w.forgot ? `linear-gradient(90deg, ${T.accent}, ${T.red})` : T.accent }} />
                    </div>
                    <span style={{ fontSize: 12, fontWeight: 600, color: w.forgot ? T.red : T.whiteDim, fontFamily: T.fontMono, width: 42 }}>{w.offTime || "—"}</span>
                    {w.forgot && <span style={{ fontSize: 8, color: T.red, fontWeight: 800, letterSpacing: 1 }}>FORGOT</span>}
                    {w.today && <span style={{ fontSize: 8, color: T.amber, fontWeight: 800, letterSpacing: 1 }}>TODAY</span>}
                  </div>
                ))}
              </div>
            </div>
          )}
          {activeChart === "power" && (
            <div>
              <p style={{ fontSize: 11, color: T.whiteDim, margin: "0 0 4px 20px" }}>Voltage + AI forecast (dashed = predicted)</p>
              <ResponsiveContainer width="100%" height={180}>
                <ComposedChart data={POWER_TREND}><CartesianGrid strokeDasharray="3 3" stroke={T.border} /><XAxis dataKey="time" tick={{ fill: T.whiteDim, fontSize: 9 }} interval={7} /><YAxis tick={{ fill: T.whiteDim, fontSize: 10 }} domain={[180, 245]} /><Tooltip contentStyle={{ background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 8, fontSize: 12, color: T.white }} /><ReferenceLine y={200} stroke={T.red} strokeDasharray="5 5" label={{ value: "DANGER 200V", fill: T.red, fontSize: 9 }} /><Line type="monotone" dataKey="voltage" stroke={T.accent} strokeWidth={2} dot={false} name="Actual V" /><Line type="monotone" dataKey="predicted" stroke={T.amber} strokeWidth={2} strokeDasharray="6 3" dot={false} name="Predicted" /><Line type="monotone" dataKey="danger" stroke={T.red} strokeWidth={2.5} strokeDasharray="3 3" dot={false} name="Danger" /></ComposedChart>
              </ResponsiveContainer>
            </div>
          )}
        </div>
      </div>

      <style>{`@keyframes pulse { 0%,100% { opacity:1; } 50% { opacity:0.3; } }`}</style>
    </div>
  );
}

/* ════════════════════════════════════════════════════════════
   SCREEN 3: AI Chat with Prediction One-Click Queries
   ════════════════════════════════════════════════════════════ */
function ChatScreen({ onNavigate, initialQuery }) {
  const [messages, setMessages] = useState([{ role: "ai", text: "Good morning! PumpIQ prediction engine is active. I've analyzed 14 days of telemetry, worker behavior, and grid patterns. Tap any quick query below or ask me anything." }]);
  const [input, setInput] = useState("");
  const [typing, setTyping] = useState(false);
  const [showThreads, setShowThreads] = useState(false);
  const chatEndRef = useRef(null);
  const handled = useRef(false);

  useEffect(() => { chatEndRef.current?.scrollIntoView({ behavior: "smooth" }); }, [messages, typing]);
  useEffect(() => { if (initialQuery && !handled.current) { handled.current = true; setTimeout(() => handleSend(initialQuery), 400); } }, [initialQuery]);

  const quickChips = [
    { label: "⚡ Power Failure?", query: "Predict power failure", pred: true },
    { label: "🔧 Next Fault?", query: "Predict next fault", pred: true },
    { label: "👤 Worker ON Tomorrow?", query: "When will worker turn ON tomorrow", pred: true },
    { label: "⚠️ Forgot OFF Risk?", query: "Forgot to turn OFF risk", pred: true },
    { label: "🛡️ Motor Health", query: "Motor health score", pred: true },
    { label: "📊 24h Summary", query: "Summarize last 24h" },
    { label: "📈 Fault History", query: "Show fault history" },
    { label: "🔋 Grid Stats", query: "Grid uptime stats" },
    { label: "📡 Status SMS", query: "Request status SMS" },
    { label: "🕐 Worker Log", query: "Show worker ON/OFF history" },
  ];

  const R = {
    "Predict power failure": `⚡ POWER FAILURE PREDICTION\n\nBased on 14 days of voltage data:\n\n📉 Predicted Dip: ~19:30 tonight\n• Voltage expected: 228V → ~195V\n• Below 200V = motor trips\n\n🔍 Pattern:\n• 11/14 evenings: dips below 210V\n• 5/14 evenings: below 200V (trips)\n• Worst: Sunday (avg 192V)\n• Safe window: 06:00–16:00\n\n🎯 Recommendation:\nStop motor by 19:00. Set reminder for 18:45.\n\nConfidence: 78%`,
    "Predict next fault": `🔧 FAULT PREDICTION\n\n⏱️ Next Likely Fault: ~8h from now\n• Type: DRY RUN (most probable)\n• Risk: 67% at +8h → 85% at +12h\n\n📊 Risk Timeline:\n• Now: 5% (Low)\n• +4h: 28% (Medium)\n• +8h: 67% (High) ⚠️\n• +12h: 85% (Critical) 🚨\n\n🔍 Why? 12 dry-runs in 14 days, all 13:00–16:00. Afternoon heat + water table drop.\n\n🎯 Action: Schedule pump rest at 13:00 & 15:00. Watch B-phase > 4.5A = imminent.`,
    "When will worker turn ON tomorrow": `👤 WORKER PREDICTION\n\n📅 Tomorrow's Start: 06:15 AM\n• Confidence: 92%\n\n📊 14-Day Pattern:\n• Average: 06:15 AM\n• Earliest: 06:05 (Mar 30)\n• Latest: 06:30 (Apr 6)\n• Deviation: ±8 min\n\n📆 By Day:\n• Mon–Fri: 06:12 (punctual)\n• Sat: 06:25 (slight late)\n• Sun: 06:30 (latest)\n\n🔮 Tomorrow (Wed): 06:14 ± 8 min\n\n💡 Set pre-heat alert at 06:00.`,
    "Forgot to turn OFF risk": `⚠️ FORGOT-TO-OFF WATCHDOG\n\n📊 Today's Risk: 14% (Low)\n• Normal shutdown: 18:38–19:10\n\n🚨 Past Incidents:\n• Mar 29: OFF at 23:48 (forgot 5h!) → dry-run at 21:15\n• Apr 3: OFF at 22:15 (forgot 3h!) → overload at 20:40\n\n📈 Risk Factors:\n• Weekend: +25%\n• Rain day: +15%\n• Friday evening: +20%\n• Today (Wed): baseline\n\n🛡️ WATCHDOG Active:\n• 19:30 → Push notification\n• 20:00 → "AUTO-STOP?" prompt\n• 20:30 → Auto STATUS SMS sent`,
    "Motor health score": `🛡️ MOTOR HEALTH: 87/100\n\n• Phase Balance: 95/100 (4.6% imbalance)\n• Current Stability: 88/100\n• Runtime Pattern: 82/100 (2 forgot-OFF events)\n• Fault Frequency: 78/100 (dry runs ↑)\n• Temperature: 91/100 (42°C, normal)\n\nTrend: -3 pts this week (dry runs). Add afternoon rest cycles.\n\nNext service: ~45 days`,
    "Summarize last 24h": `📊 LAST 24H\n\n🔋 Motor: 14.2h total, 2 sessions\n• S1: 06:14–12:30 (6h 16m)\n• S2: 14:15–22:09 (7h 54m)\n• Avg: R=3.82A, Y=3.71A, B=3.89A ✓\n\n⚡ Grid: 82% uptime, 3 outages\n🔧 Alerts: 1 dry-run at 14:32 (auto-recovered)\n👤 Worker: ON 06:14, OFF 18:40 (normal)\n\n🔮 Tomorrow: ON ~06:15, power risk ~19:30, fault risk medium`,
    "Show fault history": `🔧 14-DAY FAULTS\n\n• 12× Dry Run (43%) — 13:00–16:00\n• 8× Overload (29%) — brief spikes\n• 5× Phase Fail (18%) — grid-side\n• 3× Low Voltage (11%) — evening peak\n\nTrend: Dry runs +3 vs last week.\nCluster days: Wed & Thu.\nRoot cause: afternoon water table drop.`,
    "Grid uptime stats": `🔋 GRID RELIABILITY (7-Day)\n\n• Best: Fri 80.4% (19.3h)\n• Worst: Sun 42.5% (10.2h)\n• Avg: 63.2%\n\nPredicted tomorrow: ~72%\nStable: 06:00–14:00\nOutages: 17:00–22:00`,
    "Request status SMS": `📡 SMS sent to 070936 52065...\n\n✅ Reply (3.2s):\n• Motor: OFF\n• Last shutdown: 2h 14m ago\n• Voltage: 228V\n• Phases: Normal\n• Faults: None\n• Temp: 42°C`,
    "Show worker ON/OFF history": `👤 WORKER LOG (14 Days)\n\nMar 26: 06:12→18:45 (12h33m) ✓\nMar 27: 06:08→19:02 (12h54m) ✓\nMar 28: 06:22→18:38 (12h16m) ✓\nMar 29: 06:15→23:48 (17h33m) ⚠️ FORGOT\nMar 30: 06:05→18:55 (12h50m) ✓\nMar 31: 06:18→18:42 (12h24m) ✓\nApr 01: 06:10→18:50 (12h40m) ✓\nApr 02: 06:25→19:10 (12h45m) ✓\nApr 03: 06:08→22:15 (16h07m) ⚠️ FORGOT\nApr 04: 06:20→18:48 (12h28m) ✓\nApr 05: 06:12→18:55 (12h43m) ✓\nApr 06: 06:30→19:05 (12h35m) ✓\nApr 07: 06:14→18:40 (12h26m) ✓\nApr 08: 06:18→running... 🔄\n\nForgot rate: 2/14 (14.3%)`,
  };

  const handleSend = (text) => {
    const msg = text || input.trim();
    if (!msg) return;
    setMessages(p => [...p, { role: "user", text: msg }]);
    setInput("");
    setTyping(true);
    const key = Object.keys(R).find(k => k.toLowerCase() === msg.toLowerCase());
    const resp = key ? R[key] : `🔍 Analyzing "${msg}"...\n\nBased on 14-day telemetry, phase trends, grid voltage, and worker history:\n\nThe motor's data shows stable operation with 2 forgot-OFF anomalies (Mar 29, Apr 3). Phase currents balanced ~3.8A. Predictions improve with more data.\n\nTap a quick query above for detailed analysis.`;
    setTimeout(() => { setTyping(false); setMessages(p => [...p, { role: "ai", text: resp }]); }, 1800);
  };

  return (
    <div style={{ height: "100vh", display: "flex", flexDirection: "column", background: T.bg, fontFamily: T.font, color: T.white }}>
      {/* Header */}
      <div style={{ padding: "48px 20px 12px", display: "flex", alignItems: "center", gap: 12, borderBottom: `1px solid ${T.border}`, flexShrink: 0 }}>
        <div onClick={() => onNavigate("dashboard")} style={{ cursor: "pointer", padding: 4 }}><Icon name="back" size={22} color={T.whiteDim} /></div>
        <div style={{ flex: 1 }}>
          <h2 style={{ margin: 0, fontSize: 18, fontWeight: 700 }}>AI Analyst</h2>
          <div style={{ display: "flex", alignItems: "center", gap: 6 }}><div style={{ width: 6, height: 6, borderRadius: "50%", background: T.purple, boxShadow: `0 0 6px ${T.purple}` }} /><p style={{ margin: 0, fontSize: 11, color: T.purple }}>3B Orchestrator • Prediction Engine</p></div>
        </div>
        <div onClick={() => setShowThreads(!showThreads)} style={{ cursor: "pointer", padding: 4 }}><Icon name="menu" size={20} color={T.whiteDim} /></div>
      </div>

      {showThreads && (
        <div style={{ position: "absolute", top: 90, right: 16, zIndex: 10, background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 14, padding: "12px 0", width: 240, boxShadow: "0 8px 32px rgba(0,0,0,0.5)" }}>
          <p style={{ padding: "4px 16px 8px", margin: 0, fontSize: 11, color: T.whiteDim, textTransform: "uppercase", letterSpacing: 1.5, fontWeight: 700 }}>Chat Threads</p>
          {CHAT_THREADS.map(t => (
            <div key={t.id} style={{ padding: "10px 16px", cursor: "pointer", borderBottom: `1px solid ${T.border}22` }}>
              <div style={{ display: "flex", justifyContent: "space-between" }}><span style={{ fontSize: 13, fontWeight: 600 }}>{t.title}</span><span style={{ fontSize: 10, color: T.whiteDim }}>{t.date}</span></div>
              <p style={{ margin: "2px 0 0", fontSize: 11, color: T.whiteDim }}>{t.preview}</p>
            </div>
          ))}
        </div>
      )}

      {/* Chips */}
      <div style={{ flexShrink: 0, padding: "12px 0 4px" }}>
        <div style={{ display: "flex", gap: 8, flexWrap: "wrap", padding: "0 20px", maxHeight: 76, overflowY: "auto", scrollbarWidth: "none" }}>
          {quickChips.map(chip => (
            <button key={chip.query} onClick={() => handleSend(chip.query)} style={{
              padding: "7px 14px", borderRadius: 20, flexShrink: 0,
              border: `1px solid ${chip.pred ? T.purple+"44" : T.accent+"33"}`,
              background: chip.pred ? T.purpleDim+"33" : T.accentDim+"33",
              color: chip.pred ? T.purple : T.accent,
              fontSize: 12, fontWeight: 600, fontFamily: T.font, cursor: "pointer", whiteSpace: "nowrap",
            }}>{chip.label}</button>
          ))}
        </div>
      </div>

      {/* Messages */}
      <div style={{ flex: 1, overflowY: "auto", padding: "12px 20px", scrollbarWidth: "none" }}>
        {messages.map((msg, i) => (
          <div key={i} style={{ display: "flex", justifyContent: msg.role === "user" ? "flex-end" : "flex-start", marginBottom: 12 }}>
            <div style={{ maxWidth: "88%", padding: "12px 16px", borderRadius: 16, background: msg.role === "user" ? `linear-gradient(135deg, ${T.accentDim}, ${T.accent}33)` : T.bgCard, border: `1px solid ${msg.role === "user" ? T.accent+"44" : T.border}` }}>
              {msg.role === "ai" && <div style={{ display: "flex", alignItems: "center", gap: 4, marginBottom: 6 }}><Icon name="brain" size={10} color={T.purple} /><span style={{ fontSize: 10, color: T.purple, fontWeight: 700, letterSpacing: 1 }}>PUMPIQ AI</span></div>}
              <p style={{ margin: 0, fontSize: 13, lineHeight: 1.65, whiteSpace: "pre-wrap", color: msg.role === "user" ? T.white : T.whiteDim }}>{msg.text}</p>
            </div>
          </div>
        ))}
        {typing && (
          <div style={{ display: "flex", marginBottom: 12 }}>
            <div style={{ padding: "14px 18px", borderRadius: 16, background: T.bgCard, border: `1px solid ${T.border}` }}>
              <div style={{ display: "flex", alignItems: "center", gap: 6 }}><Icon name="brain" size={10} color={T.purple} /><span style={{ fontSize: 10, color: T.purple, fontWeight: 700, letterSpacing: 1 }}>ANALYZING</span>
                <div style={{ display: "flex", gap: 3, marginLeft: 4 }}>{[0,1,2].map(d => <div key={d} style={{ width: 6, height: 6, borderRadius: "50%", background: T.purple, animation: `dp 1.4s ease-in-out ${d*0.2}s infinite` }} />)}</div>
              </div>
            </div>
          </div>
        )}
        <div ref={chatEndRef} />
      </div>

      {/* Input */}
      <div style={{ flexShrink: 0, padding: "12px 20px 32px", borderTop: `1px solid ${T.border}`, background: T.bgSurface }}>
        <div style={{ display: "flex", alignItems: "center", gap: 10, background: T.bgCard, border: `1px solid ${T.border}`, borderRadius: 16, padding: "6px 8px 6px 16px" }}>
          <input value={input} onChange={e => setInput(e.target.value)} onKeyDown={e => e.key === "Enter" && handleSend()} placeholder="Ask about predictions, faults, worker..." style={{ flex: 1, background: "none", border: "none", outline: "none", color: T.white, fontSize: 14, fontFamily: T.font }} />
          <button style={{ width: 36, height: 36, borderRadius: 12, background: "rgba(255,255,255,0.04)", border: "none", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer" }}><Icon name="mic" size={18} color={T.whiteDim} /></button>
          <button onClick={() => handleSend()} style={{ width: 36, height: 36, borderRadius: 12, background: T.accent, border: "none", display: "flex", alignItems: "center", justifyContent: "center", cursor: "pointer" }}><Icon name="send" size={16} color={T.bg} /></button>
        </div>
      </div>

      <style>{`@keyframes dp { 0%,80%,100% { transform:scale(0.6); opacity:0.3; } 40% { transform:scale(1); opacity:1; } }`}</style>
    </div>
  );
}

/* ─── App Shell ─── */
export default function App() {
  const [screen, setScreen] = useState("hub");
  const [chatQ, setChatQ] = useState(null);
  const nav = (target, query) => { setChatQ(target === "chat" && query ? query : null); setScreen(target); };
  return (
    <div style={{ maxWidth: 430, margin: "0 auto", background: T.bg, minHeight: "100vh" }}>
      {screen === "hub" && <HubScreen onNavigate={nav} />}
      {screen === "dashboard" && <DashboardScreen onNavigate={nav} />}
      {screen === "chat" && <ChatScreen onNavigate={nav} initialQuery={chatQ} />}
    </div>
  );
}
