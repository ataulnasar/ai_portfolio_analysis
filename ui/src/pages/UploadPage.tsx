import Page from "../components/Page";
import { useNavigate } from "react-router-dom";
import UploadForm from "../components/UploadForm";
import type { Snapshot } from "../types/Snapshot";

export default function UploadPage() {
  const navigate = useNavigate();

  function onImported(snapshot: Snapshot) {
    navigate(`/snapshots/${snapshot.id}`);
  }

  return (
    <Page>
      <div className="card">
        <h1 style={{ marginTop: 0 }}>Portfolio Commentary</h1>
        <p className="muted">Upload a CSV to create a snapshot.</p>
        <UploadForm onImported={onImported} />
      </div>
    </Page>
  );
}
