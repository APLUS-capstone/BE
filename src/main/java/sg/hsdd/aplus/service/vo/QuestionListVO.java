package sg.hsdd.aplus.service.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class QuestionListVO {
    private int number;
    private int type;
    private List<QuestionVO> list;
}
