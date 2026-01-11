import type { Snapshot } from "../types/Snapshot";

type Props = {
  snapshot: Snapshot;
};

const numberFmt = new Intl.NumberFormat("en-US", {
  minimumFractionDigits: 0,
  maximumFractionDigits: 2,
});

const currencyFmt = (currency: string) =>
  new Intl.NumberFormat("en-US", {
    style: "currency",
    currency,
    minimumFractionDigits: 2,
  });

export default function HoldingsTable({ snapshot }: Props) {
  const holdings = snapshot.holdings ?? [];

  if (holdings.length === 0) {
    return <p className="muted">No holdings in this snapshot.</p>;
  }

  return (
    <div className="tableWrap">
      <table>
        <thead>
          <tr>
            <th>Instrument</th>
            <th>Ticker</th>
            <th>ISIN</th>
            <th style={{ textAlign: "right" }}>Qty</th>
            <th style={{ textAlign: "right" }}>Price</th>
            <th style={{ textAlign: "right" }}>Market value</th>
            <th>Sector</th>
            <th>Region</th>
          </tr>
        </thead>
        <tbody>
          {holdings.map((h, i) => (
            <tr key={i}>
              <td style={{ fontWeight: 600 }}>{h.instrumentName}</td>
              <td>{h.ticker || "–"}</td>
              <td>{h.isin || "–"}</td>
              <td style={{ textAlign: "right" }}>
                {numberFmt.format(h.quantity)}
              </td>
              <td style={{ textAlign: "right" }}>
                {currencyFmt(h.currency).format(h.price)}
              </td>
              <td style={{ textAlign: "right", fontWeight: 600 }}>
                {currencyFmt(snapshot.baseCurrency).format(h.marketValue)}
              </td>
              <td>{h.sector || "–"}</td>
              <td>{h.region || "–"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
