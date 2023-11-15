package sg.hsdd.aplus.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name="question_room")
@Entity
@SuperBuilder
public class QuestionRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int roomUid;

    @Column
    private int userUid;

    @Column
    private String pdfString;
}
