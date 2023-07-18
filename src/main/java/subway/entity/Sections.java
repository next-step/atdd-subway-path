package subway.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import subway.exception.ErrorCode;
import subway.exception.ApiException;

@Getter
@Setter
@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    @Builder
    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    protected Sections() {
    }

    public void addSection(Section section) {
        // 이미 등록된 구간인 경우
        if(stations().stream().anyMatch(o -> o.equals(section.getDownStation()))) {
            throw new ApiException(ErrorCode.ALREADY_REGISTERED_STATION);
        }

        // 마지막 구간의 하행역과 추가하려는 구간의 상행역과 일치하는지 확인
        var lastSection = this.lastSection();
        if(!lastSection.getDownStation().equals(section.getUpStation())) {
            throw new ApiException(ErrorCode.INVALID_SECTION_REGISTRATION);
        }

        sections.add(section);
    }

    public void deleteSection(Long stationId) {
        // 구간 개수 확인
        var size = sections.size();
        if(size == 1) {
            throw new ApiException(ErrorCode.DELETE_OF_ONLY_ONE_SECTION);
        }

        // 마지막 구간의 하행역 아이디와 일치하는지 확인
        var lastSection = this.lastSection();
        if(!lastSection.getDownStation().getId().equals(stationId)) {
            throw new ApiException(ErrorCode.DELETE_BY_TERMINATE_STATION);
        }

        sections.remove(size - 1);
    }

    public Section lastSection() {
        return sections.get(sections.size() - 1);
    }

    public List<Station> stations() {
        return this.sections.stream().map(o -> Arrays.asList(o.getUpStation(), o.getDownStation()))
            .flatMap(o -> o.stream())
            .distinct()
            .collect(Collectors.toList());
    }

    public Long totalDistance() {
        return this.sections.stream().mapToLong(o -> o.getDistance()).sum();
    }
}
