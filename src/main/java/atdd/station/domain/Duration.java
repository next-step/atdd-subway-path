package atdd.station.domain;

import org.springframework.util.Assert;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalTime;

@Embeddable
public class Duration {

    @Column(name = "duration", nullable = false)
    private LocalTime duration;

    private Duration() { }

    public Duration(LocalTime duration) {
        Assert.notNull(duration, "시간은 필수값 입니다.");
        Assert.isTrue(duration.isAfter(LocalTime.MIN), "시간은 00:00 보다 커야 합니다.");
        this.duration = duration;
    }

    public LocalTime getDuration() {
        return duration;
    }

}
