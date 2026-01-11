import { useNavigate } from "react-router-dom";
import UploadForm from "../components/UploadForm";
import type { Snapshot } from "../types/Snapshot";

export default function UploadPage() {
  const navigate = useNavigate();

  function onImported(snapshot: Snapshot) {
    navigate(`/snapshots/${snapshot.id}`);
  }

  return (
    <div style={{ padding: 24 }}>
      <h1>Portfolio Commentary</h1>
      <p>Upload a CSV to create a snapshot.</p>

      <UploadForm onImported={onImported} />
    </div>
  );
}
