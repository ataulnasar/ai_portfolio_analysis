import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import { getSnapshot } from "../api/snapshotsApi";
import type { Snapshot } from "../types/Snapshot";
import SnapshotHeader from "../components/SnapshotHeader";
import StatsCards from "../components/StatsCards";
import HoldingsTable from "../components/HoldingsTable";

export default function SnapshotPage() {
  const { id } = useParams();
  const [snapshot, setSnapshot] = useState<Snapshot | null>(null);
  const [err, setErr] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    setErr(null);
    setSnapshot(null);

    getSnapshot(id)
      .then(setSnapshot)
      .catch((e) => setErr(e instanceof Error ? e.message : String(e)));
  }, [id]);

  return (
    <div style={{ padding: 24, display: "grid", gap: 16 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
        <div />
        <Link to="/" style={{ color: "#335" }}>← New upload</Link>
      </div>

      {err && (
        <div style={{ border: "1px solid #f3c2c2", background: "#fff6f6", padding: 12, borderRadius: 10 }}>
          <strong>Error</strong>
          <pre style={{ whiteSpace: "pre-wrap", margin: "8px 0 0 0" }}>{err}</pre>
        </div>
      )}

      {!snapshot && !err && <p>Loading snapshot…</p>}

      {snapshot && (
        <>
          <SnapshotHeader snapshot={snapshot} />
          <StatsCards snapshot={snapshot} />

          <div style={{ display: "grid", gap: 8 }}>
            <h2 style={{ margin: "6px 0 0 0" }}>Holdings</h2>
            <HoldingsTable snapshot={snapshot} />
          </div>
        </>
      )}
    </div>
  );
}
