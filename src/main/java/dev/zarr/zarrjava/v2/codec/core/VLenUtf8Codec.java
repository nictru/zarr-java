package dev.zarr.zarrjava.v2.codec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import dev.zarr.zarrjava.ZarrException;
import dev.zarr.zarrjava.core.ArrayMetadata;
import dev.zarr.zarrjava.core.codec.ArrayBytesCodec;
import dev.zarr.zarrjava.utils.Utils;
import dev.zarr.zarrjava.utils.VLenUtf8;
import dev.zarr.zarrjava.v2.codec.Codec;
import ucar.ma2.Array;
import ucar.ma2.DataType;

import java.nio.ByteBuffer;

public class VLenUtf8Codec extends ArrayBytesCodec implements Codec {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public VLenUtf8Codec() {
    }

    @Override
    public Array decode(ByteBuffer chunkBytes) throws ZarrException {
        String[] strings = VLenUtf8.decode(Utils.toArray(chunkBytes), arrayMetadata.chunkSize());
        return Array.makeObjectArray(DataType.OBJECT, String.class, arrayMetadata.chunkShape, strings);
    }

    @Override
    public ByteBuffer encode(Array chunkArray) throws ZarrException {
        if (chunkArray.getElementType() != String.class) {
            throw new ZarrException("VLenUtf8Codec expects String elements, got " + chunkArray.getElementType());
        }
        String[] values = new String[(int) chunkArray.getSize()];
        for (int i = 0; i < values.length; i++) {
            Object value = chunkArray.getObject(i);
            if (!(value instanceof String)) {
                throw new ZarrException("VLenUtf8Codec expects String elements at index " + i);
            }
            values[i] = (String) value;
        }
        return ByteBuffer.wrap(VLenUtf8.encode(values));
    }

    @Override
    public Codec evolveFromCoreArrayMetadata(ArrayMetadata.CoreArrayMetadata arrayMetadata) {
        return this;
    }
}
