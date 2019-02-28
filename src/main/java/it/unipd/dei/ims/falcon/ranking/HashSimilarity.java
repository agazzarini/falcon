package it.unipd.dei.ims.falcon.ranking;

/**
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


import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.Similarity;

/**
 * Specific implementation of Lucene {@link org.apache.lucene.search.Similarity}.
 * The current implementation exploits this class only to store and get the
 * norm of a segment, that is the number of hashes per segment.
 *
 * All the currently unused methods return 1.
 *
 * @see org.apache.lucene.search.Similarity
 *
 */
public class HashSimilarity extends Similarity {

    private static final long serialVersionUID = 1L;

    @Override
    public float computeNorm(final String field, final FieldInvertState state) {
        return lengthNorm(field, state.getLength());
    }

    @Override
    public float lengthNorm(String fieldName, int numTerms) {
        return (numTerms == 0) ? 0 : 1f / numTerms;
    }

    @Override
    public float queryNorm(float sumOfSquaredWeights) {
        return 1.0f;
    }

    @Override
    public float tf(float freq) {
        return freq;
    }

    @Override
    public float sloppyFreq(int distance) {
        return 1.0f;
    }

    @Override
    public float idf(int docFreq, int numDocs) {
        return 1.0f;
    }

    @Override
    public float coord(int overlap, int maxOverlap) {
        return 1.0f;
    }
}
