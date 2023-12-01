package sg.hsdd.aplus.controller.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OptionDTO {
    private int roomUid;
    private int number;
    private int type;
    private int choice;
    private int language;
}
