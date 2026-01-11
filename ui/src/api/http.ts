const API_BASE_URL =
  (import.meta.env.VITE_API_BASE_URL as string | undefined) ??
  "http://localhost:8080";

export async function http<T>(
  path: string,
  init: RequestInit = {}
): Promise<T> {
  const headers = new Headers(init.headers);

  const body = init.body as any;
  const isFormData =
    typeof FormData !== "undefined" && body instanceof FormData;

  // Only set JSON content-type when NOT uploading FormData
  if (!isFormData && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  const res = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  });

  const text = await res.text();

  if (!res.ok) {
    throw new Error(`HTTP ${res.status}: ${text || res.statusText}`);
  }

  // Handle empty responses (e.g. 204)
  if (!text.trim()) {
    return null as unknown as T;
  }

  return JSON.parse(text) as T;
}
