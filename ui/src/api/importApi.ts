import type { Snapshot } from "../types/Snapshot";

function apiUrl(path: string) {
  const base = import.meta.env.VITE_API_BASE_URL;
  if (!base) {
    throw new Error("VITE_API_BASE_URL is not set");
  }
  return `${base.replace(/\/+$/, "")}${path.startsWith("/") ? "" : "/"}${path}`;
}

export async function importCsv(
  file: File,
  baseCurrency: string = "SEK"
): Promise<Snapshot> {
  const form = new FormData();
  form.append("file", file);
  form.append("baseCurrency", baseCurrency);
  form.append("source", "CSV");

  const res = await fetch(apiUrl("/import/csv"), {
    method: "POST",
    body: form,
  });

  const contentType = res.headers.get("content-type") || "";
  const text = await res.text();

  if (!res.ok) {
    throw new Error(
      `Import failed: HTTP ${res.status} ${text || res.statusText}`
    );
  }

  if (!contentType.includes("application/json")) {
    throw new Error(
      `Expected JSON from backend, got '${contentType}'. Body: ${text.slice(0, 200)}`
    );
  }

  return JSON.parse(text) as Snapshot;
}