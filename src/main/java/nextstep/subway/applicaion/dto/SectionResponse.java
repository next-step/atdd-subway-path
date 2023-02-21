package nextstep.subway.applicaion.dto;

import lombok.Getter;
import nextstep.subway.domain.Section;

@Getter
public class SectionResponse {
    private Long id;
    private String upStation;
    private String downStation;
    private int distance;

    public SectionResponse(Section section) {
        this.id = section.getId();
        this.upStation = section.getUpStation().getName();
        this.downStation = section.getDownStation().getName();
        this.distance = section.getDistance();
    }
}
