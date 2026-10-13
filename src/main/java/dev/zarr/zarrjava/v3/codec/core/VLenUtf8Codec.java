package dev.zarr.zarrjava.v3.codec.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dev.zarr.zarrjava.ZarrException;
import dev.zarr.zarrjava.core.ArrayMetadata.CoreArrayMetadata;
import dev.zarr.zarrjava.core.codec.ArrayBytesCodec;
import dev.zarr.zarrjava.utils.Utils;
import dev.zarr.zarrjava.utils.VLenUtf8;
import dev.zarr.zarrjava.v3.codec.Codec;
import ucar.ma2.Array;
import ucar.ma2.DataType;

import javax.annotation.Nullable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;

/**
 * The zarr-python 3 {@code vlen-utf8} array-to-bytes codec for {@code string} arrays.
 */
public class VLenUtf8Codec extends ArrayBytesCodec implements Codec {

    @JsonIgnore
    public final String name = "vlen-utf8";
    public final Map<String, Object> configuration;

    @JsonCreator
    public VLenUtf8Codec(@Nullable @JsonProperty(value = "configuration") Map<String, Object> configuration) {
        this.configuration = configuration == null ? Collections.emptyMap() : configuration;
    }

    public VLenUtf8Codec() {
        this(null);
    }

    @Override
    public Array decode(ByteBuffer chunkBytes) throws ZarrException {
        String[] strings = VLenUtf8.decode(Utils.toArray(chunkBytes), arrayMetadata.chunkSize());
        return Array.makeObjectArray(DataType.OBJECT, String.class, arrayMetadata.chunkShape, strings);
    }

    @Override
    public ByteBuffer encode(Array chunkArray) throws ZarrException {
        return ByteBuffer.wrap(VLenUtf8.encode(VLenUtf8.toStrings(chunkArray)));
    }

    @Override
    public long computeEncodedSize(long inputByteLength, CoreArrayMetadata arrayMetadata) throws ZarrException {
        throw new ZarrException("The encoded size of vlen-utf8 chunks is not known in advance.");
    }
}
