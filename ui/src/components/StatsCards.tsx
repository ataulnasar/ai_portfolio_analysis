import type { Snapshot } from "../types/Snapshot";
import { formatPct } from "../utils/format";

function Card({ label, value }: { label: string; value: string }) {
  return (
    <div
      style={{
        border: "1px solid #e6e6e6",
        borderRadius: 10,
        padding: 12,
        minWidth: 180,
        background: "white",
      }}
    >
      <div style={{ fontSize: 12, color: "#556" }}>{label}</div>
      <div style={{ fontSize: 20, fontWeight: 700, marginTop: 6 }}>{value}</div>
    </div>
  );
}

export default function StatsCards({ snapshot }: { snapshot: Snapshot }) {
  const s = snapshot.stats;

  return (
    <div style={{ display: "flex", gap: 12, flexWrap: "wrap" }}>
      <Card label="# Holdings" value={String(s.numberOfHoldings)} />
      <Card label="Top holding weight" value={formatPct(s.topHoldingWeightPercent)} />
      <Card label="Top 3 concentration" value={formatPct(s.top3ConcentrationPercent)} />
      <Card label="Base currency" value={snapshot.baseCurrency} />
    </div>
  );
}
