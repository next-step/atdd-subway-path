package nextstep.subway.section;

import nextstep.exception.BadRequestException;
import nextstep.subway.station.Station;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Embeddable
public class Sections {
    private final static int MIN_SECTION_SIZE = 1;

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {}

    public List<Section> getSections() {
        return sections;
    }

    private Section lastSection() {
        return sections.get(sections.size()-1);
    }

    public void addSection(Section newSection) {
        if(isExistStation(newSection.getDownStation())){
            throw new BadRequestException("새로운 구간의 하행역이 이미 노선에 등록된 역입니다.");
        }

        if(!newSection.getUpStation().equals(lastSection().getDownStation())) {
            throw new BadRequestException("새로운 구간의 상행역과 노선의 하행역이 일치하지 않습니다.");
        }
    }

    private boolean isExistStation(Station newStation) {
        for(Section section : sections) {
            if(newStation.equals(section.getUpStation())){
                return true;
            }
        }
        return false;
    }

    public Long deleteStation(Station deleteStation) {
        if(sections.size() == MIN_SECTION_SIZE) {
            throw new IllegalArgumentException("구간이 1개 남은 경우 삭제할 수 없습니다.");
        }

        if(!deleteStation.equals(lastSection().getDownStation())){
            throw new IllegalArgumentException("노선의 하행 종점역이 아닙니다.");
        }

        Section deleteSection = sections.stream()
                .filter(section -> section.getDownStation().equals(deleteStation))
                .findAny().get();

        sections.removeIf(s -> s.getId().equals(deleteSection.getId()));

        return deleteSection.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(getSections(), sections1.getSections());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSections());
    }
}
