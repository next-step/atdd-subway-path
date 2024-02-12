package nextstep.subway.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<SectionResponse> sections;
    private List<StationResponse> stations;
    private int distance;

    public SectionResponse findSectionByUpStationName(String name) {
        return this.sections.stream()
                .filter(section -> section.getUpStation().getName().equals(name))
                .findFirst()
                .get();
    }
}


