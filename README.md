# Document Reader

## Application Usage
The Android application is designed to read electronic travel documents, such as passports or electronic identification cards.

Upon selecting a document from the initial screen, a camera preview opens in landscape orientation. Within the preview, a rectangular overlay appears, sized appropriately for the chosen document type.

Tapping the preview prompts the application to capture a photograph and display it. The user can press the "Cancel" button to retake the photo or "Ok" to proceed to the next step. For passports, this initial photo step is skipped as only one side needs to be captured.

In the subsequent phase, the camera preview reopens to capture the back side of the identification card or passport. By properly framing the document and ensuring even lighting, the app can read the Machine Readable Zone (MRZ) code located at the bottom.

Once the MRZ code is successfully read, the application automatically captures a photo.

Following this, the user can bring the document closer to the NFC sensor, allowing the app to read and display the associated data.

## Development Usage

For development purposes, it is necessary to download the appropriate version of the OpenCV Android SDK (opencv-4.1.1-android-sdk) and specify the relative path in the app's Gradle configuration.

The "assets" folder contains the trained data for the ocrb font, required by Tesseract OCR engine.

Additionally, the "assets" folder includes the Italian Country Signing Certification Authority (CSCA), which is necessary for verifying the authenticity of the document.