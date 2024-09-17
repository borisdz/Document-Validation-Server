package mk.ukim.finki.ib.documentvalidator;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;

@Service
public class SignatureExtractor {
    public boolean validatePdfSignature(InputStream pdfInputStream, X509Certificate clientCert) throws Exception {

        PdfReader reader = new PdfReader(pdfInputStream);
        PdfDocument pdfDocument = new PdfDocument(reader);
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        List<String> signatureNames = signatureUtil.getSignatureNames();

        if (signatureNames.isEmpty())
            throw new Exception("No signature found in the PDF document");

        for (String name : signatureNames) {
            PdfSignature pdfSignature = signatureUtil.getSignature(name);
            PdfPKCS7 pkcs7Signature = signatureUtil.readSignatureData(name);
            boolean isValid = pkcs7Signature.verifySignatureIntegrityAndAuthenticity();

            if (isValid) {
                X509Certificate signingCert = (X509Certificate) pkcs7Signature.getSigningCertificate();
                return clientCert.equals(signingCert);
            }
        }

        return false;
    }

    public boolean validateWordSignature(InputStream wordInputStream, X509Certificate clientCert) throws IOException, InvalidFormatException {
//        OPCPackage pkg = OPCPackage.open(wordInputStream);
//        XWPFDocument document = new XWPFDocument(pkg);
//
//        //SignatureConfig signatureConfig = new SignatureConfig();
//        SignatureInfo signatureInfo = new SignatureInfo();
//        signatureInfo.setOpcPackage(pkg);
//
//        boolean isValid = signatureInfo.verifySignature();
//
//        if (isValid) {
//            X509Certificate signingCert = signatureInfo.getSignatureConfig().getSigningCertificateChain().getFirst();
//            return clientCert.equals(signingCert);
//        }
        return false;
    }
}
