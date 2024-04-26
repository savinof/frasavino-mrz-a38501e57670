# Doc Reader

## App Usage
L'applicazione android effettua la lettura di documenti elettronici di viaggio come passaporto o carta d'identità elettronica.

Una volta selezionato il documento dalla schermata iniziale, si apre la preview della fotocamera (orientamento landscape) all'interno della quale è presente un overlay rettangolare, delle dimensioni adatte rispetto al tipo di documento selezionato.

Facendo touch sulla preview l'applicazione scatta una foto e la visualizza. 

Premendo Cancel è possibile rifare lo scatto.

Premendo Ok si procede al passo successivo. 

Nel caso di Passaporto questo prima foto è salta, dato che la parte interessata del passaporto è solo una.

Nella fase successiva si riapre la preview della fotocamera per scattare la foto all parte posteriore della carta d'identità o del passaporto.

Inquadrando opportunamente il documento e facendo attenzione che la luce sia omogenea, l'app legge il codice MRZ presente in basso.

Una volta letto il codice scatta la foto automaticamente.

Successivamente avvicinando il documento al sensore NFC effettua la lettura dei dati e li visualizza.

## Dev Usage

Per lo sviluppo è necessario scaricare la versione opportuna di opencv Android SDK (opencv-4.1.1-android-sdk) 
indicare il path relativo nell'app gradle.

In assets è presente il traineddata del font ocrb per tesseract.

In assets è presente l' Italian Country Signing Certification Authority (CSCA) necessario ad effettuare la verifica del documento.

