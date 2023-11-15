package sg.hsdd.aplus.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import sg.hsdd.aplus.controller.dto.OptionDTO;
import sg.hsdd.aplus.controller.dto.PdfSaveDTO;
import sg.hsdd.aplus.service.PdfService;
import sg.hsdd.aplus.service.vo.PdfStringVO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pdf")
public class PdfController {
    @Autowired
    private PdfService pdfService;

    @RequestMapping(value = "/save", method = RequestMethod.POST
            ,consumes = {MediaType.APPLICATION_JSON_VALUE ,MediaType.MULTIPART_FORM_DATA_VALUE}
    )
    public PdfStringVO uploadFile(
            @RequestPart("pdf") MultipartFile multipartFile,
            @RequestPart("userUid") PdfSaveDTO pdfSaveDTO,
            HttpServletRequest request
    ) throws IllegalStateException, IOException {
        return pdfService.extractText(multipartFile, pdfSaveDTO.getUserUid());
    }
}
