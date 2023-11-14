package sg.hsdd.aplus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.aspectj.apache.bcel.classfile.Field;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sg.hsdd.aplus.controller.dto.OptionDTO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService{


    @Override
    public String extractText(MultipartFile multipartFile, OptionDTO optionDTO) throws IOException {
//        File file = new File("/Users/hong/" + multipartFile.getOriginalFilename());
//        multipartFile.transferTo(file);
//        File file = new File("/Users/hong/Downloads/"+"test.pdf");
        File file = new File(multipartFile.getOriginalFilename());
        file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(multipartFile.getBytes());
        fos.close();
        PDDocument document;
        document = PDDocument.load(file);

        PDFTextStripperByArea stripper = new PDFTextStripperByArea();
        stripper.setSortByPosition(true);
        PDFTextStripper Tstripper = new PDFTextStripper();
//        Tstripper.setStartPage(2);
//        Tstripper.setEndPage(2);
        String summaryText = Tstripper.getText(document);
//        log.info("{extracted String} ==>" + summaryText);

        return summaryText;
    }
}
