package nextstep.subway.section;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void addSection(Section section) {
        if(sectionList.isEmpty()) {
            sectionList.add(section);
            return;
        }

        validateSection(section);
        if (isFirstAddSection(section)) {
            sectionList.add(0, section);
            return;
        }

        if (isLastAddSection(section)) {
            sectionList.add(section);
            return;
        }

        addMidSection(section);
    }

    private void validateSection(Section section) {
        if (isAlreadyRegistration(section)) {
            throw new IllegalStateException("이미 존재하는 구간입니다.");
        }
        if (isFirstAddSection(section) && isStationRegistered(section::firstAddValidate)) {
            throw new IllegalStateException("상행 종점역에 추가할 때 상행역이 기존 노선에 등록되어 있는 역이라 추가할 수 없습니다.");
        }
        if (isLastAddSection(section) && isStationRegistered(section::lastAddValidate)) {
            throw new IllegalStateException("하행 종점역에 추가할 때 하행역이 기존 노선에 등록되어 있는 역이라 추가할 수 없습니다.");
        }
    }

    private boolean isStationRegistered(Predicate<Section> validate) {
        return sectionList.stream().anyMatch(validate);
    }

    private void addMidSection(Section section) {
        Section findSection = findMidSection(section);
        Long distance = findSection.getDistance() - section.getDistance();
        updateSection(section, distance, findSection.getDownStationId());
        sectionList.add(section);
    }

    private Section findMidSection(Section section) {
        return findSectionByUpStationId(section.getUpStationId());
    }

    private boolean isAlreadyRegistration(Section section) {
        return sectionList.stream().anyMatch(item -> isEqualUpAndDownId(section, item));
    }

    private static boolean isEqualUpAndDownId(Section section, Section item) {
        return section.getUpStationId().equals(item.getUpStationId())
            && section.getDownStationId().equals(item.getDownStationId());
    }

    private boolean isFirstAddSection(Section section) {
        return section.getDownStationId().equals(getFirstUpStationId());
    }

    private boolean isLastAddSection(Section section) {
        return section.getUpStationId().equals(getLastDownStationId());
    }

    public Long getFirstUpStationId() {
        if(sectionList.isEmpty()) {
            throw new IllegalArgumentException("section is Empty");
        }
        return getUpStationId(0);
    }

    public Long getLastDownStationId() {
        if (sectionList.isEmpty()) {
            throw new IllegalArgumentException("section is Empty");
        }
        return getDownStationId(getLastIndex());
    }

    private Long getUpStationId(int index) {
        return sectionList.get(index).getUpStationId();
    }

    private Long getDownStationId(int index) {
        return sectionList.get(index).getDownStationId();
    }

    public void removeLastSection(Long stationId) {
        if(sectionList.isEmpty() || sectionList.size() < 2) {
            throw new IllegalArgumentException("노선에 2개 미만에 구역이 존재하여 삭제할 수 없습니다.");
        }

        //하행종점역을 삭제하는 경우
        if(isLastRemoveSection(stationId)) {
            sectionList.remove(getLastIndex());
            return;
        }

        //상행종점역을 삭제하는 경우
        if(isFirstRemoveSection(stationId)) {
            sectionList.remove(0);
            return;
        }

        //중간역을 삭제하는 경우
        removeMidSection(stationId);
    }

    private void removeMidSection(Long stationId) {
        Section findLeftSection = findSectionByDownStationId(stationId);
        Section findRightSection = findSectionByUpStationId(stationId);
        Long distance = findLeftSection.getDistance() + findRightSection.getDistance();
        updateSection(findRightSection, distance, findLeftSection.getUpStationId());
        sectionList.remove(findLeftSection);
    }

    private static void updateSection(Section section, Long distance, Long upStationId) {
        section.updateDistance(distance);
        section.updateUpStationId(upStationId);
    }

    private Section findSectionByUpStationId(Long stationId) {
        return findSectionByStationId(stationId, Section::getUpStationId);
    }

    private Section findSectionByDownStationId(Long stationId) {
        return findSectionByStationId(stationId, Section::getDownStationId);
    }

    private boolean isFirstRemoveSection(Long stationId) {
        return getFirstUpStationId().equals(stationId);
    }

    private boolean isLastRemoveSection(Long stationId) {
        return getLastDownStationId().equals(stationId);
    }

    private int getLastIndex() {
        return sectionList.size() - 1;
    }

    public List<Long> getAllStationId() {
        return sectionList.stream()
                          .flatMap((section) -> Stream.of(section.getUpStationId(),
                              section.getDownStationId())).distinct().collect(Collectors.toList());
    }

    private Section findSectionByStationId(Long stationId, Function<Section, Long> getStationId) {
        return sectionList.stream()
                          .filter(item -> stationId.equals(getStationId.apply(item)))
                          .findFirst()
                          .orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
    }
}
