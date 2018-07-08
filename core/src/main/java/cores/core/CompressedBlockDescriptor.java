package cores.core;

import java.io.IOException;

public class CompressedBlockDescriptor extends BlockDescriptor {
    int rowCount;
    int uncompressedSize;
    int lengthUnion;
    int lengthOffset;
    int lengthPayload;

    public CompressedBlockDescriptor() {

    }

    public CompressedBlockDescriptor(int rowCount, int uncompressedSize, int lengthOffset, int lengthPayload) {
        this(rowCount, uncompressedSize, 0, lengthOffset, lengthPayload);
    }

    public CompressedBlockDescriptor(int rowCount, int uncompressedSize, int lengthUnion, int lengthOffset,
            int lengthPayload) {
        this.rowCount = rowCount;
        this.uncompressedSize = uncompressedSize;
        this.lengthUnion = lengthUnion;
        this.lengthOffset = lengthOffset;
        this.lengthPayload = lengthPayload;
    }

    public int getUncompressedSize() {
        return uncompressedSize;
    }

    public int getCompressedSize() {
        return lengthUnion + lengthOffset + lengthPayload;
    }

    public void writeTo(OutputBuffer out) throws IOException {
        out.writeFixed32(rowCount);
        out.writeFixed32(uncompressedSize);
        out.writeFixed32(lengthUnion);
        out.writeFixed32(lengthOffset);
        out.writeFixed32(lengthPayload);
    }

    public static CompressedBlockDescriptor read(InputBuffer in) throws IOException {
        CompressedBlockDescriptor result = new CompressedBlockDescriptor();
        result.rowCount = in.readFixed32();
        result.uncompressedSize = in.readFixed32();
        result.lengthUnion = in.readFixed32();
        result.lengthOffset = in.readFixed32();
        result.lengthPayload = in.readFixed32();
        return result;
    }

}
