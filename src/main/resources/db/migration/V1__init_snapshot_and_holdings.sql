-- Enable UUID generation (optional but useful)
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE portfolio_snapshot (
                                    id uuid PRIMARY KEY,
                                    as_of_date date NOT NULL,
                                    base_currency varchar(8) NOT NULL,
                                    source varchar(32) NOT NULL,

                                    total_market_value numeric(19,2) NOT NULL,
                                    number_of_holdings int NOT NULL,
                                    top_holding_weight_pct numeric(7,2) NOT NULL,
                                    top3_concentration_pct numeric(7,2) NOT NULL,

                                    top_holdings_json text NOT NULL,
                                    sector_exposure_json text NOT NULL,
                                    region_exposure_json text NOT NULL,
                                    currency_exposure_json text NOT NULL,

                                    created_at timestamptz NOT NULL
);

CREATE INDEX idx_snapshot_as_of_date ON portfolio_snapshot(as_of_date);
CREATE INDEX idx_snapshot_source ON portfolio_snapshot(source);

CREATE TABLE equity_holding (
                                id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
                                snapshot_id uuid NOT NULL REFERENCES portfolio_snapshot(id) ON DELETE CASCADE,

                                instrument_name text NOT NULL,
                                ticker text NULL,
                                isin text NULL,

                                quantity numeric(19,6) NOT NULL,
                                price numeric(19,6) NOT NULL,
                                currency varchar(8) NOT NULL,

                                sector text NULL,
                                region text NULL,

                                market_value numeric(19,2) NOT NULL
);

CREATE INDEX idx_holding_snapshot_id ON equity_holding(snapshot_id);
CREATE INDEX idx_holding_instrument_name ON equity_holding(instrument_name);
