import { Routes, Route, Navigate } from "react-router-dom";
import UploadPage from "./pages/UploadPage";
import SnapshotPage from "./pages/SnapshotPage";

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<UploadPage />} />
      <Route path="/snapshots/:id" element={<SnapshotPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
