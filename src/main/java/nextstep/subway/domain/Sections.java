package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
            CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        if (insertSectionIsUpStation(section)) {
            return;
        }

        if (insertSectionIsDownStation(section)) {
            return;
        }

        if (insertSectionBetweenSections(section)) {
            return;
        }

        throw new IllegalArgumentException("section 을 넣을 수 없습니다.");
    }

    private boolean insertSectionIsUpStation(Section section) {
        if (sections.get(0).getUpStation().equals(section.getDownStation())) {
            sections.add(0, section);
            return true;
        }
        return false;
    }

    private boolean insertSectionIsDownStation(Section section) {
        if (sections.get(sections.size() - 1).getDownStation().equals(section.getUpStation())) {
            sections.add(sections.size() - 1, section);
            return true;
        }
        return false;
    }

    private boolean insertSectionBetweenSections(Section registerSection) {
        for (Section section : sections) {
            int index = sections.indexOf(section);

            if (section.getUpStation().equals(registerSection.getUpStation())) {
                validateDistance(section, registerSection);

                sections.remove(section);

                sections.add(index, registerSection);
                sections.add(index + 1
                        , new Section(section.getLine(), registerSection.getDownStation(),
                                section.getDownStation(),
                                section.getDistance() - registerSection.getDistance()));

                return true;
            }

            if (section.getDownStation().equals(registerSection.getDownStation())) {
                validateDistance(section, registerSection);

                sections.remove(section);

                sections.add(index
                        , new Section(section.getLine(), section.getUpStation(),
                                registerSection.getUpStation(),
                                section.getDistance() - registerSection.getDistance()));
                sections.add(index + 1, registerSection);

                return true;
            }
        }

        return false;
    }

    private void validateDistance(Section section, Section registerSection) {
        if (registerSection.getDistance() > section.getDistance()) {
            throw new IllegalArgumentException("등록하려는 구간의 distance 가 등록된 구간 보다 깁니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Section firstSection = firstSection();
        stations.add(firstSection.getUpStation());

        Station downStation = firstSection.getDownStation();

        while (sections.size() == stations.size() + 1) {
            Section nextSection = nextSection(downStation);
            Station upStation = nextSection.getUpStation();
            downStation = nextSection.getDownStation();
            stations.add(upStation);
        }

        stations.add(downStation);

        return stations;
    }

    /**
     * 상행역이 상행종점역인 구간 찾기
     */
    private Section firstSection() {
        return sections.stream()
                .filter(section -> sections.stream()
                        .noneMatch(otherSection -> otherSection.getDownStation()
                                .equals(section.getUpStation())))
                .findFirst().orElseThrow(RuntimeException::new);
    }

    private Section nextSection(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation().equals(downStation))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public void removeSection() {
        sections.remove(sections.size() - 1);
    }

    public List<Section> getSections() {
        return sections;
    }
}
