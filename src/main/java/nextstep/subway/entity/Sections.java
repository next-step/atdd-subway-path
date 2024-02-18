package nextstep.subway.entity;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public List<Section> getSections() {
        return this.sections.stream()
            .sorted()
            .collect(Collectors.toList());
    }

    public List<Station> getStations() {
        return this.sections.stream()
            .sorted()
            .flatMap(it -> Stream.of(it.getUpStation(), it.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if(!this.sections.isEmpty()) {
            verifyAddSection(section);

            updateMiddleSection(section);
        }

        this.sections.add(section);
    }

    private void verifyAddSection(Section section) {
        boolean existsUpStation = getStations().stream()
            .anyMatch(it -> it.equals(section.getUpStation()));
        boolean existsDownStation = getStations().stream()
            .anyMatch(it -> it.equals(section.getDownStation()));

        verifyConnectedSection(existsUpStation, existsDownStation);

        verifyDuplicationStation(existsUpStation, existsDownStation);
    }

    private void verifyConnectedSection(boolean existsUpStation, boolean existsDownStation) {
        if(!existsUpStation && !existsDownStation) {
            throw new IllegalArgumentException("연결할 수 없는 구간입니다.");
        }
    }

    private void verifyDuplicationStation(boolean existsUpStation, boolean existsDownStation) {
        if(existsUpStation && existsDownStation) {
            throw new IllegalArgumentException("중복된 역은 등록 불가합니다.");
        }
    }
    private Section verifyEndSection(Station station) {
        Section endSection = getEndSection();

        boolean isEndSection = endSection.getDownStation().equals(station);
        if(!isEndSection) {
            throw new IllegalArgumentException("지하철 노선에 등록된 마지막 구간만 제거할 수 있다");
        }

        return endSection;
    }

    private void updateMiddleSection(Section section) {
        // 이후 구간
        Optional<Section> nextSection = getSections().stream()
            .filter(it -> it.getUpStation().equals(section.getUpStation()))
            .findAny();
        nextSection.ifPresent(
            it -> it.updateNextSection(section.getDownStation(), section.getDistance())
        );

        // 이전 구간
        Optional<Section> prevSection = getSections().stream()
            .filter(it -> it.getDownStation().equals(section.getDownStation()))
            .findAny();
        prevSection.ifPresent(
            it -> it.updatePrevSection(section.getUpStation(), section.getDistance())
        );
    }

    public void removeSection(Station station) {
        verifyStartSection(station);
        Section endSection = verifyEndSection(station);
        this.sections.remove(endSection);
    }

    private void verifyStartSection(Station station) {
        boolean isStartSection = getStartSection().getDownStation().equals(station);
        if(isStartSection) {
            throw new IllegalArgumentException("지하철 노선에 상행 종점역과 하행 종점역만 있는 경우(구간이 1개인 경우) 역을 삭제할 수 없다");
        }
    }

    private Section getStartSection() {
        return getSections().get(0);
    }

    private Section getEndSection() {
        return getSections().get(sections.size()-1);
    }
}
