package nextstep.subway.section.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import nextstep.subway.section.domain.Section;


@Getter
@Setter
@NoArgsConstructor
public class SectionCreateResponse {
    private Long id;

    public SectionCreateResponse(Section section) {
        this.id = section.getId();
    }
}
