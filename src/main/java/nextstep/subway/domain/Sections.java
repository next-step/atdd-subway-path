package nextstep.subway.domain;

import nextstep.subway.applicaion.exception.BusinessException;
import nextstep.subway.applicaion.exception.DuplicationException;
import nextstep.subway.applicaion.exception.NotFoundException;
import org.springframework.http.HttpStatus;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Sections {

    private static final int LAST_ONE = 1;
    private static final int NO_ONE = 0;


    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        duplicationSection(newSection);
        notInStationOfSections(newSection);

        if (isLastDownSection(newSection.getUpStation().getId())) {
            //맨 뒤에 붙는 경우
            sections.add(newSection);
            return;
        }

        if (isLastUpSection(newSection.getDownStation().getId())) {
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

    private boolean isLastDownSection(Long id) {
        return sections.stream().filter(section ->
                section.isSameDownStation(id)).count() == LAST_ONE &&
                sections.stream().filter(section ->
                        section.isSameUpStation(id)).count() == NO_ONE
                ;
    }

    private boolean isLastUpSection(Long id) {
        return sections.stream().filter(section ->
                section.isSameDownStation(id)).count() == NO_ONE &&
                sections.stream().filter(section ->
                        section.isSameUpStation(id)).count() == LAST_ONE
                ;
    }

    private void notInStationOfSections(Section newSection) {
        if (stationIsNotInSection(newSection)) {
            throw new BusinessException("해당 노선에 등록된 역들이 없습니다", HttpStatus.BAD_REQUEST);
        }
    }

    private void addSectionBetweenMatchDownStation(Section newSection) {
        Section oldSection = sections.stream().filter(section -> section.isSameDownStation(newSection.getDownStation())).findFirst().orElseThrow(BusinessException::new);
        isCanInsertBetweenSection(oldSection, newSection);
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
        isCanInsertBetweenSection(oldSection, newSection);
        int index = sections.indexOf(oldSection);
        Section firstSection = Section.of(newSection.getUpStation(), newSection.getDownStation(), newSection.getDistance());
        Section secondSection = Section.of(newSection.getDownStation(), oldSection.getDownStation(), oldSection.getDistance() - newSection.getDistance());

        firstSection.updateLine(oldSection.getLine());
        secondSection.updateLine(oldSection.getLine());

        sections.set(index, firstSection);
        sections.add(index + 1, secondSection);
    }

    private void isCanInsertBetweenSection(Section oldSection, Section newSection) {
        if (oldSection.getDistance() - newSection.getDistance() < 0) {
            throw new BusinessException("기존 구간을 넘었어요", HttpStatus.BAD_REQUEST);
        }
    }

    private boolean isDownStation(Long station) {
        return sections.stream().anyMatch(
                section -> section.isSameDownStation(station)
        );
    }

    private boolean isUpStation(Long station) {
        return sections.stream().anyMatch(
                section -> section.isSameUpStation(station)
        );
    }

    private void duplicationSection(Section section) {
        if (containsStation(section)) {
            throw new DuplicationException();
        }
    }

    public boolean isLastSection() {
        return sections.size() == LAST_ONE;
    }

    public void deleteSection(Long stationId) {
        if (isLastSection()) {
            throw new BusinessException("하나 남은 구간 삭제 불가", HttpStatus.BAD_REQUEST);
        }
        if (countStation(stationId) == NO_ONE) {
            throw new BusinessException("노선에 없는 역은 삭제 불가", HttpStatus.BAD_REQUEST);
        }

        //마지막 역 삭제
        if (isLastDownSection(stationId)) {
            Section sectionByUpStation = findLastSection();
            sections.remove(sectionByUpStation);
            return;
        }

        //처음 역 삭제
        if (isLastUpSection(stationId)) {
            Section sectionByUpStation = findFirstSection();
            sections.remove(sectionByUpStation);
            return;
        }

        //중간 역 삭제
        deleteAndMergeSection(stationId);
    }

    private void deleteAndMergeSection(Long stationId) {
        Section findSection = findSectionByUpStation(stationId);
        int index = sections.indexOf(findSection);
        int frontIndex = index - 1;
        int behindIndex = index + 1;

        if (frontIndex < 0 || behindIndex >= sections.size()) {
            sections.remove(findSection);
            return;
        }


        Section frontSection = sections.get(frontIndex);

        sections.remove(findSection);
        sections.remove(frontSection);

        Section newSection = Section.of(frontSection.getUpStation(), findSection.getDownStation(), frontSection.getDistance() + findSection.getDistance());
        newSection.updateLine(findSection.getLine());
        sections.add(frontIndex, newSection);
    }

    public int countStation(Long stationId) {
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

    public boolean containsStation(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        return sections.stream()
                .filter(section -> section.isSameUpStation(upStation) || section.isSameDownStation(upStation))
                .findFirst()
                .isPresent()
                &&
                sections.stream()
                        .filter(section -> section.isSameUpStation(downStation) || section.isSameDownStation(downStation))
                        .findFirst()
                        .isPresent();
    }

    public boolean stationIsNotInSection(Section newSection) {
        Station upStation = newSection.getUpStation();
        Station downStation = newSection.getDownStation();
        return !(sections.stream()
                .filter(section -> section.isSameUpStation(upStation) || section.isSameDownStation(upStation))
                .findFirst()
                .isPresent()
                ||
                sections.stream()
                        .filter(section -> section.isSameUpStation(downStation) || section.isSameDownStation(downStation))
                        .findFirst()
                        .isPresent());
    }

    public Section findFirstSection() {
        return sections.stream().filter(section -> countStation(section.getUpStation().getId()) == LAST_ONE)
                .findFirst()
                .orElseThrow(BusinessException::new);
    }

    private Section findLastSection() {
        return sections.stream().filter(section -> countStation(section.getDownStation().getId()) == LAST_ONE)
                .findFirst()
                .orElseThrow(BusinessException::new);
    }

    public Section findSectionByUpStation(Long id) {
        return sections.stream()
                .filter(section -> section.isSameUpStation(id))
                .findFirst()
                .orElseThrow(NotFoundException::new);
    }
}
