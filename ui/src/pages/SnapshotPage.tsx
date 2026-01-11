import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { getSnapshot } from "../api/snapshotsApi";
import type { Snapshot } from "../types/Snapshot";
import Page from "../components/Page";
import SnapshotHeader from "../components/SnapshotHeader";
import StatsCards from "../components/StatsCards";
import HoldingsTable from "../components/HoldingsTable";
import CommentaryPanel from "../components/CommentaryPanel";
import ExposureChips from "../components/ExposureChips";

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
    <Page>
      <div className="row" style={{ justifyContent: "space-between" }}>
        <div />
        <Link to="/" className="muted">
          ← New upload
        </Link>
      </div>

      {err && (
        <div className="errorBox">
          <strong>Error</strong>
          <pre style={{ whiteSpace: "pre-wrap", margin: "8px 0 0 0" }}>{err}</pre>
        </div>
      )}

      {!snapshot && !err && <p className="muted">Loading snapshot…</p>}

      {snapshot && (
        <>
          {/* Header */}
          <div className="card">
            <SnapshotHeader snapshot={snapshot} />
          </div>

          {/* Stats */}
          <StatsCards snapshot={snapshot} />

          {/* Exposures */}
          <div className="row">
            <div style={{ flex: 1, minWidth: 320 }}>
              <ExposureChips title="Sector exposure" data={snapshot.sectorExposure} />
            </div>
            <div style={{ flex: 1, minWidth: 320 }}>
              <ExposureChips title="Region exposure" data={snapshot.regionExposure} />
            </div>
            <div style={{ flex: 1, minWidth: 320 }}>
              <ExposureChips title="Currency exposure" data={snapshot.currencyExposure} />
            </div>
          </div>

          {/* Holdings */}
          <div className="card">
            <h2 className="cardTitle">Holdings</h2>
            <HoldingsTable snapshot={snapshot} />
          </div>

          {/* Commentary */}
          <div className="card">
            <CommentaryPanel snapshotId={snapshot.id} />
          </div>
        </>
      )}
    </Page>
  );
}
