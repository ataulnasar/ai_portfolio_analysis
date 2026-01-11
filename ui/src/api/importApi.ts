import type { Snapshot } from "../types/Snapshot";

function apiUrl(path: string) {
  const base = import.meta.env.VITE_API_BASE_URL as string | undefined;
  if (!base) {
    throw new Error("VITE_API_BASE_URL is not set (Render Static Site → Environment).");
  }
  const normalizedBase = base.replace(/\/+$/, "");
  const normalizedPath = path.startsWith("/") ? path : `/${path}`;
  return `${normalizedBase}${normalizedPath}`;
}

async function parseJsonOrThrow<T>(res: Response): Promise<T> {
  const contentType = res.headers.get("content-type") || "";
  const text = await res.text(); // read once

  if (!res.ok) {
    // Try to extract a useful message from JSON error responses
    if (contentType.includes("application/json") && text) {
      try {
        const data = JSON.parse(text) as any;
        const msg =
          data?.message ?? data?.error ?? data?.detail ?? JSON.stringify(data);
        throw new Error(`Import failed: HTTP ${res.status} ${msg}`);
      } catch {
        // fall through
      }
    }
    throw new Error(
      `Import failed: HTTP ${res.status} ${res.statusText}${text ? `: ${text.slice(0, 500)}` : ""}`
    );
  }

  // Success but empty response body (avoid "Unexpected end of JSON input")
  if (!text.trim()) {
    throw new Error("Import succeeded but response body was empty (expected JSON Snapshot).");
  }

  if (!contentType.includes("application/json")) {
    throw new Error(
      `Import succeeded but did not return JSON (content-type: '${contentType || "unknown"}'). Body: ${text.slice(0, 200)}`
    );
  }

  return JSON.parse(text) as T;
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

  return parseJsonOrThrow<Snapshot>(res);
}