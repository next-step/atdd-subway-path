package nextstep.subway.domain;

import nextstep.subway.domain.exception.CanNotAddSectionException;
import nextstep.subway.domain.exception.NotEnoughSectionException;
import nextstep.subway.domain.vo.Sections;

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

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    private Line(String name, String color, Station upStation, Station downStation, Long distance) {
        this.name = name;
        this.color = color;
        this.sections.add(Section.of(upStation, downStation, distance));
    }

    public List<Section> getSections() {
        return sections.getValue();
    }

    public static Builder builder() {
        return new Builder();
    }

    public void add(Section newSection) {
        if (this.sections.canNotAdd(newSection)) {
            throw new CanNotAddSectionException(newSection);
        }

        this.sections.add(newSection);
    }

    public void remove(Station targetStation) {
        if (this.sections.isMinimumSize()) {
            throw new NotEnoughSectionException();
        }

        this.sections.remove(targetStation);
    }

    public long getDistance() {
        return this.sections.sumOfDistance();
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
            Objects.requireNonNull(name);
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            Objects.requireNonNull(color);
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
