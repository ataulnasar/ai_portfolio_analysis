import type { Commentary } from "../types/Commentary";

export async function getCommentaryForSnapshot(snapshotId: string): Promise<Commentary> {
  const res = await fetch(`/snapshots/${snapshotId}/commentary`, { method: "GET" });

  if (res.status === 404) {
    throw new Error("NOT_FOUND");
  }
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Failed to load commentary: HTTP ${res.status} ${res.statusText}: ${text}`);
  }
  return (await res.json()) as Commentary;
}

export async function generateCommentary(snapshotId: string): Promise<Commentary> {
  const res = await fetch(`/commentary`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ snapshotId }),
  });

  if (!res.ok) {
    const text = await res.text();
    throw new Error(`Failed to generate commentary: HTTP ${res.status} ${res.statusText}: ${text}`);
  }
  return (await res.json()) as Commentary;
}
