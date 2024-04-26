package it.links.scanmrz;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;
//import it.ipzs.nfccardreader.beanAndUtils.AppUtil;
//import it.ipzs.nfccardreader.beanAndUtils.Mrz;
//import it.ipzs.nfccardreader.logica.Eac;
import java.io.File;

public class AnalisiOcr {
    private static final String TAG = "m.recupero";
    public static TessBaseAPI baseApi = null;
    private static boolean checkCompleto = true;
    private static byte checkDigitComplessivoCalcolato = 0;
    private static char checkDigitComplessivoLetto = '\u0000';
    private static byte checkDigitDataNCalcolato = 0;
    private static char checkDigitDataNLetto = '\u0000';
    private static byte checkDigitDataScadCalcolato = 0;
    private static char checkDigitDataScadLetto = '\u0000';
    private static byte checkDigitPersonalNumberCalcolato = 0;
    private static char checkDigitPersonalNumberleLetto = '\u0000';
    private static byte checkDigitPnCalcolato = 0;
    private static char checkDigitPnLetto = '\u0000';
    static boolean[] checkDigitTrovati = {false, false, false};
    static boolean containsChar = false;
    private static String dataN = null;
    private static String dataS = null;
    private static String idCarta = null;
    private static boolean isPE = false;
    static Mrz mrz = null;
    static Mrz[] mrzConCD = {null, null, null};
    private static String personalNumber = null;
    private static String personalNumber1 = null;
    private static String personalNumber2 = null;
    private static boolean personalNumberIsPresent = false;
    static String testoTradottoLinea = "";
    static boolean trovato = false;
    //
    @SuppressLint({"NewApi"})
    static void setTestoTradotto(String str) {
        if (str.length() > 2) {
            containsChar = true;
            try {
                if (!trovato && str != null) {
                    str.trim();
                    String replaceAll = str.replaceAll("\\s", "");
                    int length = replaceAll.length();
                    testoTradottoLinea = replaceAll;
                    if (length == 88) {
                        isPE = true;
                        idCarta = replaceAll.substring(44, 53);
                        dataN = replaceAll.substring(57, 63);
                        dataS = replaceAll.substring(65, 71);
                        personalNumber = replaceAll.substring(72, 86);
                        checkDigitPnLetto = testoTradottoLinea.charAt(53);
                        checkDigitDataNLetto = testoTradottoLinea.charAt(63);
                        checkDigitDataScadLetto = testoTradottoLinea.charAt(71);
                        checkDigitPersonalNumberleLetto = testoTradottoLinea.charAt(86);
                        checkDigitComplessivoLetto = testoTradottoLinea.charAt(87);
                    } else if (length == 90) {
                        isPE = false;
                        idCarta = replaceAll.substring(5, 14);
                        dataN = replaceAll.substring(30, 36);
                        dataS = replaceAll.substring(38, 44);
                        personalNumber = "";
                        personalNumber1 = replaceAll.substring(15, 30);
                        personalNumber2 = replaceAll.substring(48, 59);
                        checkDigitPnLetto = testoTradottoLinea.charAt(14);
                        checkDigitDataNLetto = testoTradottoLinea.charAt(36);
                        checkDigitDataScadLetto = testoTradottoLinea.charAt(44);
                        checkDigitPersonalNumberleLetto = testoTradottoLinea.charAt(59);
                        checkDigitComplessivoLetto = testoTradottoLinea.charAt(59);
                    } else {
                        return;
                    }
                    mrz = new Mrz(idCarta, dataN, dataS);
                    checkDigitPnCalcolato = AppUtil.checkdigit(mrz.getIdCarta().getBytes());
                    checkDigitDataNCalcolato = AppUtil.checkdigit(mrz.getDataNascita().getBytes());
                    checkDigitDataScadCalcolato = AppUtil.checkdigit(mrz.getDataScadenza().getBytes());
                    checkDigitPersonalNumberCalcolato = AppUtil.checkdigit(personalNumber.getBytes());
                    if (isPE && !personalNumber.equals("<<<<<<<<<<<<<<")) {
                        checkDigitPersonalNumberCalcolato = AppUtil.checkdigit(personalNumber.getBytes());
                        personalNumberIsPresent = true;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(mrz.getIdCarta());
                    sb.append(checkDigitPnLetto);
                    sb.append(mrz.getDataNascita());
                    sb.append(checkDigitDataNLetto);
                    sb.append(mrz.getDataScadenza());
                    sb.append(checkDigitDataScadLetto);
                    String sb2 = sb.toString();
                    if (personalNumberIsPresent) {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append(sb2);
                        sb3.append(personalNumber);
                        sb3.append(checkDigitPersonalNumberleLetto);
                        sb2 = sb3.toString();
                    }
                    if (!isPE) {
                        StringBuilder sb4 = new StringBuilder();
                        sb4.append(mrz.getIdCarta());
                        sb4.append(checkDigitPnLetto);
                        sb4.append(personalNumber1);
                        sb4.append(mrz.getDataNascita());
                        sb4.append(checkDigitDataNLetto);
                        sb4.append(mrz.getDataScadenza());
                        sb4.append(checkDigitDataScadLetto);
                        sb4.append(personalNumber2);
                        sb2 = sb4.toString();
                    }
                    checkDigitComplessivoCalcolato = AppUtil.checkdigit(sb2.getBytes());
                    if (checkCompleto && checkDigitPnLetto == checkDigitPnCalcolato &&
                            checkDigitDataNLetto == checkDigitDataNCalcolato &&
                            checkDigitDataScadLetto == checkDigitDataScadCalcolato &&
                            checkDigitComplessivoLetto == checkDigitComplessivoCalcolato) {
                        trovato = true;
                        MainActivity.mrz = mrz;
                        MainActivity.mrz.setDataNascitaString(MainActivity.mrz.getDataNascita());
                        MainActivity.mrz.setDataScadenzaString(MainActivity.mrz.getDataScadenza());
                    } else if (checkDigitPnLetto == checkDigitPnCalcolato &&
                            checkDigitDataNLetto == checkDigitDataNCalcolato &&
                            checkDigitDataScadLetto == checkDigitDataScadCalcolato) {
                        trovato = true;
                        MainActivity.mrz = mrz;
                        MainActivity.mrz.setDataNascitaString(MainActivity.mrz.getDataNascita());
                        MainActivity.mrz.setDataScadenzaString(MainActivity.mrz.getDataScadenza());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                StringBuilder sb5 = new StringBuilder();
                sb5.append("eccezione catturata: ");
                sb5.append(e.getMessage());
                Log.i("m.recupero", sb5.toString());
            }
        } else {
            containsChar = false;
        }
    }

    public static void endOrc() throws Exception {
        if (baseApi != null) {
            baseApi.end();
        }
    }

    public static void initOrc() throws Exception {
        File file = new File(MainActivity.fDirCache, "tesseract");
        baseApi = new TessBaseAPI();
        baseApi.init(file.getAbsolutePath(), "ocrb");
        baseApi.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "<0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }
}
