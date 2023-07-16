package nextstep.subway.domain;

import nextstep.subway.domain.exception.AlreadyRegisteredSectionDownStationException;
import nextstep.subway.domain.exception.NotEnoughSectionException;
import nextstep.subway.domain.exception.NotMatchesSectionStationException;
import nextstep.subway.domain.vo.Sections;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 20, nullable = false)
    private String color;

    @Column(nullable = false)
    private Long distance;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.sections.add(Section.of(upStation, downStation, distance));
        this.distance = distance;
    }

    public Station getUpStation() {
        return this.sections.getUpStation();
    }

    public Station getDownStation() {
        return this.sections.getDownStation();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void expandLine(Section newSection) {
        if (this.sections.isNotEndWith(newSection.getUpStation())) {
            throw new NotMatchesSectionStationException(this.getDownStation(), newSection.getUpStation());
        }
        if (this.sections.contains(newSection.getDownStation())) {
            throw new AlreadyRegisteredSectionDownStationException(newSection.getDownStation());
        }

        this.sections.add(newSection);
        this.distance = sections.sumOfDistance();
    }

    public void shorten(Station targetStation) {
        if (this.sections.isMinimumSize()) {
            throw new NotEnoughSectionException();
        }
        if (this.sections.isNotEndWith(targetStation)) {
            throw new NotMatchesSectionStationException(this.getDownStation(), targetStation);
        }

        this.sections.pop();
        this.distance = sections.sumOfDistance();
    }

    public long getDistance() {
        return this.distance;
    }

    public List<Station> getStations() {
        return this.sections.getStations();
    }

    public static class Builder {
        private String name;
        private String color;
        private Station upStation;
        private Station downStation;
        private Long distance;

        private Builder() {
        }

        public Builder name(String name) {
            assert StringUtils.hasText(name);
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            assert StringUtils.hasText(color);
            this.color = color;
            return this;
        }

        public Builder upStation(Station upStation) {
            Objects.requireNonNull(upStation);
            this.upStation = upStation;
            return this;
        }

        public Builder downStation(Station downStation) {
            Objects.requireNonNull(downStation);
            this.downStation = downStation;
            return this;
        }

        public Builder distance(Long distance) {
            Objects.requireNonNull(distance);
            this.distance = distance;
            return this;
        }

        public Line build() {
            return new Line(this.name, this.color, upStation, downStation, distance);
        }


    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void modify(String lineName, String color) {
        this.name = lineName;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line that = (Line) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
