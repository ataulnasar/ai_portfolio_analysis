import type { Holding, Snapshot } from "../types/Snapshot";
import { formatMoney } from "../utils/format";

function Cell({ children }: { children: React.ReactNode }) {
  return <td style={{ padding: "10px 8px", borderBottom: "1px solid #eee" }}>{children}</td>;
}

function HeaderCell({ children }: { children: React.ReactNode }) {
  return (
    <th
      style={{
        textAlign: "left",
        padding: "10px 8px",
        borderBottom: "1px solid #ddd",
        fontSize: 12,
        color: "#556",
      }}
    >
      {children}
    </th>
  );
}

export default function HoldingsTable({ snapshot }: { snapshot: Snapshot }) {
  const holdings = snapshot.holdings ?? [];

  if (holdings.length === 0) {
    return <p>No holdings found in this snapshot.</p>;
  }

  return (
    <div style={{ overflowX: "auto", border: "1px solid #eee", borderRadius: 10 }}>
      <table style={{ width: "100%", borderCollapse: "collapse", background: "white" }}>
        <thead>
          <tr>
            <HeaderCell>Instrument</HeaderCell>
            <HeaderCell>Ticker</HeaderCell>
            <HeaderCell>ISIN</HeaderCell>
            <HeaderCell>Qty</HeaderCell>
            <HeaderCell>Price</HeaderCell>
            <HeaderCell>Market value</HeaderCell>
            <HeaderCell>Sector</HeaderCell>
            <HeaderCell>Region</HeaderCell>
          </tr>
        </thead>
        <tbody>
          {holdings.map((h: Holding) => (
            <tr key={`${h.instrumentName}-${h.isin ?? ""}-${h.ticker ?? ""}`}>
              <Cell><strong>{h.instrumentName}</strong></Cell>
              <Cell>{h.ticker ?? "-"}</Cell>
              <Cell>{h.isin ?? "-"}</Cell>
              <Cell>{h.quantity}</Cell>
              <Cell>{formatMoney(h.price, h.currency)}</Cell>
              <Cell>{formatMoney(h.marketValue, h.currency)}</Cell>
              <Cell>{h.sector ?? "-"}</Cell>
              <Cell>{h.region ?? "-"}</Cell>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
