package sg.hsdd.aplus.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sg.hsdd.aplus.entity.Chatroom;
import sg.hsdd.aplus.repository.ChatroomRepository;
import sg.hsdd.aplus.service.vo.PdfStringVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class PdfServiceImpl implements PdfService{

    @Autowired
    private ChatroomRepository chatroomRepository;

    @Override
    public PdfStringVO extractText(MultipartFile multipartFile, int userUid) throws IOException {
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

        if(document != null )
        {
            document.close();
        }

        Chatroom chatroom = Chatroom.builder()
                .userUid(userUid)
                .pdfString(summaryText)
                .build();

        chatroomRepository.save(chatroom);

        PdfStringVO pdfStringVO = PdfStringVO.builder()
                .pdfString(summaryText)
                .build();

        return pdfStringVO;
    }
}
