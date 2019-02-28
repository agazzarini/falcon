package it.unipd.dei.ims.falcon.ranking;

import java.util.Map;

public class QueryResults {

	private final Map<String, Double> results;
	private final long prunedHashes;
	private final long totalConsideredHashes;

	QueryResults(final Map<String, Double> results, final long prunedHashes, final long totalConsideredHashes) {
		this.results = results;
		this.prunedHashes = prunedHashes;
		this.totalConsideredHashes = totalConsideredHashes;
	}

	public Map<String, Double> getResults() {
		return results;
	}

	public long getPrunedHashes() {
		return prunedHashes;
	}

	public long getTotalConsideredHashes() {
		return totalConsideredHashes;
	}
}
