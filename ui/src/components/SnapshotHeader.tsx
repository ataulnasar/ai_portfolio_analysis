import type { Snapshot } from "../types/Snapshot";
import { formatMoney } from "../utils/format";

export default function SnapshotHeader({ snapshot }: { snapshot: Snapshot }) {
  return (
    <div style={{ display: "grid", gap: 6 }}>
      <h1 style={{ margin: 0 }}>Snapshot</h1>
      <div style={{ display: "flex", gap: 16, flexWrap: "wrap", color: "#445" }}>
        <span><strong>As of:</strong> {snapshot.asOfDate}</span>
        <span><strong>Source:</strong> {snapshot.source}</span>
        <span><strong>Total:</strong> {formatMoney(snapshot.totalMarketValue, snapshot.baseCurrency)}</span>
      </div>
    </div>
  );
}
