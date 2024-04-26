package it.links.scanmrz;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import net.sf.scuba.smartcards.CardService;

import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.DG11File;
import org.jmrtd.lds.DG12File;
import org.jmrtd.lds.DG1File;
import org.jmrtd.lds.LDSFileUtil;
import org.jmrtd.lds.SODFile;
import org.spongycastle.jce.provider.BouncyCastleProvider;

import java.io.InputStream;
import java.security.Security;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.security.auth.x500.X500Principal;

import it.links.scanmrz.utilities.CheckSession;
import it.links.scanmrz.utilities.SendData;


public class nfcReader extends AppCompatActivity implements NfcAdapter.ReaderCallback {

    private NfcAdapter nfcAdapter;
    public DG1File dg1;
    public DG11File dg11;
    public DG12File dg12;
    private TextView textview;
    public static Map<Integer, byte[]> dgMap;
    public SODFile sod;
    Map<Integer, byte []> ceneri;
    public static VerificaCertificati verificaCertificati;

    private static final String TAG = "e.nfcReader";
    public ProgressBar bar;
    public TextView textInfo;
    public Button btnCertificati;

    public ImageUtil imgUtil;
    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_reader);

        CheckSession checkSession = new CheckSession(getApplicationContext());
        checkSession.check();

        textview = (TextView) findViewById(R.id.textView);
        textInfo = (TextView) findViewById(R.id.textInfo);
        btnCertificati = (Button) findViewById(R.id.button);
        /*textview.setText("Id carta: " + MainActivity.mrz.getIdCarta() + '\n' +
                "Data di nascita: " + MainActivity.mrz.getDataNascita() + '\n' +
                "Data di scadenza: " + MainActivity.mrz.getDataScadenza() + '\n');*/
        bar = (ProgressBar) findViewById(R.id.progressBar);
        bar.setVisibility(View.VISIBLE);
        textInfo.setText("WAITING NFC TAG");
        //bar.setProgress(5);

    }

    @Override
    public void onResume() {
        //sull'onResume dell'activity viene instanziato l'oggetto adapter e viene registrato l'evento
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_NFC_B |  NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK, null);
        imgUtil = new ImageUtil();
    }

    public void onClickBtn(View v)
    {
        DialogFragment newFragment = new CertificatiFragment();
        newFragment.show(getSupportFragmentManager(), "missiles");
        /*Intent intent = new Intent(this, QrCodeScannerActivity.class);
        startActivityForResult(intent, 1);*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("QRCODE","tornato");
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                String code = data.getStringExtra("QRCODE");
                Log.i("QRCODE", code);
                // Do something with the contact here (bigger example below)
            }
    }


    //metodo listener invocato quando si appoggia il documento al terminale
    @Override
    public void onTagDiscovered(Tag tag) {
        String infoTag[] = null;
        try {
            infoTag = tag.getTechList();//si recuperano le informazioni del tag
        }catch(Exception exc){exc.printStackTrace();}

        IsoDep isoDep = IsoDep.get(tag);//l'oggetto IsoDep implementa la specifica  ISO-DEP (ISO 14443-4)
        // per le operazioni di I/O verso il chip

        PassportService ps = null;
        //alcuni controlli sul flusso
        if(MainActivity.mrz != null) try {
            CardService cs = CardService.getInstance(isoDep);

            ps = new PassportService(cs);
            ps.open();
            ps.sendSelectApplet(false);

            BACKeySpec bacKey = new BACKeySpec() {
                @Override
                public String getDocumentNumber() {
                    return MainActivity.mrz.getIdCarta();
                }

                @Override
                public String getDateOfBirth() {
                    return MainActivity.mrz.getDataNascitaString();
                }

                @Override
                public String getDateOfExpiry() {
                    return MainActivity.mrz.getDataScadenzaString();
                }
            };
            bar.setProgress(5);
            textInfo.setText("INIT BAC");
            ps.doBAC(bacKey);
            bar.setProgress(15);
            textInfo.setText("BAC OK");
            readDg(ps);

        } catch (Exception e) {
            Log.i("PROVA", e.getMessage());
        } finally {
        try {
            ps.close();
            popData();
            validate();
            bar.setVisibility(View.INVISIBLE);
            textInfo.setVisibility(View.INVISIBLE);
            btnCertificati.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
        else
            Log.i("NFC","Prima devi eseguire la scansione dell' MRZ, dopo avvicinare la carta al dispositivo.");

    }

    public void readDg(PassportService ps){
        InputStream streamDati = null;
        InputStream streamHash = null;

        dgMap = new HashMap();

        try {
            streamDati = ps.getInputStream(PassportService.EF_SOD);
            sod = (SODFile) LDSFileUtil.getLDSFile(PassportService.EF_SOD, streamDati);
            ceneri = sod.getDataGroupHashes();
            bar.setProgress(20);
            textInfo.setText("READ DATA GROUPS");
            for (Integer i : ceneri.keySet()) {
                switch (i) {
                    case 1:
                        textInfo.setText("READ MRZ");
                        streamHash = ps.getInputStream(PassportService.EF_DG1);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        streamDati = ps.getInputStream(PassportService.EF_DG1);
                        dg1 = (DG1File) LDSFileUtil.getLDSFile(PassportService.EF_DG1, streamDati);
                        bar.setProgress(25);
                        break;
                    case 2:
                        textInfo.setText("READ PIC");
                        streamHash = ps.getInputStream(PassportService.EF_DG2);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        streamDati = ps.getInputStream(PassportService.EF_DG2);
                        imgUtil.parse(AppUtil.readBytes(streamDati));
                        bar.setProgress(30);
                        break;
                    case 3:
                        // Non è possibile leggere Dg3
                        bar.setProgress(35);
                        break;
                    case 4:
                        streamHash = ps.getInputStream(PassportService.EF_DG4);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(40);
                        break;
                    case 5:
                        streamHash = ps.getInputStream(PassportService.EF_DG5);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(45);
                        break;
                    case 6:
                        streamHash = ps.getInputStream(PassportService.EF_DG6);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(50);
                        break;
                    case 7:
                        streamHash = ps.getInputStream(PassportService.EF_DG7);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(55);
                        break;
                    case 8:
                        streamHash = ps.getInputStream(PassportService.EF_DG8);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(60);
                        break;
                    case 9:
                        streamHash = ps.getInputStream(PassportService.EF_DG9);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(65);
                        break;
                    case 10:
                        streamHash = ps.getInputStream(PassportService.EF_DG10);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(70);
                        break;
                    case 11:
                        textInfo.setText("READ DETAILS");
                        streamHash = ps.getInputStream(PassportService.EF_DG11);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        streamDati = ps.getInputStream(PassportService.EF_DG11);
                        dg11 = (DG11File) LDSFileUtil.getLDSFile(PassportService.EF_DG11, streamDati);
                        bar.setProgress(75);
                        break;
                    case 12:
                        streamHash = ps.getInputStream(PassportService.EF_DG12);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        streamDati = ps.getInputStream(PassportService.EF_DG12);
                        dg12 = (DG12File) LDSFileUtil.getLDSFile(PassportService.EF_DG12, streamDati);
                        bar.setProgress(80);
                        break;
                    case 13:
                        streamHash = ps.getInputStream(PassportService.EF_DG13);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(85);
                        break;
                    case 14:
                        streamHash = ps.getInputStream(PassportService.EF_DG14);
                        dgMap.put(i, AppUtil.getSha1(AppUtil.readBytes(streamHash)));
                        bar.setProgress(90);
                        break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                streamDati.close();
                streamHash.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void validate() throws Exception {
        verificaCertificati = new VerificaCertificati();
        textInfo.setText("VALIDATION");

        // verifico doc signature certificate
        X509Certificate dsc = sod.getDocSigningCertificate();
        dsc.checkValidity();
        if(!sod.checkDocSignature(dsc)) {
            Log.i(TAG, "impossibile verificare doc signature");
        } else {
            Log.i(TAG, "doc signature certificate ok");
        }

        // recupero country signing certification authority
        InputStream ims = getAssets().open("CSCA03.cer");
        X509Certificate csca = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(ims);
        csca.checkValidity();
        bar.setProgress(95);
        // verifico catena certificati
        PKIXCertPathValidatorResult pKIXCertPathValidatorResult = null;
        CertPathValidator instance2 = CertPathValidator.getInstance("PKIX");
        CertPath generateCertPath = CertificateFactory.getInstance("X509").generateCertPath(Arrays.asList(new X509Certificate[]{dsc, csca}));
        TrustAnchor trustAnchor = new TrustAnchor(csca, null);
        HashSet hashSet = new HashSet();
        hashSet.add(trustAnchor);
        PKIXParameters pKIXParameters = new PKIXParameters(hashSet);
        pKIXParameters.setRevocationEnabled(false);
        pKIXCertPathValidatorResult = (PKIXCertPathValidatorResult) instance2.validate(generateCertPath, pKIXParameters);
        X509Certificate trustedCert = pKIXCertPathValidatorResult.getTrustAnchor().getTrustedCert();
        try{
            trustedCert.checkValidity();
        } catch (Exception e) {
            Log.i(TAG,e.getMessage());
        }finally {
            verificaCertificati.setEsitoFinale(1);
        }

        //comparaDate(x509Certificate.getNotAfter(), mrz.getDataScadenza());

        // verifico hash dg
        for (Integer i : dgMap.keySet()) {
            if(AppUtil.areEqual(dgMap.get(i),ceneri.get(i))){
                Log.i(TAG,"DG " + i + " HASH OK");
            } else {
                Log.i(TAG,"ERROR: DG " + i + " HASH FAIL");
                verificaCertificati.setEsitoFinale(0);
            }
        }

        // pop verifica certificati

        verificaCertificati.setAlgoritmo(trustedCert.getSigAlgName());
        verificaCertificati.setOid(trustedCert.getSigAlgOID());
        verificaCertificati.setTipoCertificato(trustedCert.getType());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        verificaCertificati.setDataScadenza(simpleDateFormat.format(trustedCert.getNotAfter()));
        verificaCertificati.setDataEmissione(simpleDateFormat.format(trustedCert.getNotBefore()));
        verificaCertificati.setSerialNumber(trustedCert.getSerialNumber().toString());
        verificaCertificati.setNumeroVersione(String.valueOf(trustedCert.getVersion()));
        X500Principal issuerX500Principal = trustedCert.getIssuerX500Principal();
        X500Principal subjectX500Principal = trustedCert.getSubjectX500Principal();
        verificaCertificati.setIssure(issuerX500Principal.getName());
        verificaCertificati.setSubject(subjectX500Principal.getName());

        //impostare esito finale
        if (verificaCertificati.getEsitoFinale() == 0) {
            Log.i(TAG,"Esito verifica certificati negativo");
        }
        bar.setProgress(100);

    }
    public void popData() {
        MainActivity.mrz.setCognome(dg1.getMRZInfo().getPrimaryIdentifier());
        MainActivity.mrz.setNome(dg1.getMRZInfo().getSecondaryIdentifier());
        MainActivity.mrz.setSesso(dg1.getMRZInfo().getGender().toString());
        MainActivity.mrz.setCittadinanza(dg1.getMRZInfo().getNationality());
        MainActivity.mrz.setDocType(Integer.toString(dg1.getMRZInfo().getDocumentType()));

        try {
            MainActivity.mrz.setMrz(dg1.getMRZInfo().toString().replaceAll("\\s", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        MainActivity.mrz.setDataNascita(MainActivity.mrz.getDataNascita());

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(MainActivity.mrz.getFoto());
                textview.setText("Id carta: " + MainActivity.mrz.getIdCarta() + '\n' +
                "Data di nascita: " + MainActivity.mrz.getDataNascita() + '\n' +
                "Data di scadenza: " + MainActivity.mrz.getDataScadenza() + '\n');
                textview.append(
                        "Nome: " + MainActivity.mrz.getNome() + "\n" +
                                "Cognome: " + MainActivity.mrz.getCognome() + "\n" +
                                "Sesso: " + MainActivity.mrz.getSesso() + "\n" +
                                "Nazionalità: " + MainActivity.mrz.getCountry() + "\n" +
                                "DocType: " + MainActivity.mrz.getDocType() + "\n"
                );
                if (!MainActivity.isPE) {
                    MainActivity.mrz.setCf(dg11.getPersonalNumber());
                    MainActivity.mrz.setLuogoDiNascita(dg11.getPlaceOfBirth());
                    MainActivity.mrz.setResidenza(dg11.getPermanentAddress());
                    MainActivity.mrz.setAuthority(dg12.getIssuingAuthority());
                    MainActivity.mrz.setEmissione(dg12.getDateOfIssue().toString());
                    textview.append(
                            "Cod Fiscale: " + MainActivity.mrz.getCf() + "\n" +
                                    "Luogo di nascita: " + MainActivity.mrz.getLuogoDiNascita() + "\n" +
                                    "Residenza: " + MainActivity.mrz.getResidenza() + "\n" +
                                    "Issuing Authority: " + dg12.getIssuingAuthority() + "\n" +
                                    "Date of issue: " + dg12.getDateOfIssue() + "\n"
                    );

                }

                SendData sendData = new SendData(getApplicationContext());
                sendData.prova();
                //sendData.uploadImage(sendData.compress(ShowPreviewActivity.fotoRetro));
            }
        });


    }


}
