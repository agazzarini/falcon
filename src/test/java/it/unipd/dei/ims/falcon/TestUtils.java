package it.unipd.dei.ims.falcon;

import it.unipd.dei.ims.falcon.analysis.chromafeatures.ChromaVector;

import java.util.Random;

import static java.util.stream.IntStream.range;

public abstract class TestUtils {
    private final static Random RANDOMIZER = new Random();

    public static double randomDouble() {
        return RANDOMIZER.nextDouble();
    }

    public static ChromaVector newChromaVector() {
        return new ChromaVector(range(0, 12).mapToDouble(index -> randomDouble()).toArray());
    }
}
