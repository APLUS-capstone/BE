package sg.hsdd.aplus.service;

import org.springframework.web.multipart.MultipartFile;
import sg.hsdd.aplus.controller.dto.OptionDTO;

import java.io.IOException;

public interface PdfService {
    String extractText(MultipartFile multipartFile, OptionDTO optionDTO) throws IOException;
}
