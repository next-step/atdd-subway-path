package atdd.line;

import atdd.station.AbstractEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Line extends AbstractEntity {

    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private int intervalTime;
}
