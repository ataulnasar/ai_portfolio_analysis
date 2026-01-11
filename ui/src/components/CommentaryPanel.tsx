import { useEffect, useState } from "react";
import type { Commentary } from "../types/Commentary";
import { getCommentaryForSnapshot, generateCommentary } from "../api/commentaryApi";

type Props = {
  snapshotId: string;
};

function Section({ title, text }: { title: string; text: string }) {
  return (
    <div style={{ display: "grid", gap: 6 }}>
      <div style={{ fontWeight: 700 }}>{title}</div>
      <div style={{ color: "#223", lineHeight: 1.4 }}>{text}</div>
    </div>
  );
}

export default function CommentaryPanel({ snapshotId }: Props) {
  const [commentary, setCommentary] = useState<Commentary | null>(null);
  const [loading, setLoading] = useState(false);
  const [missing, setMissing] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 1) Load existing commentary on mount
  useEffect(() => {
    let cancelled = false;
    setError(null);
    setMissing(false);
    setCommentary(null);
    setLoading(true);

    getCommentaryForSnapshot(snapshotId)
      .then((c) => {
        if (cancelled) return;
        setCommentary(c);
      })
      .catch((e) => {
        if (cancelled) return;
        if (e instanceof Error && e.message === "NOT_FOUND") {
          setMissing(true);
        } else {
          setError(e instanceof Error ? e.message : String(e));
        }
      })
      .finally(() => {
        if (cancelled) return;
        setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [snapshotId]);

  async function onGenerate() {
    setError(null);
    setLoading(true);
    try {
      const c = await generateCommentary(snapshotId);
      setCommentary(c);
      setMissing(false);
    } catch (e) {
      setError(e instanceof Error ? e.message : String(e));
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{ display: "grid", gap: 12 }}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
        <h2 style={{ margin: 0 }}>Commentary</h2>
        {commentary && (
          <div style={{ fontSize: 12, color: "#556" }}>
            {commentary.promptVersion} · {commentary.model}
          </div>
        )}
      </div>

      {loading && <p>Loading commentary…</p>}

      {error && (
        <div style={{ border: "1px solid #f3c2c2", background: "#fff6f6", padding: 12, borderRadius: 10 }}>
          <strong>Error</strong>
          <pre style={{ whiteSpace: "pre-wrap", margin: "8px 0 0 0" }}>{error}</pre>
        </div>
      )}

      {!loading && missing && (
        <div style={{ border: "1px solid #eee", background: "white", padding: 12, borderRadius: 10 }}>
          <p style={{ marginTop: 0 }}>No commentary exists for this snapshot yet.</p>
          <button onClick={onGenerate} style={{ padding: "10px 12px" }}>
            Generate commentary
          </button>
        </div>
      )}

      {!loading && commentary && (
        <div style={{ border: "1px solid #eee", background: "white", padding: 14, borderRadius: 10, display: "grid", gap: 14 }}>
          <Section title="Summary" text={commentary.sections.summary} />
          <Section title="Concentration & structure" text={commentary.sections.concentrationAndStructure} />
          <Section title="Sector exposure" text={commentary.sections.sectorExposure} />
          <Section title="Geo & currency" text={commentary.sections.geoAndCurrency} />
          <Section title="Contextual note" text={commentary.sections.contextualNote} />
          <Section title="Disclaimer" text={commentary.sections.disclaimer} />
        </div>
      )}
    </div>
  );
}
