package it.unipd.dei.ims.falcon.audio;
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
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import static java.util.stream.IntStream.range;

/**
 * Read an audio file. Provided that the appropriate packages are in the 
 * classpath, mp3 and ogg should be readable too.
 * The safest route is to read from a MONO, WAV file.
 */
public class AudioReader implements AutoCloseable {

	private final AudioInputStream signedInputStream;
	private final AudioInputStream inputStream;

	public AudioReader(final File file) throws UnsupportedAudioFileException, IOException {
		inputStream = AudioSystem.getAudioInputStream(file);

		final AudioFormat audioFormat =
                new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
						inputStream.getFormat().getSampleRate(),
						16,
                        1,
                        16 / 8 * 1,
                        inputStream.getFormat().getSampleRate(),
                        true);
		signedInputStream = AudioSystem.getAudioInputStream(audioFormat, inputStream);
	}

	/** Read next n samples (at most) from file (interleaved  if channels > 1). Return as double[]. */
	public double[] readDoubleSamples(int n) throws IOException {
		int fl = signedInputStream.getFormat().getFrameSize();
		int ss = signedInputStream.getFormat().getSampleSizeInBits();
		int ssB = ss / 8;
		byte[] b = new byte[n * signedInputStream.getFormat().getChannels() * ssB];
		int read = signedInputStream.read(b);

		return range(0, read / ssB)
                .mapToDouble(index -> {
                    switch (ss) {
                        case 8:
                            return ((b[index * ssB] & 0xFF) - 128) / 128.0;
                        case 16:
                            return ((b[index * ssB + 0] << 8) | (b[index * ssB + 1] & 0xFF)) / 32768.0;
                        case 24:
                            return ((b[index * ssB + 0] << 16) | ((b[index * ssB + 1] & 0xFF) << 8)
                                    | (b[index * ssB + 2] & 0xFF)) / 8388606.0;
                        case 32:
                            return ((b[index * ssB + 0] << 24) | ((b[index * ssB + 1] & 0xFF) << 16)
                                    | ((b[index * ssB + 2] & 0xFF) << 8) | (b[index * ssB + 3] & 0xFF)) / 2147483648.0;
                        default:
                            throw new IllegalArgumentException();}})
                .toArray();
	}

	public float getSampleRate() {
		return signedInputStream.getFormat().getSampleRate();
	}

	@Override
	public void close() throws IOException {
		signedInputStream.close();
		inputStream.close();
	}
}