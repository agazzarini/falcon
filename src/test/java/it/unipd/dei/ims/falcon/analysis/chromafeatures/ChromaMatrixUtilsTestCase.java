package it.unipd.dei.ims.falcon.analysis.chromafeatures;

import it.unipd.dei.ims.falcon.TestUtils;
import org.junit.Test;

import static java.util.stream.IntStream.range;

public class ChromaMatrixUtilsTestCase {

    @Test
    public void readChromaMatrixFromStream() {
        final ChromaVector[] vector = range(0, 100).mapToObj(index -> TestUtils.newChromaVector()).toArray(ChromaVector[]::new);
    }
}
