package it.links.scanmrz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Random;

public class AppUtil {

    public static byte checkdigit(byte[] data) throws Exception
    {
        int i;
        int tot = 0;
        int curval = 0;
        int[] weight = new int[] { 7, 3, 1 };
        for (i = 0; i < data.length; i++)
        {
            char ch = Character.toUpperCase(((char)data[i]));
            if (ch >= 'A' && ch <= 'Z')
                curval = ch - 'A' + 10;
            else
            {
                if (ch >= '0' && ch <= '9')
                    curval = ch - '0';
                else
                {
                    if (ch == '<')
                        curval = 0;
                    else
                        throw new Exception("errore nel calcolo della check digit");
                }
            }
            tot += curval * weight[i % 3];
        }
        tot = tot % 10;
        return (byte)('0' + tot);
    }


    public static byte[] getSub(byte[] bArr, int i, int i2) throws Exception {
        if (Math.signum((float) i2) < 0.0f) {
            i2 &= 255;
        }
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        return bArr2;
    }

    public static byte[] getSub(byte[] bArr, int i) throws Exception {
        byte[] bArr2 = new byte[(bArr.length - i)];
        System.arraycopy(bArr, i, bArr2, 0, bArr2.length);
        return bArr2;
    }

    public static byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static byte[] getSha1(byte[] bArr) throws Exception {
        return MessageDigest.getInstance("SHA1").digest(bArr);
    }

    public static boolean areEqual(byte[] bArr, byte[] bArr2) throws Exception {
        if (bArr.length != bArr2.length) {
            return false;
        }
        for (int i = 0; i < bArr.length; i++) {
            if (bArr[i] != bArr2[i]) {
                return false;
            }
        }
        return true;
    }

}
