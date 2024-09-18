package mk.ukim.finki.ib.documentvalidator.service.impl;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.PdfPKCS7;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import mk.ukim.finki.ib.documentvalidator.service.DocumentService;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Store;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {
    public boolean validatePdfSignature(InputStream pdfInputStream) throws Exception {

        PdfReader reader = new PdfReader(pdfInputStream);
        PdfDocument pdfDocument = new PdfDocument(reader);
        SignatureUtil signatureUtil = new SignatureUtil(pdfDocument);

        List<String> signatureNames = signatureUtil.getSignatureNames();

        if (signatureNames.isEmpty())
            return false;

        for (String name : signatureNames) {
            PdfSignature pdfSignature = signatureUtil.getSignature(name);
            PdfPKCS7 pkcs7Signature = signatureUtil.readSignatureData(name);

            return pkcs7Signature.verifySignatureIntegrityAndAuthenticity();

        }
        return false;
    }

    public boolean validateWordSignature(InputStream wordInputStream) throws IOException, InvalidFormatException {
//        try(XWPFDocument document = new XWPFDocument(wordInputStream)) {
//            ZipFile zipFile = new ZipFile("word/document.xml");
//            ZipEntry entry = zipFile.getEntry("word/document.xml");
//            if(entry!=null){
//                InputStream xmlStream = zipFile.getInputStream(entry);
//                String xmlContent = new String(xmlStream.readAllBytes());
//
//                byte[] certificateBytes = extractCertificateBytesFromXML(xmlContent);
//
//                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
//                X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(certificateBytes));
//
//                cert.checkValidity();
//                return true;
//            }
//
////            OPCPackage pkg = OPCPackage.open(wordInputStream);
////
////            SignatureInfo signatureInfo = new SignatureInfo();
////            signatureInfo.setOpcPackage(pkg);
////
////            return signatureInfo.verifySignature();
//        }catch (CertificateExpiredException | CertificateNotYetValidException e){
//            return false;
//        }catch(CertificateException e){
//            System.out.println("Error while generating the certificate. Might not be signed at all");
//            return false;
//        }
//        return false;
        try(XWPFDocument document = new XWPFDocument(wordInputStream)){
            PackagePart signaturePart = findSignaturePart(document);
            if(signaturePart==null){
                return false;
            }

            try (InputStream signatureInputStream = signaturePart.getInputStream()){
                CMSSignedData signedData = new CMSSignedData(signatureInputStream);

                Store certs = signedData.getCertificates();
                SignerInformation signer = (SignerInformation) signedData.getSignerInfos().getSigners().iterator().next();

                X509Certificate cert = extractCertificate(certs, signer);

                return signer.verify(new JcaSimpleSignerInfoVerifierBuilder()
                        .setProvider("BC")
                        .build(cert));
            } catch (CMSException | OperatorCreationException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private X509Certificate extractCertificate(Store certs, SignerInformation signer){
        Collection<X509Certificate> certificateCollection = (Collection<X509Certificate>) certs.getMatches(signer.getSID());
        if(certificateCollection.isEmpty()){
            throw new IllegalArgumentException("No matching certificate found for signer.");
        }

        return certificateCollection.iterator().next();
    }
    private PackagePart findSignaturePart(POIXMLDocument document) throws InvalidFormatException {
        for(PackagePart part : document.getPackage().getParts()){
            if(part.getContentType().equals("application/vnd.openxmlformats-package.digital-signature-origin"))
                return part;
        }
        return null;
    }

    private static byte[] extractCertificateBytesFromXML(String xmlContent){
        int start = xmlContent.indexOf("<Certificate>");
        int end = xmlContent.indexOf("</Certificate>");
        if(start!=-1 && end!=-1){
            return xmlContent.substring(start + "<Certificate>".length(), end).getBytes();
        }
        return new byte[0];
    }
}
