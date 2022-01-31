package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.BusinessException;
import nextstep.subway.applicaion.exception.DuplicationException;
import nextstep.subway.applicaion.exception.NotLastSectionException;
import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        duplicationSection(newSection);

        if (isDownStation(newSection.getUpStation().getId())) {
            //맨 뒤에 붙는 경우
            sections.add(newSection);
            return;
        }

        if (isUpStation(newSection.getDownStation().getId())) {
            //맨 앞에 붙는 경우
            sections.add(0, newSection);
            return;
        }

        //중간에 붙는 경우 (앞쪽 중간에 붙는 경우)
        if (isUpStation(newSection.getUpStation().getId())) {
            addSectionBetweenMatchUpStation(newSection);
            return;
        }

        //중간에 붙는 경우 (뒤쪽 중간에 붙는 경우)
        if (isDownStation(newSection.getDownStation().getId())) {
            addSectionBetweenMatchDownStation(newSection);
            return;
        }

    }

    private void addSectionBetweenMatchDownStation(Section newSection) {
        Section oldSection = sections.stream().filter(section -> section.isSameDownStation(newSection.getDownStation())).findFirst().orElseThrow(BusinessException::new);
        int index = sections.indexOf(oldSection);
        Section firstSection = Section.of(oldSection.getUpStation(), newSection.getUpStation(), oldSection.getDistance() - newSection.getDistance());
        Section secondSection = Section.of(newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance());

        firstSection.updateLine(oldSection.getLine());
        secondSection.updateLine(oldSection.getLine());

        sections.set(index, firstSection);
        sections.add(index + 1, secondSection);
    }

    private void addSectionBetweenMatchUpStation(Section newSection) {
        Section oldSection = sections.stream().filter(section -> section.isSameUpStation(newSection.getUpStation())).findFirst().orElseThrow(BusinessException::new);
        int index = sections.indexOf(oldSection);
        Section firstSection = Section.of(newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance());
        Section secondSection = Section.of(newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance());

        firstSection.updateLine(oldSection.getLine());
        secondSection.updateLine(oldSection.getLine());

        sections.set(index, firstSection);
        sections.add(index + 1, secondSection);
    }

    private boolean isDownStation(Long station) {
        for (Section section : sections) {
            if (section.isSameDownStation(station)) {
                return true;
            }
        }
        return false;
    }

    private boolean isUpStation(Long station) {
        for (Section section : sections) {
            if (section.isSameUpStation(station)) {
                return true;
            }
        }
        return false;
    }

    private void duplicationSection(Section section) {
        if (contains(section)) {
            throw new DuplicationException();
        }
    }

    public boolean isLastSection() {
        return sections.size() == 1;
    }

    public void deleteSection(Long stationId) {
        if (isLastSection()) {
            throw new BusinessException("마지막 구간 삭제 불가", HttpStatus.BAD_REQUEST);
        }
        int count = countStation(stationId);
        deleteSection(stationId, count);
    }

    private void deleteSection(Long stationId, int count) {
        int lastDownStation = 1;
        if (count == lastDownStation) {
            Section findSection = sections
                    .stream()
                    .filter(section ->
                            section.isSameDownStation(stationId))
                    .findFirst()
                    .orElse(null);
            sections.remove(findSection);
            return;
        }
        throw new NotLastSectionException();
    }

    public int countStation(Long stationId){
        return (int) sections.stream()
                .filter(section ->
                        section.isSameUpStation(stationId) || section.isSameDownStation(stationId))
                .count();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public int getSize() {
        return sections.size();
    }

    public boolean contains(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        return sections.stream()
                .filter(section -> section.isSameUpStation(upStation) && section.isSameDownStation(downStation))
                .findFirst()
                .isPresent();
    }
}
