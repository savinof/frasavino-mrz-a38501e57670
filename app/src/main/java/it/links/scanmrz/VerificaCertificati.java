package it.links.scanmrz;

public class VerificaCertificati {
    private String algoritmo;
    private String dataEmissione;
    private String dataScadenza;
    private int esitoFinale = 0;
    private String issure;
    private String numeroVersione;
    private String oid;
    private String serialNumber;
    private String subject;
    private String tipoCertificato;

    public String getAlgoritmo() {
        return algoritmo;
    }

    public void setAlgoritmo(String algoritmo) {
        this.algoritmo = algoritmo;
    }

    public String getDataEmissione() {
        return dataEmissione;
    }

    public void setDataEmissione(String dataEmissione) {
        this.dataEmissione = dataEmissione;
    }

    public String getDataScadenza() {
        return dataScadenza;
    }

    public void setDataScadenza(String dataScadenza) {
        this.dataScadenza = dataScadenza;
    }

    public int getEsitoFinale() {
        return esitoFinale;
    }

    public void setEsitoFinale(int esitoFinale) {
        this.esitoFinale = esitoFinale;
    }

    public String getIssure() {
        return issure;
    }

    public void setIssure(String issure) {
        this.issure = issure;
    }

    public String getNumeroVersione() {
        return numeroVersione;
    }

    public void setNumeroVersione(String numeroVersione) {
        this.numeroVersione = numeroVersione;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTipoCertificato() {
        return tipoCertificato;
    }

    public void setTipoCertificato(String tipoCertificato) {
        this.tipoCertificato = tipoCertificato;
    }
    public String toString() {
        String str = "\n";
        if (this.issure != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(str);
            sb.append("Emittente: ");
            sb.append(this.issure);
            sb.append("\n");
            str = sb.toString();
        }
        if (this.subject != null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(str);
            sb2.append("\nSoggetto: ");
            sb2.append(this.subject);
            sb2.append("\n");
            str = sb2.toString();
        }
        if (this.serialNumber != null) {
            StringBuilder sb3 = new StringBuilder();
            sb3.append(str);
            sb3.append("\nNumero seriale: ");
            sb3.append(this.serialNumber);
            sb3.append("\n");
            str = sb3.toString();
        }
        if (this.dataEmissione != null) {
            StringBuilder sb4 = new StringBuilder();
            sb4.append(str);
            sb4.append("\nData di emissione: ");
            sb4.append(this.dataEmissione);
            sb4.append("\n");
            str = sb4.toString();
        }
        if (this.dataScadenza != null) {
            StringBuilder sb5 = new StringBuilder();
            sb5.append(str);
            sb5.append("\nData di scadenza: ");
            sb5.append(this.dataScadenza);
            sb5.append("\n");
            str = sb5.toString();
        }
        if (this.numeroVersione != null) {
            StringBuilder sb6 = new StringBuilder();
            sb6.append(str);
            sb6.append("\nNumero versione: ");
            sb6.append(this.numeroVersione);
            sb6.append("\n");
            str = sb6.toString();
        }
        if (this.tipoCertificato != null) {
            StringBuilder sb7 = new StringBuilder();
            sb7.append(str);
            sb7.append("\nTipo di certificato: ");
            sb7.append(this.tipoCertificato);
            sb7.append("\n");
            str = sb7.toString();
        }
        if (this.oid != null) {
            StringBuilder sb8 = new StringBuilder();
            sb8.append(str);
            sb8.append("\nOid: ");
            sb8.append(this.oid);
            sb8.append("\n");
            str = sb8.toString();
        }
        if (this.algoritmo != null) {
            StringBuilder sb9 = new StringBuilder();
            sb9.append(str);
            sb9.append("\nAlgoritmo: ");
            sb9.append(this.algoritmo);
            sb9.append("\n");
            str = sb9.toString();
        }
        return str;
    }
}
