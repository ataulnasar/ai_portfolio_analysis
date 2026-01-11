export interface CommentarySections {
  summary: string;
  concentrationAndStructure: string;
  sectorExposure: string;
  geoAndCurrency: string;
  contextualNote: string;
  disclaimer: string;
}

export interface Commentary {
  id: string;
  snapshotId: string;
  createdAt: string;
  sections: CommentarySections;
  promptVersion: string;
  model: string;
}
