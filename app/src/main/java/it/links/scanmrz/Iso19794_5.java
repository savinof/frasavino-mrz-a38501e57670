package it.links.scanmrz;

import java.io.ByteArrayInputStream;

public class Iso19794_5 {
    public static byte[] getImage(byte[] bArr) throws Exception {
        BinaryReader binaryReader = new BinaryReader(new ByteArrayInputStream(bArr));
        binaryReader.readInt32();
        binaryReader.readInt32();
        binaryReader.readInt32();
        binaryReader.readInt16();
        int readInt32 = binaryReader.readInt32();
        binaryReader.readInt16();
        binaryReader.read();
        binaryReader.read();
        binaryReader.read();
        binaryReader.read(new byte[3], 0, 3);
        binaryReader.readInt16();
        binaryReader.read(new byte[3], 0, 3);
        binaryReader.read(new byte[3], 0, 3);
        binaryReader.read();
        binaryReader.read();
        binaryReader.readInt16();
        binaryReader.readInt16();
        binaryReader.read();
        binaryReader.read();
        binaryReader.readInt16();
        binaryReader.readInt16();
        byte[] bArr2 = new byte[(readInt32 - 32)];
        binaryReader.read(bArr2, 0, bArr2.length);
        return bArr2;
    }
}
