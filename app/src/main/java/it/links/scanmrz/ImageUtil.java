package it.links.scanmrz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.gemalto.jp2.JP2Decoder;

public class ImageUtil {

    public static final String TAG = "m.recupero";
    public static byte[] m_pbtPhoto;
    int lenDo = 0;
    int lenHdrLength = 0;
    int lenLength = 0;
    private byte[] m_dg2;
    int offset = 0;
    int offsetTempImp2 = 0;

    public static int unsignedToBytes(byte b) throws Exception {
        return b & 255;
    }

    public ImageUtil() {
    }

    @SuppressLint({"NewApi"})
    public boolean parse(byte[] bArr) throws Exception {
        System.out.println("INIT ---> parsing DG2");
        this.m_dg2 = new byte[bArr.length];
        System.arraycopy(bArr, 0, this.m_dg2, 0, this.m_dg2.length);
        if (Byte.compare(this.m_dg2[0], (byte) 117) == 0) {
            getLengthBerTlv(this.m_dg2, this.offset);
            this.offset += this.lenLength + this.lenHdrLength;
            if (Byte.compare(this.m_dg2[this.offset + 1], Byte.MAX_VALUE) == 0 && Byte.compare(this.m_dg2[this.offset + 2], (byte) 97) == 0) {
                this.offset += 2;
                getLengthBerTlv(this.m_dg2, this.offset);
                this.offset += this.lenLength + this.lenHdrLength;
                if (Byte.compare(this.m_dg2[this.offset + 1], (byte) 2) == 0) {
                    this.offset++;
                    getLengthBerTlv(this.m_dg2, this.offset);
                    this.offset += this.lenLength + this.lenHdrLength;
                    if (this.lenDo != 1) {
                        System.out.println("Errore parsing DG2_4");
                        return false;
                    } else if (this.m_dg2[this.offset + 1] != 1) {
                        System.out.println("Errore parsing DG2_3");
                        return false;
                    } else {
                        this.offset += this.lenDo;
                        if (Byte.compare(this.m_dg2[this.offset + 1], Byte.MAX_VALUE) == 0 && Byte.compare(this.m_dg2[this.offset + 2], (byte) 96) == 0) {
                            this.offset += 2;
                            getLengthBerTlv(this.m_dg2, this.offset);
                            this.offset += this.lenLength + this.lenHdrLength;
                            this.offsetTempImp2 = this.offset + this.lenDo;
                            if (Byte.compare(this.m_dg2[this.offset + 1], (byte) -95) == 0) {
                                this.offset++;
                                getLengthBerTlv(this.m_dg2, this.offset);
                                this.offset += this.lenLength + this.lenHdrLength;
                                int i = this.offset + this.lenDo;
                                if (Byte.compare(this.m_dg2[i + 1], (byte) 95) == 0) {
                                    int i2 = i + 2;
                                    if (Byte.compare(this.m_dg2[i2], (byte) 46) == 0) {
                                        getLengthBerTlv(this.m_dg2, i2);
                                        int i3 = i2 + this.lenLength + this.lenHdrLength;
                                        if (this.lenDo != 0) {
                                            m_pbtPhoto = new byte[this.lenDo];
                                            System.arraycopy(this.m_dg2, i3 + 1, m_pbtPhoto, 0, m_pbtPhoto.length);
                                            MainActivity.mrz.setFoto(getFotoBase64());
                                            return true;
                                        }
                                        System.out.println("Errore parsing DG2_8");
                                        return false;
                                    }
                                }
                                System.out.println("Errore parsing DG2_9");
                                return false;
                            }
                            System.out.println("Errore parsing DG2_7");
                            return false;
                        }
                        System.out.println("Errore parsing DG2_6");
                        return false;
                    }
                } else {
                    System.out.println("Errore parsing DG2_5");
                    return false;
                }
            } else {
                System.out.println("Errore parsing DG2_2 ");
                return false;
            }
        } else {
            System.out.println("Errore parsing DG2_1 ");
            return false;
        }
    }

    public static Bitmap getFotoBase64() throws Exception {
        Bitmap bitmap;
        new ByteArrayOutputStream();
        try {
            byte[] image = Iso19794_5.getImage(m_pbtPhoto);
            if (Arrays.equals(new byte[]{-1, -40, -1}, AppUtil.getSub(image, 0, 3))) {
                Log.i("m.recupero", "la foto è in jpeg");
                bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            } else {
                //bitmap = JJ2000Frontend.decode(image);
                bitmap = new JP2Decoder(image).decode();
                StringBuilder sb = new StringBuilder();
                sb.append("foto dg2: ");
                sb.append(m_pbtPhoto.length);
                sb.append("  idCarta:");
                sb.append(MainActivity.mrz.getIdCarta());
                Log.i("m.recupero", sb.toString());
                if (bitmap == null) {
                    return null;
                }
            }
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Impossibile recuperare la foto");
        }
    }

    private byte[] lenToAsn1(int i) throws Exception {
        if (i > 16777215) {
            return new byte[]{-124, (byte) ((i >> 24) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
        } else if (i > 65535) {
            return new byte[]{-125, (byte) ((i >> 16) & 255), (byte) ((i >> 8) & 255), (byte) (i & 255)};
        } else if (i > 255) {
            return new byte[]{-126, (byte) ((i >> 8) & 255), (byte) (i & 255)};
        } else if (i > 127) {
            return new byte[]{-127, (byte) (i & 255)};
        } else if (i > 127) {
            return null;
        } else {
            return new byte[]{(byte) (i & 255)};
        }
    }

    @SuppressLint({"NewApi"})
    public boolean getLengthBerTlv(byte[] bArr, int i) throws Exception {
        this.lenDo = 0;
        this.lenLength = 0;
        this.lenHdrLength = 0;
        int i2 = i + 1;
        if (Byte.compare(bArr[i2], (byte) -127) == 0) {
            this.lenDo = bArr[i + 2];
            this.lenLength = 1;
            this.lenHdrLength = 1;
        } else {
            if (Byte.compare(bArr[i2], (byte) -126) == 0) {
                this.lenDo |= unsignedToBytes(bArr[i + 2]);
                for (int i3 = 3; i3 <= 3; i3++) {
                    this.lenDo <<= 8;
                    this.lenDo |= unsignedToBytes(bArr[i + i3]);
                }
                this.lenLength = 2;
                this.lenHdrLength = 1;
            } else if (Byte.compare(bArr[i2], (byte) -125) == 0) {
                this.lenDo |= bArr[i + 2];
                for (int i4 = 3; i4 <= 4; i4++) {
                    this.lenDo <<= 8;
                    this.lenDo |= unsignedToBytes(bArr[i + i4]);
                }
                this.lenLength = 3;
                this.lenHdrLength = 1;
            } else if (Byte.compare(bArr[i2], (byte) -124) == 0) {
                this.lenDo |= bArr[i + 2];
                for (int i5 = 3; i5 <= 5; i5++) {
                    this.lenDo <<= 8;
                    this.lenDo |= unsignedToBytes(bArr[i + i5]);
                }
                this.lenLength = 4;
                this.lenHdrLength = 1;
            } else {
                this.lenDo = unsignedToBytes(bArr[i2]);
                this.lenLength = 1;
                this.lenHdrLength = 0;
            }
        }
        return true;
    }
}