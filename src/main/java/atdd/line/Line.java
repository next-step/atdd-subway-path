package atdd.line;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalTime;

@Getter
@NoArgsConstructor
@Entity
public class Line {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private LocalTime start_time;
    private LocalTime end_time;
    private int interval_time;
    private int extra_fare;

    @Builder
    public Line(Long id, String name, LocalTime start_time, LocalTime end_time, int interval_time, int extra_fare ){
        this.id = id;
        this.name = name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.interval_time = interval_time;
        this.extra_fare = extra_fare;
    }
}
