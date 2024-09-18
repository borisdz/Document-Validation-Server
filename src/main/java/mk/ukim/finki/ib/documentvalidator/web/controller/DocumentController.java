package mk.ukim.finki.ib.documentvalidator.web.controller;

import mk.ukim.finki.ib.documentvalidator.service.DocumentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.logging.Logger;

@Controller
public class DocumentController {

    private final DocumentService documentService;
    private static final Logger logger = Logger.getLogger(DocumentController.class.getName());

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws Exception {
//            HttpServletRequest originalRequest = (HttpServletRequest) unwrapRequest(request);
//
//            X509Certificate[] certs = (X509Certificate[]) originalRequest.getAttribute("javax.servlet.request.X509Certificate");
//
//            if(certs==null || certs.length==0) {
//                logger.warning("No client certificate found!");
//                throw new Exception("No client certificate found");
//            }
//
//
//            X509Certificate clientCert = certs[0];
//            logger.info("Client certificate: "+ clientCert.getSubjectX500Principal().getName());
        String mimeType = file.getContentType();
        if ("application/pdf".equals(mimeType)) {
            boolean validity = this.documentService.validatePdfSignature(file.getInputStream());
            if (validity) {
                System.out.println("Document is valid.");
                return ("valid");
            } else {
                System.out.println("Document is invalid or not signed at all.");
                return ("invalid");
            }
        } else if ("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)) {
            boolean validity = this.documentService.validateWordSignature(file.getInputStream());

            if (validity) {
                System.out.println("Document is valid.");
                return ("valid");
            } else {
                System.out.println("Document is invalid");
                return ("invalid");
            }
        } else {
            throw new Exception("File type is not supported. Please submit a .pdf or .docx file.");
        }
    }
}