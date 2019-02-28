package it.unipd.dei.ims.falcon.analysis.chromafeatures;

/*
 * Copyright 2012 University of Padova, Italy
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
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import it.unipd.dei.ims.falcon.audio.AudioReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.DoubleUnaryOperator;

import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

public class ChromaExtraction {
    private final static Logger LOGGER = LoggerFactory.getLogger(ChromaExtraction.class);
	private final static double A0 = 440. / 16.;

	private static double[] getHammingWindow(final int N) {
		return range(0, N)
                .mapToDouble(index -> .54 - .46 * Math.cos(2 * Math.PI * index / (N - 1)))
                .toArray();
	}

	private static int closestPowerOfTwo(int n) {
		for (int p = 1; p < 16; p++) {   // ugly code for getting best matching window length
			int h = (int) Math.pow(2, p);
			int l = (int) Math.pow(2, p - 1);
			if (h >= n && l <= n) {
				n = n - l < h - n ? l : h;
				break;
			}
		}
		return n;
	}

	private static DoubleUnaryOperator octaver = frequency -> {
        final double octave = Math.log10(frequency / A0) / Math.log10(2);

        LOGGER.debug("Frequency {} belongs to octave {}", frequency, octave);

        return octave;
    };

	private static DoubleUnaryOperator closestPitch =
            octaver.andThen(octave -> octave - Math.floor(octave))
                    .andThen(fraction -> fraction * 12)
                    .andThen(Math::round)
                    .andThen(value -> value % 12);


	private static int closestPitch(final double frequency) {
	    return (int) closestPitch.applyAsDouble(frequency);
	}

	static double octave(final double frequency) {
        final double octave = Math.log10(frequency / A0) / Math.log10(2);
	    LOGGER.debug("Frequency {} belongs to octave {}", frequency, octave);
	    return octave;
    }

	/**
	 * shift the audio buffer left, and read;
	 * @return true if all n samples were read; false otherise
	 */
	private static boolean shiftAndRead(double[] buffer, AudioReader reader, int n) throws IOException {
		double[] tmp = reader.readDoubleSamples(n);
		if (tmp.length < n)
			return false;

        for (int i = 0; i < buffer.length - n; i++)
            buffer[i] = buffer[i + n];

		for (int i = 0; i < n; i++)
			buffer[buffer.length - n + i] = tmp[i];
		return true;
	}

	private static double[] peakPick(double[] spectrum) {
		double[] pp = new double[spectrum.length];
		for (int i = 1; i < spectrum.length - 1; i++)
			if (spectrum[i] > spectrum[i - 1] && spectrum[i] > spectrum[i + 1])
				pp[i] = spectrum[i];
			else
				pp[i] = 0;
		return pp;
	}

	/**
	 * Returns a matrix of chroma features, each chroma is a row
     *
	 * @param reader audio stream
	 * @param winLenInMs length in ms; will be rounded to the closes power of two
	 * @param hopsizeRatio 1 = no hopsize, 2 = 50% overlap, 3 = 66% overlap ...
	 * @return 
	 */
	public static List<ChromaVector> getChromaFeatures(AudioReader reader, double winLenInMs, int hopsizeRatio) throws IOException {

		int winLen = closestPowerOfTwo((int) (reader.getSampleRate() * (winLenInMs / 1000.)));
		int hopSize = winLen / hopsizeRatio;

		List<float[]> chromas_floats = new LinkedList<float[]>();
		double[] hammingwin = getHammingWindow(winLen);


		double[] audio = new double[winLen];
		shiftAndRead(audio, reader, hopSize * (hopsizeRatio - 1));

		DoubleFFT_1D fftizer = new DoubleFFT_1D(winLen);

		int[] closestPitches = new int[winLen / 2];
		for (int i = 0; i < closestPitches.length; i++) {
			double f = i / reader.getSampleRate();
			closestPitches[i] = closestPitch(f);
		}

		while (true) {
			if (!shiftAndRead(audio, reader, hopSize))
				break;
			double[] fft = Arrays.copyOf(audio, winLen);       // copy buffer 
			for (int i = 0; i < winLen; i++)                    // do windowing
				fft[i] *= hammingwin[i];
			fftizer.realForward(fft);                          // do fft
			double[] spectrum = new double[winLen / 2];          // compute abs fft
			for (int i = 0; i < Math.min(spectrum.length, 10000 / reader.getSampleRate() * winLen); i++)
				spectrum[i] = Math.sqrt(fft[2 * i] * fft[2 * i] + fft[2 * i + 1] * fft[2 * i + 1]);
			spectrum = peakPick(spectrum);                     // spectrum peaks
			float[] chroma = new float[12];
			for (int i = 0; i < chroma.length; i++)
				chroma[i] = 0;
			for (int i = 0; i < closestPitches.length; i++)
				chroma[closestPitches[i]] += spectrum[i];
			chromas_floats.add(chroma);
		}

		return chromas_floats.stream().map(ChromaVector::new).collect(toList());
	}
}
