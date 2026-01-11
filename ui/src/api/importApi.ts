import type { Snapshot } from "../types/Snapshot";

export async function importCsv(
  file: File,
  baseCurrency: string = "SEK"
): Promise<Snapshot> {
  const form = new FormData();
  form.append("file", file);
  form.append("baseCurrency", baseCurrency);
  form.append("source", "CSV");

  const res = await fetch("/import/csv", {
    method: "POST",
    body: form,
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Import failed: HTTP ${res.status} ${res.statusText}: ${text}`);
  }

  return (await res.json()) as Snapshot;
}
