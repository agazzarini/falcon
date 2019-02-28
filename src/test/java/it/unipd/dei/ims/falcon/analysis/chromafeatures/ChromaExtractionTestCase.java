package it.unipd.dei.ims.falcon.analysis.chromafeatures;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ChromaExtractionTestCase {
    private ChromaExtraction chromaExtraction;

    @Before
    public void setUp() {
        chromaExtraction = new ChromaExtraction();
    }

    @Test
    public void octave() {
        Map<Double, Double> expected = new HashMap<>();
        {
            expected.put(440d, 4.0);
        }

        expected.forEach( (k,v) -> assertEquals(v, ChromaExtraction.octave(k), 0.0d));
    }
}
