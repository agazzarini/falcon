package it.unipd.dei.ims.falcon.ranking;

/*
 * Copyright 2010 University of Padova, Italy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class DocScorePair implements Comparable<DocScorePair> {

    private final String doc;
	private final double score;

	public int compareTo(final DocScorePair o) {
		if (getScore() < o.getScore())
			return 1;
		if (getScore() > o.getScore())
			return -1;
		return getDoc().compareTo(o.getDoc());
	}

	public DocScorePair(final String d, final double s) {
		this.doc = d;
		this.score = s;
	}

    public String getDoc() {
        return doc;
    }

    public double getScore() {
        return score;
    }

	public static SortedSet<DocScorePair> docscore2scoredoc(Map<String, Double> map) {
		return map.entrySet().stream()
                .map(entry -> new DocScorePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toCollection(TreeSet::new));
	}
}