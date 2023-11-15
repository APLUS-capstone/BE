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
@Table(name="problem_type")
@Entity
@SuperBuilder
public class ProblemType {
    @Id
    private int typeUid;

    @Column
    private String problemType;
}
