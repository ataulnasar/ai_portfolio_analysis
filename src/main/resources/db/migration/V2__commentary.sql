CREATE TABLE commentary (
                            id uuid PRIMARY KEY,
                            snapshot_id uuid NOT NULL REFERENCES portfolio_snapshot(id) ON DELETE CASCADE,

                            model varchar(64) NOT NULL,
                            prompt_version varchar(64) NOT NULL,
                            sections_json jsonb NOT NULL,
                            created_at timestamptz NOT NULL
);

CREATE UNIQUE INDEX uq_commentary_snapshot_prompt
    ON commentary(snapshot_id, prompt_version);

CREATE INDEX idx_commentary_snapshot_id ON commentary(snapshot_id);
CREATE INDEX idx_commentary_created_at ON commentary(created_at);
