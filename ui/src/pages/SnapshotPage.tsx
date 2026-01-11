import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getSnapshot } from "../api/snapshotsApi";
import type { Snapshot } from "../types/Snapshot";

export default function SnapshotPage() {
  const { id } = useParams();
  const [snapshot, setSnapshot] = useState<Snapshot | null>(null);
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    setErr(null);
    getSnapshot(id)
      .then(setSnapshot)
      .catch((e) => setErr(String(e)));
  }, [id]);

  return (
    <div style={{ padding: 24 }}>
      <h1>Snapshot</h1>
      <p>ID: {id}</p>

      {err && <pre style={{ color: "crimson" }}>{err}</pre>}
      {!snapshot && !err && <p>Loading…</p>}

      {snapshot && (
        <div style={{ display: "grid", gap: 12 }}>
          <div>
            <strong>As of:</strong> {snapshot.asOfDate}
          </div>
          <div>
            <strong>Total:</strong> {snapshot.totalMarketValue} {snapshot.baseCurrency}
          </div>
          <div>
            <strong>Holdings:</strong> {snapshot.stats.numberOfHoldings}
          </div>
        </div>
      )}
    </div>
  );
}
