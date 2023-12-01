package sg.hsdd.aplus.service;

import org.springframework.web.multipart.MultipartFile;
import sg.hsdd.aplus.controller.dto.OptionDTO;
import sg.hsdd.aplus.service.vo.PdfStringVO;
import sg.hsdd.aplus.service.vo.QuestionListVO;
import sg.hsdd.aplus.service.vo.QuestionVO;

import java.io.IOException;
import java.util.List;

public interface PdfService {
    PdfStringVO extractText(MultipartFile multipartFile, int userUid) throws IOException;
    QuestionListVO generateQuestion(OptionDTO optionDTO);
}
