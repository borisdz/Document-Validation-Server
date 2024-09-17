package mk.ukim.finki.ib.documentvalidator.controller;

import jakarta.servlet.http.HttpServletRequest;
import mk.ukim.finki.ib.documentvalidator.SignatureExtractor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.security.cert.X509Certificate;

@Controller
public class HelloController {

    private final SignatureExtractor signatureExtractor;

    public HelloController(SignatureExtractor signatureExtractor) {
        this.signatureExtractor = signatureExtractor;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload")
    public ModelAndView fileUpload(@RequestParam("file")MultipartFile file, HttpServletRequest request){
        ModelAndView modelAndView = new ModelAndView("upload");
        try{
            X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

            if(certs==null || certs.length==0) {
                throw new Exception("No client certificate found");
            }

            X509Certificate clientCert = certs[0];

            String mimeType = file.getContentType();
            if("application/pdf".equals(mimeType)){
                this.signatureExtractor.validatePdfSignature(file.getInputStream(),clientCert);
            }else if("application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(mimeType)) {
                boolean validity = this.signatureExtractor.validateWordSignature(file.getInputStream(),clientCert);

                if(validity){
                    System.out.println("Document is valid.");
                    return new ModelAndView("valid");
                }else{
                    System.out.println("Document is invalid");
                    return new ModelAndView("invalid");
                }
            }else {
                throw new Exception("File type is not supported. Please submit a .pdf or .docx file.");
            }
        }catch (Exception e){
            modelAndView.addObject("message","Error occurred: "+e.getMessage());
        }
        return new ModelAndView("result");
    }
}