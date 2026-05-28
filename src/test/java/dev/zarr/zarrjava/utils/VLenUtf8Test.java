package dev.zarr.zarrjava.utils;

import dev.zarr.zarrjava.ZarrException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VLenUtf8Test {

    @Test
    void roundTripEncodesAndDecodesStrings() throws ZarrException {
        String[] original = {"alpha", "beta", "gamma"};
        String[] decoded = VLenUtf8.decode(VLenUtf8.encode(original), original.length);
        Assertions.assertArrayEquals(original, decoded);
    }

    @Test
    void decodeEmptyPayload() throws ZarrException {
        Assertions.assertArrayEquals(new String[0], VLenUtf8.decode(new byte[0], 0));
    }
}
