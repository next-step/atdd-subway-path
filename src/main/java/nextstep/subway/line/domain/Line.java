package nextstep.subway.line.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.global.exception.AlreadyRegisteredException;
import nextstep.subway.global.exception.InsufficientStationException;
import nextstep.subway.global.exception.SectionMismatchException;
import nextstep.subway.global.exception.StationNotMatchException;
import nextstep.subway.section.presentation.request.SectionCreateRequest;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String color;
    @Embedded
    private Sections sections;

    @Builder
    public Line(String name, String color) {
        this.name = name;
        this.color = color;
        this.sections = new Sections();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateColor(String color) {
        this.color = color;
    }

    public void validateSequence(SectionCreateRequest request) {
        if (this.getSections().getDownStationId() != request.getUpStationId()) {
            throw new SectionMismatchException();
        }
    }

    public void validateUniqueness(SectionCreateRequest request) {
        if (this.getSections().getDownStationIds().contains(request.getDownStationId())) {
            throw new AlreadyRegisteredException();
        }
    }

    public void validateLastStation() {
        if (this.getSections().getDownStationIds().size() < 1) {
            throw new InsufficientStationException();
        }
    }

    public void validateDownStationId(Long stationId) {
        if (this.getSections().getDownStationId() != stationId) {
            throw new StationNotMatchException();
        }
    }
}
