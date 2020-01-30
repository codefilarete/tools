package org.gama.lang.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Methods around {@link InputStream} and {@link OutputStream}.
 *
 * @author Guillaume Mary
 */
public final class IOs {
	
	public static final long _1_Ko = 1024;
	
	public static final long _8_Ko = 8 * _1_Ko;
	
	public static final long _512_Ko = 512 * _1_Ko;
	
	public static final long _1_Mo = 1024 * _1_Ko;
	
	public static final long _1_Go = 1024 * _1_Mo;
	
	/**
	 * Convert an {@link InputStream} as a {@link ByteArrayInputStream} by using a 1024 byte buffer.
	 * It's up to the caller to close the passed {@link InputStream} argument.
	 *
	 * @param inputStream the source
	 * @return a new {@link ByteArrayInputStream} which content is the input stream
	 * @throws IOException if an error occurs during copy
	 * @see #toByteArrayInputStream(InputStream)
	 */
	public static ByteArrayInputStream toByteArrayInputStream(InputStream inputStream) throws IOException {
		return toByteArrayInputStream(inputStream, 1024);
	}
	
	/**
	 * Convert an {@link InputStream} as a {@link ByteArrayInputStream} by using a byte buffer.
	 * It's up to the caller to close the passed {@link InputStream} argument.
	 *
	 * @param inputStream the source
	 * @param bufferSize the size of the buffer to use (performance optimization)
	 * @return a new {@link ByteArrayInputStream} with ne content of inputStream
	 * @throws IOException if an error occurs during copy
	 */
	public static ByteArrayInputStream toByteArrayInputStream(InputStream inputStream, int bufferSize) throws IOException {
		return new ByteArrayInputStream(toByteArray(inputStream, bufferSize));
	}
	
	/**
	 * Convert an {@link InputStream} as a byte array with a 1024 byte buffer
	 * It's up to the caller to close the passed {@link InputStream} argument.
	 *
	 * @param inputStream the source
	 * @return a byte[] which content is the input stream
	 * @throws IOException if an error occurs during copy
	 * @see #toByteArrayInputStream(InputStream, int)
	 */
	public static byte[] toByteArray(InputStream inputStream) throws IOException {
		return toByteArray(inputStream, 1024);
	}
	
	/**
	 * Convert an {@link InputStream} as a byte array by using a byte buffer.
	 *
	 * @param inputStream the source
	 * @param bufferSize the size of the buffer to use (performance optimization)
	 * @return a byte[] which content is the input stream
	 * @throws IOException if an error occurs during copy
	 */
	public static byte[] toByteArray(InputStream inputStream, int bufferSize) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
			copy(inputStream, bos, bufferSize);
			return bos.toByteArray();
		}
	}
	
	/**
	 * Copy an {@link InputStream} to an {@link OutputStream}
	 *
	 * @param inputStream the source
	 * @param outputStream the target
	 * @param bufferSize the size of packet
	 * @throws IOException if an error occurs during copy
	 */
	public static void copy(InputStream inputStream, OutputStream outputStream, int bufferSize) throws IOException {
		int b;
		byte[] readBytes = new byte[bufferSize];
		while ((b = inputStream.read(readBytes)) != -1) {
			outputStream.write(readBytes, 0, b);
		}
	}
	
	private IOs() {
	}
	
	/**
	 * A convenient class to iterate over an {@link InputStream} as an {@link Iterator}
	 */
	public static class InputStreamIterator implements Iterator<byte[]> {
		
		/**
		 * Convenience method to create an {@link Iterable} from an {@link InputStream}
		 * @param source the source of bytes
		 * @param packetSize the size of returned packets
		 * @return a new {@link Iterable} wrapping the given {@link InputStream}
		 */
		public static Iterable<byte[]> iterable(InputStream source, int packetSize) {
			return () -> new InputStreamIterator(source, packetSize);
		}
		
		private final InputStream source;
		private final int packetSize;
		private final byte[] buffer;
		
		/**
		 * Creates an {@link Iterator} from an {@link InputStream}
		 * 
		 * @param source the source of bytes
		 * @param packetSize the size of returned packets
		 */
		public InputStreamIterator(InputStream source, int packetSize) {
			this.source = source;
			this.packetSize = packetSize;
			this.buffer = new byte[packetSize];
		}
		
		@Override
		public boolean hasNext() {
			try {
				return source.available() != 0;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public byte[] next() {
			// compliance with next() specification
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			try {
				int readBytesCount = source.read(buffer, 0, packetSize);
				if (readBytesCount == buffer.length) {
					return buffer;
				} else {
					// last packet may have a different size
					return Arrays.copyOf(buffer, readBytesCount);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
