package dev.zarr.zarrjava.utils;

import dev.zarr.zarrjava.ZarrException;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

/**
 * Numcodecs-compatible vlen-utf8 encoding used by AnnData-on-Zarr and zarr-python.
 */
public final class VLenUtf8 {

    private VLenUtf8() {
    }

    public static String[] decode(byte[] bytes, int expectedLength) throws ZarrException {
        ByteBuffer buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        if (buffer.remaining() < 4) {
            return new String[0];
        }

        int numItems = buffer.getInt();
        if (numItems <= 0) {
            return new String[0];
        }

        String[] values = new String[numItems];
        for (int i = 0; i < numItems; i++) {
            if (buffer.remaining() < 4) {
                throw new ZarrException(
                        "Unexpected end of vlen-utf8 data while reading length at index " + i);
            }
            int size = buffer.getInt();
            if (size < 0 || size > buffer.remaining()) {
                throw new ZarrException("Invalid vlen-utf8 length " + size + " at index " + i);
            }
            byte[] valueBytes = new byte[size];
            buffer.get(valueBytes);
            values[i] = new String(valueBytes, StandardCharsets.UTF_8);
        }

        if (numItems != expectedLength) {
            throw new ZarrException(
                    "Expected " + expectedLength + " vlen-utf8 values but decoded " + numItems);
        }
        return values;
    }

    public static byte[] encode(String[] values) throws ZarrException {
        int totalSize = 4;
        for (String value : values) {
            byte[] encoded = value.getBytes(StandardCharsets.UTF_8);
            totalSize += 4 + encoded.length;
        }

        ByteBuffer buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(values.length);
        for (String value : values) {
            byte[] encoded = value.getBytes(StandardCharsets.UTF_8);
            buffer.putInt(encoded.length);
            buffer.put(encoded);
        }
        return buffer.array();
    }
}
