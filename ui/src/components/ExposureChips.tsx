export default function ExposureChips({
  title,
  data,
  limit = 6,
}: {
  title: string;
  data: Record<string, number>;
  limit?: number;
}) {
  const entries = Object.entries(data ?? {})
    .sort((a, b) => b[1] - a[1])
    .slice(0, limit);

  if (entries.length === 0) return null;

  return (
    <div className="card">
      <h3 className="cardTitle">{title}</h3>
      <div className="row">
        {entries.map(([k, v]) => (
          <span className="badge" key={k}>
            <strong>{k}</strong> {v.toFixed(2)}%
          </span>
        ))}
      </div>
    </div>
  );
}
