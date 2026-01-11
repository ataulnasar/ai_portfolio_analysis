import { http } from "./http";
import type { Snapshot } from "../types/Snapshot";

export function getSnapshots(): Promise<Snapshot[]> {
  return http<Snapshot[]>("/snapshots");
}

export function getSnapshot(id: string): Promise<Snapshot> {
  return http<Snapshot>(`/snapshots/${id}`);
}
