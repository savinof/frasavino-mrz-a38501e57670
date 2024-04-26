package it.links.scanmrz;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Mrz {
    private static final Pattern LTRIM = Pattern.compile("^\\s+");
    private static final Pattern RTRIM = Pattern.compile("\\s+$");
    private String CAN;
    private String annoNascita;
    private String annoScadenza;
    private String cittadinanza;
    private String cognome;
    private String country = "";
    private String dataNascita;
    private String dataNascitaString;
    private String dataScadenza;
    private String dataScadenzaString;
    private String docType = "";
    private Bitmap Foto = null;
    private Bitmap fotoFronte = null;
    private Bitmap fotoRetro = null;
    private String giornoNascita;
    private String giornoScadenza;
    private String idCarta;
    private String linea1;
    private String linea2;
    private String linea3;
    private String meseNascita;
    private String meseScadenza;
    private String mrz;
    private String nome;
    private String sesso;
    private String cf="";
    private String luogoDiNascita ="";
    private String residenza="";

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    private String authority="";

    public String getEmissione() {
        return emissione;
    }

    public void setEmissione(String emissione) {
        this.emissione = emissione;
    }

    private String emissione="";

    public String getResidenza() {
        return residenza;
    }

    public void setResidenza(List<String> residenza) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < residenza.size(); i++) {

            sb.append(residenza.get(i));

            // if not the last item
            if (i != residenza.size() - 1) {
                sb.append(", ");
            }

        }

        this.residenza = sb.toString();
    }

    public String getLuogoDiNascita() {
        return luogoDiNascita;
    }

    public void setLuogoDiNascita(List<String> luogoDiNascita) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < luogoDiNascita.size(); i++) {

            sb.append(luogoDiNascita.get(i));

            // if not the last item
            if (i != luogoDiNascita.size() - 1) {
                sb.append(", ");
            }

        }

        this.luogoDiNascita = sb.toString();
    }

    public String getCf() {
        return cf;
    }

    public void setCf(String cf) {
        this.cf = cf;
    }

    public void setAnnoScadenza(String annoScadenza) {
        this.annoScadenza = annoScadenza;
    }

    public void setCittadinanza(String cittadinanza) {
        this.cittadinanza = cittadinanza;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDataNascita(String dataNascita) {

        SimpleDateFormat formatter2=new SimpleDateFormat("dd/MM/yy");
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            this.dataNascita = dateFormat.format(formatter2.parse(dataNascita));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setDataNascitaString(String dataNascitaString) {
        this.dataNascitaString = dataNascitaString;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public void setDataScadenzaString(String dataScadenzaString) {
        this.dataScadenzaString = dataScadenzaString;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public void setGiornoNascita(String giornoNascita) {
        this.giornoNascita = giornoNascita;
    }

    public void setGiornoScadenza(String giornoScadenza) {
        this.giornoScadenza = giornoScadenza;
    }

    public void setIdCarta(String idCarta) {
        this.idCarta = idCarta;
    }

    public void setMeseNascita(String meseNascita) {
        this.meseNascita = meseNascita;
    }

    public void setMeseScadenza(String meseScadenza) {
        this.meseScadenza = meseScadenza;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setSesso(String sesso) {
        this.sesso = sesso;
    }

    public void setAnnoNascita(String annoNascita) {
        this.annoNascita = annoNascita;
    }

    public static int unsignedToBytes(byte b) throws Exception {
        return b & 255;
    }

    public static String ltrim(String str) {
        return LTRIM.matcher(str).replaceAll("");
    }

    public static String rtrim(String str) {
        return RTRIM.matcher(str).replaceAll("");
    }

    public Mrz() {
    }

    public Bitmap getFoto() {
        return Foto;
    }

    public void setFoto(Bitmap foto) {
        Foto = foto;
    }

    public void setMrz(String str) throws Exception {
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        if (checkMrz(str)) {
            this.mrz = str;
            if (str.length() == 90) {
                this.linea1 = str.substring(0, 30);
                this.linea2 = str.substring(30, 60);
                this.linea3 = str.substring(60, 90);
                this.idCarta = str.substring(5, 14);
                if (this.idCarta.length() != 9) {
                    throw new Exception("l'id della carta non è corretto!");
                }
                str.substring(30, 36);
                this.dataNascitaString = this.linea2.substring(0, 6);
                str9 = str.substring(30, 32);
                str4 = str.substring(32, 34);
                str6 = str.substring(34, 36);
                this.dataScadenzaString = this.linea2.substring(8, 14);
                str8 = str.substring(38, 40);
                String substring = str.substring(40, 42);
                str5 = str.substring(42, 44);
                str3 = str.substring(45, 48);
                str2 = str.substring(37, 38);
                char charAt = this.mrz.charAt(0);
                if (charAt == 'C' || charAt == 'I') {
                    this.docType = "CARD";
                }
                int indexOf = this.linea3.indexOf("<<");
                this.country = getCountry(this.linea2.substring(15, 18));
                this.cognome = this.linea3.substring(0, indexOf).replaceAll("<", " ");
                this.nome = this.linea3.substring(indexOf + 2, this.linea3.length()).replaceAll("<", " ");
                this.nome = rtrim(this.nome);
                str7 = substring;
            } else {
                this.linea1 = str.substring(0, 44);
                this.linea2 = str.substring(44, 88);
                this.idCarta = str.substring(44, 53).replaceAll("<", "");
                str.substring(57, 63);
                this.dataNascitaString = str.substring(57, 63);
                String substring2 = str.substring(57, 59);
                str4 = str.substring(59, 61);
                String substring3 = str.substring(61, 63);
                this.dataScadenzaString = str.substring(65, 71);
                String substring4 = str.substring(65, 67);
                str7 = str.substring(67, 69);
                String substring5 = str.substring(69, 71);
                String substring6 = str.substring(72, 75);
                str2 = str.substring(64, 65);
                int indexOf2 = this.mrz.indexOf("<<");
                int indexOf3 = this.mrz.indexOf("<<<<");
                if (this.mrz.charAt(0) == 'P') {
                    this.docType = "PASSPORT";
                }
                //this.country = getCountry(this.mrz.substring(2, 5));
                this.country = getCountry(str.substring(54, 57));
                this.cognome = this.mrz.substring(5, indexOf2).replaceAll("<", " ");
                this.nome = this.mrz.substring(indexOf2 + 2, indexOf3).replaceAll("<", " ");
                str6 = substring3;
                str9 = substring2;
                str8 = substring4;
                str5 = substring5;
                str3 = substring6;
            }
            try {
                switch (Integer.parseInt(str9)) {
                    case 0:
                        this.annoNascita = "00";
                        break;
                    case 1:
                        this.annoNascita = "01";
                        break;
                    case 2:
                        this.annoNascita = "02";
                        break;
                    case 3:
                        this.annoNascita = "03";
                        break;
                    case 4:
                        this.annoNascita = "04";
                        break;
                    case 5:
                        this.annoNascita = "05";
                        break;
                    case 6:
                        this.annoNascita = "06";
                        break;
                    case 7:
                        this.annoNascita = "07";
                        break;
                    case 8:
                        this.annoNascita = "08";
                        break;
                    case 9:
                        this.annoNascita = "09";
                        break;
                    default:
                        StringBuilder sb = new StringBuilder();
                        sb.append("");
                        sb.append(Integer.parseInt(str9));
                        this.annoNascita = sb.toString();
                        break;
                }
                try {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("");
                    sb2.append(Integer.parseInt(str4));
                    this.meseNascita = sb2.toString();
                    try {
                        StringBuilder sb3 = new StringBuilder();
                        sb3.append("");
                        sb3.append(Integer.parseInt(str6));
                        this.giornoNascita = sb3.toString();
                        try {
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("");
                            sb4.append(Integer.parseInt(str8));
                            this.annoScadenza = sb4.toString();
                            try {
                                StringBuilder sb5 = new StringBuilder();
                                sb5.append("");
                                sb5.append(Integer.parseInt(str7));
                                this.meseScadenza = sb5.toString();
                                try {
                                    StringBuilder sb6 = new StringBuilder();
                                    sb6.append("");
                                    sb6.append(Integer.parseInt(str5));
                                    this.giornoScadenza = sb6.toString();
                                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
                                    simpleDateFormat.setLenient(false);
                                    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
                                    simpleDateFormat2.setLenient(false);
                                    StringBuilder sb7 = new StringBuilder();
                                    sb7.append(this.giornoScadenza);
                                    sb7.append("/");
                                    sb7.append(this.meseScadenza);
                                    sb7.append("/");
                                    sb7.append(this.annoScadenza);
                                    this.dataScadenza = simpleDateFormat2.format(simpleDateFormat.parse(sb7.toString()));
                                    if (Integer.parseInt(this.annoNascita) <= Calendar.getInstance().get(Calendar.YEAR)) {
                                        new SimpleDateFormat("dd/MM/yy");
                                        simpleDateFormat.setLenient(false);
                                    }
                                    StringBuilder sb8 = new StringBuilder();
                                    sb8.append(this.giornoNascita);
                                    sb8.append("/");
                                    sb8.append(this.meseNascita);
                                    sb8.append("/");
                                    sb8.append(this.annoNascita);
                                    this.dataNascita = simpleDateFormat.format(simpleDateFormat.parse(sb8.toString()));
                                    this.cittadinanza = str3;
                                    this.sesso = str2;
                                } catch (Exception unused) {
                                    throw new Exception("Errore nell'acquisizione del mese della scadenza");
                                }
                            } catch (Exception unused2) {
                                throw new Exception("Errore nell'acquisizione del mese della scadenza");
                            }
                        } catch (Exception unused3) {
                            throw new Exception("Errore nell'acquisizione dell'anno di scadenza");
                        }
                    } catch (Exception unused4) {
                        throw new Exception("Errore nell'acquisizione del giorno di nascita");
                    }
                } catch (Exception unused5) {
                    throw new Exception("Errore nell'acquisizione del mese di nascita");
                }
            } catch (Exception unused6) {
                throw new Exception("Errore nell'acquisizione dell'anno di nascita");
            }
        }
    }

    public Mrz(String str, String str2, String str3) throws Exception {
        int length = 9 - str.length();
        String str4 = "";
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(str4);
                sb.append("<");
                str4 = sb.toString();
            }
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append(str4);
            this.idCarta = sb2.toString();
        } else {
            this.idCarta = str;
        }
        this.dataNascita = str2;
        this.dataScadenza = str3;
    }

    public void setNomeCognome(String str) {
        int indexOf = str.indexOf("<<");
        this.cognome = str.substring(0, indexOf).replaceAll("<", " ");
        this.nome = str.substring(indexOf + 2, str.length()).replaceAll("<", " ");
        this.nome = rtrim(this.nome);
    }

    public boolean checkMrz(String str) throws Exception {
        return str != null && (str.length() == 90 || str.length() == 88);
    }

    public String getMrz() {
        return this.mrz;
    }

    public String getLinea1() {
        return this.linea1;
    }

    public String getLinea2() {
        return this.linea2;
    }

    public String getLinea3() {
        return this.linea3;
    }

    public String getIdCarta() {
        return this.idCarta;
    }

    public String getDataNascita() {
        return this.dataNascita;
    }

    public String getAnnoNascita() {
        return this.annoNascita;
    }

    public String getMeseNascita() {
        return this.meseNascita;
    }

    public String getGiornoNascita() {
        return this.giornoNascita;
    }

    public String getAnnoScadenza() {
        return this.annoScadenza;
    }

    public String getMeseScadenza() {
        return this.meseScadenza;
    }

    public String getGiornoScadenza() {
        return this.giornoScadenza;
    }

    public String getCittadinanza() {
        return this.cittadinanza;
    }

    public String getDataNascitaString() {
        return this.dataNascitaString;
    }

    public String getSesso() {
        return this.sesso;
    }

    public String getDataScadenza() {
        return this.dataScadenza;
    }

    public String getDataScadenzaString() {
        return this.dataScadenzaString;
    }

    public String getNome() {
        return this.nome;
    }

    public String getCognome() {
        return this.cognome;
    }

    public Bitmap getFotoFronte() {
        return this.fotoFronte;
    }

    public void setFotoFronte(Bitmap foto) {
        this.fotoFronte = foto;
    }
    public Bitmap getFotoRetro() {
        return this.fotoRetro;
    }

    public void setFotoRetro(Bitmap bitmap) {
        this.fotoRetro = bitmap;
    }


    public String getDocType() {
        return this.docType;
    }

    public String getCountry() {
        return this.country;
    }

    public String getCAN() {
        return this.CAN;
    }

    public void setCAN(String str) {
        this.CAN = str;
    }

    public static String getCountry(String str) {
        HashMap hashMap = new HashMap();
        hashMap.put("AFG", "Afghanistan");
        hashMap.put("ALB", "Albania");
        hashMap.put("DZA", "Algeria");
        hashMap.put("ASM", "American Samoa");
        hashMap.put("AND", "Andorra");
        hashMap.put("AGO", "Angola");
        hashMap.put("AIA", "Anguilla");
        hashMap.put("ATA", "Antarctica");
        hashMap.put("ATG", "Antigua and Barbuda");
        hashMap.put("ARG", "Argentina");
        hashMap.put("ARM", "Armenia");
        hashMap.put("ABW", "Aruba");
        hashMap.put("AUS", "Australia");
        hashMap.put("AUT", "Austria");
        hashMap.put("AZE", "Azerbaijan");
        hashMap.put("BHS", "Bahamas");
        hashMap.put("BHR", "Bahrain");
        hashMap.put("BGD", "Bangladesh");
        hashMap.put("BRB", "Barbados");
        hashMap.put("BLR", "Belarus");
        hashMap.put("BEL", "Belgium");
        hashMap.put("BLZ", "Belize");
        hashMap.put("BEN", "Benin");
        hashMap.put("BMU", "Bermuda");
        hashMap.put("BTN", "Bhutan");
        hashMap.put("BOL", "Bolivia");
        hashMap.put("BIH", "Bosnia and Herzegovina");
        hashMap.put("BWA", "Botswana");
        hashMap.put("BVT", "Bouvet Island");
        hashMap.put("BRA", "Brazil");
        hashMap.put("IOT", "British Indian Ocean Territory");
        hashMap.put("BRN", "Brunei Darussalam");
        hashMap.put("BGR", "Bulgaria");
        hashMap.put("BFA", "Burkina Faso");
        hashMap.put("BDI", "Burundi");
        hashMap.put("KHM", "Cambodia");
        hashMap.put("CMR", "Cameroon");
        hashMap.put("CAN", "Canada");
        hashMap.put("CPV", "Cape Verde");
        hashMap.put("CYM", "Cayman Islands");
        hashMap.put("CAF", "Central African Republic");
        hashMap.put("TCD", "Chad");
        hashMap.put("CHL", "Chile");
        hashMap.put("CHN", "China");
        hashMap.put("CXR", "Christmas Island");
        hashMap.put("CCK", "Cocos (Keeling) Islands");
        hashMap.put("COL", "Colombia");
        hashMap.put("COM", "Comoros");
        hashMap.put("COG", "Congo");
        hashMap.put("COK", "Cook Islands");
        hashMap.put("CRI", "Costa Rica");
        hashMap.put("CIV", "Côte d'Ivoire");
        hashMap.put("HRV", "Croatia");
        hashMap.put("CUB", "Cuba");
        hashMap.put("CYP", "Cyprus");
        hashMap.put("CZE", "Czech Republic");
        hashMap.put("PRK", "Democratic People's Republic of Korea");
        hashMap.put("COD", "Democratic Republic of the Congo");
        hashMap.put("DNK", "Denmark");
        hashMap.put("DJI", "Djibouti");
        hashMap.put("DMA", "Dominica");
        hashMap.put("DOM", "Dominican Republic");
        hashMap.put("TMP", "East Timor");
        hashMap.put("ECU", "Ecuador");
        hashMap.put("EGY", "Egypt");
        hashMap.put("SLV", "El Salvador");
        hashMap.put("GNQ", "Equatorial Guinea");
        hashMap.put("ERI", "Eritrea");
        hashMap.put("EST", "Estonia");
        hashMap.put("ETH", "Ethiopia");
        hashMap.put("FLK", "Falkland Islands (Malvinas)");
        hashMap.put("FRO", "Faeroe Islands");
        hashMap.put("FJI", "Fiji");
        hashMap.put("FIN", "Finland");
        hashMap.put("FRA", "France");
        hashMap.put("FXX", "France, Metropolitan");
        hashMap.put("GUF", "French Guiana");
        hashMap.put("PYF", "French Polynesia");
        hashMap.put("GAB", "Gabon");
        hashMap.put("GMB", "Gambia");
        hashMap.put("GEO", "Georgia");
        hashMap.put("DEU", "Germany");
        hashMap.put("GHA", "Ghana");
        hashMap.put("GIB", "Gibraltar");
        hashMap.put("GRC", "Greece");
        hashMap.put("GRL", "Greenland");
        hashMap.put("GRD", "Grenada");
        hashMap.put("GLP", "Guadeloupe");
        hashMap.put("GUM", "Guam");
        hashMap.put("GTM", "Guatemala");
        hashMap.put("GIN", "Guinea");
        hashMap.put("GNB", "Guinea-Bissau");
        hashMap.put("GUY", "Guyana");
        hashMap.put("HTI", "Haiti");
        hashMap.put("HMD", "Heard and McDonald Islands");
        hashMap.put("VAT", "Holy See (Vatican City State)");
        hashMap.put("HND", "Honduras");
        hashMap.put("HKG", "Hong Kong");
        hashMap.put("HUN", "Hungary");
        hashMap.put("ISL", "Iceland");
        hashMap.put("IND", "India");
        hashMap.put("IDN", "Indonesia");
        hashMap.put("IRN", "Iran, Islamic Republic of");
        hashMap.put("IRQ", "Iraq");
        hashMap.put("IRL", "Ireland");
        hashMap.put("ISR", "Israel");
        hashMap.put("ITA", "Italy");
        hashMap.put("JAM", "Jamaica");
        hashMap.put("JPN", "Japan");
        hashMap.put("JOR", "Jordan");
        hashMap.put("KAZ", "Kazakhstan");
        hashMap.put("KEN", "Kenya");
        hashMap.put("KIR", "Kiribati");
        hashMap.put("KWT", "Kuwait");
        hashMap.put("KGZ", "Kyrgyzstan");
        hashMap.put("LAO", "Lao People's Democratic Republic");
        hashMap.put("LVA", "Latvia");
        hashMap.put("LBN", "Lebanon");
        hashMap.put("LSO", "Lesotho");
        hashMap.put("LBR", "Liberia");
        hashMap.put("LBY", "Libyan Arab Jamahiriya");
        hashMap.put("LIE", "Liechtenstein");
        hashMap.put("LTU", "Lithuania");
        hashMap.put("LUX", "Luxembourg");
        hashMap.put("MDG", "Madagascar");
        hashMap.put("MWI", "Malawi");
        hashMap.put("MYS", "Malaysia");
        hashMap.put("MDV", "Maldives");
        hashMap.put("MLI", "Mali");
        hashMap.put("MLT", "Malta");
        hashMap.put("MHL", "Marshall Islands");
        hashMap.put("MTQ", "Martinique");
        hashMap.put("MRT", "Mauritania");
        hashMap.put("MUS", "Mauritius");
        hashMap.put("MYT", "Mayotte");
        hashMap.put("MEX", "Mexico");
        hashMap.put("FSM", "Micronesia, Federated States of");
        hashMap.put("MCO", "Monaco");
        hashMap.put("MNG", "Mongolia");
        hashMap.put("MSR", "Montserrat");
        hashMap.put("MAR", "Morocco");
        hashMap.put("MOZ", "Mozambique");
        hashMap.put("MMR", "Myanmar");
        hashMap.put("NAM", "Namibia");
        hashMap.put("NRU", "Nauru");
        hashMap.put("NPL", "Nepal");
        hashMap.put("NLD", "Netherlands, Kingdom of the");
        hashMap.put("ANT", "Netherlands Antilles");
        hashMap.put("NTZ", "Neutral Zone");
        hashMap.put("NCL", "New Caledonia");
        hashMap.put("NZL", "New Zealand");
        hashMap.put("NIC", "Nicaragua");
        hashMap.put("NER", "Niger");
        hashMap.put("NGA", "Nigeria");
        hashMap.put("NIU", "Niue");
        hashMap.put("NFK", "Norfolk Island");
        hashMap.put("MNP", "Northern Mariana Islands");
        hashMap.put("NOR", "Norway");
        hashMap.put("OMN", "Oman");
        hashMap.put("PAK", "Pakistan");
        hashMap.put("PLW", "Palau");
        hashMap.put("PAN", "Panama");
        hashMap.put("PNG", "Papua New Guinea");
        hashMap.put("PRY", "Paraguay");
        hashMap.put("PER", "Peru");
        hashMap.put("PHL", "Philippines");
        hashMap.put("PCN", "Pitcairn");
        hashMap.put("POL", "Poland");
        hashMap.put("PRT", "Portugal");
        hashMap.put("PRI", "Puerto Rico");
        hashMap.put("QAT", "Qatar");
        hashMap.put("KOR", "Republic of Korea");
        hashMap.put("MDA", "Republic of Moldova");
        hashMap.put("REU", "Réunion");
        hashMap.put("ROU", "Romania");
        hashMap.put("RUS", "Russian Federation");
        hashMap.put("RWA", "Rwanda");
        hashMap.put("SHN", "Saint Helena");
        hashMap.put("KNA", "Saint Kitts and Nevis");
        hashMap.put("LCA", "Saint Lucia");
        hashMap.put("SPM", "Saint Pierre and Miquelon");
        hashMap.put("VCT", "Saint Vincent and the Grenadines");
        hashMap.put("WSM", "Samoa");
        hashMap.put("SMR", "San Marino");
        hashMap.put("STP", "Sao Tome and Principe");
        hashMap.put("SAU", "Saudi Arabia");
        hashMap.put("SEN", "Senegal");
        hashMap.put("SYC", "Seychelles");
        hashMap.put("SLE", "Sierra Leone");
        hashMap.put("SGP", "Singapore");
        hashMap.put("SVK", "Slovakia");
        hashMap.put("SVN", "Slovenia");
        hashMap.put("SLB", "Solomon Islands");
        hashMap.put("SOM", "Somalia");
        hashMap.put("ZAF", "South Africa");
        hashMap.put("SGS", "South Georgia and the South Sandwich Island");
        hashMap.put("ESP", "Spain");
        hashMap.put("LKA", "Sri Lanka");
        hashMap.put("SDN", "Sudan");
        hashMap.put("SUR", "Suriname");
        hashMap.put("SJM", "Svalbard and Jan Mayen Islands");
        hashMap.put("SWZ", "Swaziland");
        hashMap.put("SWE", "Sweden");
        hashMap.put("CHE", "Switzerland");
        hashMap.put("SYR", "Syrian Arab Republic");
        hashMap.put("TWN", "Taiwan Province of China");
        hashMap.put("TJK", "Tajikistan");
        hashMap.put("THA", "Thailand");
        hashMap.put("MKD", "The former Yugoslav Republic of Macedonia");
        hashMap.put("TGO", "Togo");
        hashMap.put("TKL", "Tokelau");
        hashMap.put("TON", "Tonga");
        hashMap.put("TTO", "Trinidad and Tobago");
        hashMap.put("TUN", "Tunisia");
        hashMap.put("TUR", "Turkey");
        hashMap.put("TKM", "Turkmenistan");
        hashMap.put("TCA", "Turks and Caicos Islands");
        hashMap.put("TUV", "Tuvalu");
        hashMap.put("UGA", "Uganda");
        hashMap.put("UKR", "Ukraine");
        hashMap.put("ARE", "United Arab Emirates");
        hashMap.put("GBR", "United Kingdom");
        hashMap.put("GBD", "United Kingdom");
        hashMap.put("GBN", "United Kingdom");
        hashMap.put("GBO", "United Kingdom");
        hashMap.put("GBP", "United Kingdom");
        hashMap.put("GBS", "United Kingdom");
        hashMap.put("TZA", "United Republic of Tanzania");
        hashMap.put("USA", "United States of America");
        hashMap.put("UMI", "United States of America Minor Outlying Islands");
        hashMap.put("URY", "Uruguay");
        hashMap.put("UZB", "Uzbekistan");
        hashMap.put("VUT", "Vanuatu");
        hashMap.put("VEN", "Venezuela");
        hashMap.put("VNM", "Viet Nam");
        hashMap.put("VGB", "Virgin Islands (Great Britian)");
        hashMap.put("VIR", "Virgin Islands (United States)");
        hashMap.put("WLF", "Wallis and Futuna Islands");
        hashMap.put("ESH", "Western Sahara");
        hashMap.put("YEM", "Yemen");
        hashMap.put("ZAR", "Zaire");
        hashMap.put("ZMB", "Zambia");
        hashMap.put("ZWE", "Zimbabwe");
        hashMap.put("UNO", "United Nations Organization");
        hashMap.put("UNA", "United Nations Specialized agency official");
        hashMap.put("XXA", "Stateless");
        hashMap.put("XXB", "Refugee");
        hashMap.put("XXC", "Refugee (non-convention)");
        hashMap.put("XXX", "Unspecified / Unknown");
        return hashMap.get(str).toString().toUpperCase();
    }
}
