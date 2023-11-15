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
@Table(name="question")
@Entity
@SuperBuilder
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int questionUid;

    @Column
    private int roomUid;

    @Column
    private int typeUid;

    @Column
    private int languageUid;

    @Column
    private int choiceNum;

    @Column
    private String question;

    @Column
    private String answer;

    @Column
    private String solution;
}
