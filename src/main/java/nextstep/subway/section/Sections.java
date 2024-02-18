package nextstep.subway.section;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @OneToMany(
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(int index, Section section) {
        sections.add(index, section);
    }

    public void addMiddleSection(Section section) {
        int insertIndex = sections.stream()
                .filter(s -> s.isConnectedSection(section))
                .findFirst()
                .map(s -> sections.indexOf(s) + 1)
                .orElseThrow(() -> new IllegalArgumentException("구간이 올바르게 이어지지 않습니다."));
        sections.add(insertIndex, section);

        // 양쪽 구간의 상/하행역 변경
        Section beforeSection = sections.get(insertIndex - 1);
        Section afterSection = sections.get(insertIndex);
        Station updateUpStation = getUpdateUpStation(beforeSection, section);
        Station updateDownStation = getUpdateDownStation(afterSection, section);
        beforeSection.updateDownStation(updateDownStation);
        afterSection.updateUpStation(updateUpStation);
    }

    private Station getUpdateUpStation(Section beforeSection, Section section) {
        if (beforeSection.getDownStation() == section.getDownStation()) {
            return section.getUpStation();
        }
        return beforeSection.getDownStation();
    }

    private Station getUpdateDownStation(Section beforeSection, Section section) {
        if (beforeSection.getUpStation() == section.getUpStation()) {
            return section.getDownStation();
        }
        return beforeSection.getUpStation();
    }

    public Section getLastSection() {
        return sections.get(sections.size() - 1);
    }

    public boolean isAlreadyRegisteredSection(Section anotherSection) {
        return sections.stream()
                .anyMatch(section -> section.isAlreadyRegisteredSection(anotherSection));
    }

    public boolean isFirstSection(Section section) {
        return sections.isEmpty() || section.isFirstSection(sections.get(0));
    }

    public boolean isFinalSection(Section section) {
        return section.isFinalSection(sections.get(sections.size() - 1));
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isSingleSection() {
        return sections.size() == 1;
    }

    public void addSection(Section section) {
        // 처음 구간 추가
        if (isFirstSection(section)) {
            addFirstSection(section);
            return;
        }

        // 마지막 구간 추가
        if (isFinalSection(section)) {
            addFinalSection(section);
            return;
        }

        // 중간 구간 추가
        addMiddleSection(section);
    }

    private void addFirstSection(Section section) {
        sections.add(0, section);
    }

    private void addFinalSection(Section section) {
        sections.add(sections.size(), section);
    }

    public void removeSection(Station station) {
        Section lastSection = getLastSection();
        if (lastSection.getDownStation() != station) {
            throw new IllegalArgumentException("삭제할 구간이 올바르지 않습니다.");
        }

        lastSection.remove();
        sections.remove(lastSection);
    }

    public void validateSaveSection(Section section) {
        if (isAlreadyRegisteredSection(section)) {
            throw new IllegalArgumentException("이미 등록된 구간입니다.");
        }
    }

    public void validateDeleteSection(Station station) {
        if (isSingleSection()) {
            throw new IllegalArgumentException("삭제할 구간이 존재하지 않습니다.");
        }

        if (!isRemoveFinalSection(station)) {
            throw new IllegalArgumentException("삭제할 구간이 올바르지 않습니다.");
        }
    }

    public boolean isRemoveFinalSection(Station station) {
        return getLastSection().isMatchDownStation(station);
    }

    @Override
    public String toString() {
        return "Sections{" +
                "sections=" + sections +
                '}';
    }
}
