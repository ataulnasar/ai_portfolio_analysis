export interface SnapshotStats {
  numberOfHoldings: number;
  topHoldingWeightPercent: number;
  top3ConcentrationPercent: number;
}

export interface Holding {
  instrumentName: string;
  ticker?: string | null;
  isin?: string | null;
  quantity: number;
  price: number;
  currency: string;
  sector?: string | null;
  region?: string | null;
  marketValue: number;
}

export interface TopHolding {
  instrumentName: string;
  weightPercent: number;
}

export interface Snapshot {
  id: string;
  asOfDate: string;
  baseCurrency: string;
  source: string;
  totalMarketValue: number;
  stats: SnapshotStats;
  topHoldings: TopHolding[];
  sectorExposure: Record<string, number>;
  regionExposure: Record<string, number>;
  currencyExposure: Record<string, number>;
  holdings: Holding[];
}
