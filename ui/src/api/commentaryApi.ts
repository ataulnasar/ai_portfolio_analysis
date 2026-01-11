import type { Commentary } from "../types/Commentary";
import { http } from "./http";

export async function getCommentaryForSnapshot(
  snapshotId: string
): Promise<Commentary> {
  try {
    return await http<Commentary>(`/snapshots/${snapshotId}/commentary`, {
      method: "GET",
    });
  } catch (err: any) {
    // Keep your existing semantic for 404
    const msg = String(err?.message ?? err);
    if (msg.startsWith("HTTP 404")) {
      throw new Error("NOT_FOUND");
    }
    throw new Error(`Failed to load commentary: ${msg}`);
  }
}

export async function generateCommentary(
  snapshotId: string
): Promise<Commentary> {
  try {
    return await http<Commentary>("/commentary", {
      method: "POST",
      body: JSON.stringify({ snapshotId }),
    });
  } catch (err: any) {
    const msg = String(err?.message ?? err);
    throw new Error(`Failed to generate commentary: ${msg}`);
  }
}
