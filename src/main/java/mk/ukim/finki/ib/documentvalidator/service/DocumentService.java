package mk.ukim.finki.ib.documentvalidator.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentService {
    boolean validatePdfSignature(InputStream pdfInputStream) throws Exception;
    boolean validateWordSignature(InputStream wordInputStream) throws IOException, InvalidFormatException;
}
