package it.links.scanmrz;

import java.io.ByteArrayInputStream;

public class BinaryReader {
    ByteArrayInputStream ms = null;
    byte[] r16 = new byte[2];
    byte[] r32 = new byte[4];
    byte[] r48 = new byte[6];

    public static int unsignedToBytes(byte b) throws Exception {
        return b & 255;
    }

    public BinaryReader(ByteArrayInputStream byteArrayInputStream) {
        this.ms = byteArrayInputStream;
    }

    public int read() throws Exception {
        return this.ms.read();
    }

    public int read(byte[] bArr, int i, int i2) throws Exception {
        return this.ms.read(bArr, i, i2);
    }

    public int readInt48() throws Exception {
        this.ms.read(this.r48, 0, 6);
        return (this.r48[2] << 24) | (this.r48[3] << 16) | (this.r48[4] << 8) | this.r48[5];
    }

    public int readInt32() throws Exception {
        this.ms.read(this.r32, 0, 4);
        return (unsignedToBytes(this.r32[0]) << 24) | (unsignedToBytes(this.r32[1]) << 16) | (unsignedToBytes(this.r32[2]) << 8) | unsignedToBytes(this.r32[3]);
    }

    public short readInt16() throws Exception {
        this.ms.read(this.r16, 0, 2);
        return (short) ((this.r16[0] << 8) | this.r16[1]);
    }
}
