import { useState } from "react";
import { importCsv } from "../api/importApi";
import type { Snapshot } from "../types/Snapshot";

type Props = {
  onImported: (snapshot: Snapshot) => void;
};

export default function UploadForm({ onImported }: Props) {
  const [file, setFile] = useState<File | null>(null);
  const [baseCurrency, setBaseCurrency] = useState("SEK");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);

    if (!file) {
      setError("Please select a CSV file.");
      return;
    }

    try {
      setLoading(true);
      const snapshot = await importCsv(file, baseCurrency);
      onImported(snapshot);
    } catch (err) {
      setError(err instanceof Error ? err.message : String(err));
    } finally {
      setLoading(false);
    }
  }

  return (
    <form onSubmit={onSubmit} style={{ display: "grid", gap: 12, maxWidth: 520 }}>
      <label style={{ display: "grid", gap: 6 }}>
        <span>CSV file</span>
        <input
          type="file"
          accept=".csv,text/csv"
          onChange={(e) => setFile(e.target.files?.[0] ?? null)}
          disabled={loading}
        />
      </label>

      <label style={{ display: "grid", gap: 6 }}>
        <span>Base currency</span>
        <select value={baseCurrency} onChange={(e) => setBaseCurrency(e.target.value)} disabled={loading}>
          <option value="SEK">SEK</option>
          <option value="EUR">EUR</option>
          <option value="USD">USD</option>
        </select>
      </label>

      <button className="btn" type="submit" disabled={loading}>
        {loading ? "Importing..." : "Import CSV"}
      </button>

      {error && (
        <div className="errorBox">
          <strong>Error</strong>
          <pre style={{ whiteSpace: "pre-wrap", margin: "8px 0 0 0" }}>{error}</pre>
        </div>
      )}
    </form>
  );
}
