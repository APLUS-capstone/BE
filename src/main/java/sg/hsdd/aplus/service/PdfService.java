package sg.hsdd.aplus.service;

import org.springframework.web.multipart.MultipartFile;
import sg.hsdd.aplus.controller.dto.OptionDTO;
import sg.hsdd.aplus.service.vo.PdfStringVO;

import java.io.IOException;

public interface PdfService {
    PdfStringVO extractText(MultipartFile multipartFile, int userUid) throws IOException;
}
