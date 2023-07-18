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
        var existDownStation = stations().stream().anyMatch(o -> o.equals(section.getDownStation()));
        var existUpStation = stations().stream().anyMatch(o -> o.equals(section.getUpStation()));

        // 이미 등록된 구간인 경우( 상행 하행 둘다 등록 )
        if(existDownStation && existUpStation) {
            throw new ApiException(ErrorCode.ALREADY_REGISTERED_SECTION);
        }

        // 연결 될 수 없는 구간인 경우( 상행 하행 둘다 등록 안됨 )
        if(!existDownStation && !existUpStation) {
            throw new ApiException(ErrorCode.NOT_CHAINING_SECTION);
        }

        var lastAttend = this.lastSection().getDownStation().equals(section.getUpStation());
        var firstAttend = this.firstSection().getUpStation().equals(section.getDownStation());

        if(lastAttend) {
            sections.add(section);
            return;
        }

        if(firstAttend) {
            List<Section> replaceSections = new ArrayList<>();
            replaceSections.add(section);
            replaceSections.addAll(this.sections);
            sections = replaceSections;
            return;
        }

        sections = middleAttend(section);
    }

    private List<Section> middleAttend(Section section) {
        //새로 등록하려는 구간이 같거나 큰 경우
        if(sections.stream()
            .filter(o -> (o.getUpStation().equals(section.getUpStation()) || o.getDownStation().equals(section.getDownStation())))
            .anyMatch(o -> o.getDistance() <= section.getDistance())) {
            throw new ApiException(ErrorCode.INVALID_SECTION_DISTANCE);
        }

        List<Section> replaceSections = new ArrayList<>();

        sections.stream().forEach(o -> {
            if(o.getUpStation().equals(section.getUpStation())) {
                replaceSections.add(section);
                var distance = o.getDistance() - section.getDistance();
                replaceSections.add(Section.builder()
                    .upStation(section.getDownStation())
                    .downStation(o.getDownStation())
                    .distance(distance)
                    .build());
            } else if(o.getDownStation().equals(section.getDownStation())) {
                var distance = o.getDistance() - section.getDistance();
                replaceSections.add(Section.builder()
                    .upStation(o.getUpStation())
                    .downStation(section.getUpStation())
                    .distance(distance)
                    .build());
                replaceSections.add(section);
            } else {
                replaceSections.add(o);
            }
        });

        return replaceSections;
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

    public Section firstSection() {
        return sections.get(0);
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
