package nextstep.subway.section;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sectionList = new ArrayList<>();

    public void addSection(Section section) {
        if (sectionList.isEmpty()) {
            sectionList.add(section);
            return;
        }
        if(isAlreadyRegistration(section)) {
            throw new IllegalStateException("이미 존재하는 구간입니다.");
        }

        // 상행역이나 하행역 둘 다 기존 노선에 없는 경우
        //하행종점역에 추가되는 경우
        if(isLastAddSection(section)) {
            lastAddValidate(section);
            sectionList.add(section);
            return;
        }

        //상행종점역에 추가하는 경우
        if(isFirstAddSection(section)) {
            firstAddValidate(section);
            sectionList.add(0, section);
            return;
        }

        //중간에 추가하는 경우 해당 section 찾기
        Section findSection = findMidSection(section);
        updateSectionDistance(section, findSection);
        sectionList.add(section);
    }

    private static void updateSectionDistance(Section section, Section findSection) {
        Long distance = findSection.getDistance() - section.getDistance();
        findSection.updateUpStationId(section.getDownStationId());
        findSection.updateDistance(distance);
    }

    private Section findMidSection(Section section) {
        return sectionList.stream().map(item -> {
            if (section.getUpStationId().equals(item.getUpStationId())) {
                return item;
            }
            return null;
        }).findFirst().orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
    }


    private boolean isAlreadyRegistration(Section section) {
        return sectionList.stream().anyMatch(item -> isEqualUpAndDownId(section, item));
    }

    private static boolean isEqualUpAndDownId(Section section, Section item) {
        return section.getUpStationId().equals(item.getUpStationId())
            && section.getDownStationId().equals(item.getDownStationId());
    }


    private void firstAddValidate(Section section) {
        if(sectionList.stream().anyMatch(section::firstAddValidate)) {
            throw new IllegalStateException("상행 종점역에 추가할 때 상행역이 기존 노선에 등록되어 있는 역이라 추가할 수 없습니다.");
        }
    }

    private boolean isFirstAddSection(Section section) {
        return section.getDownStationId().equals(getFirstUpStationId());
    }

    private void lastAddValidate(Section section) {
        if(sectionList.stream().anyMatch(section::lastAddValidate)) {
            throw new IllegalStateException("하행 종점역에 추가할 때 하행역이 기존 노선에 등록되어 있는 역이라 추가할 수 없습니다.");
        }
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
        Section findLeftSection = findSectionByDownStationId(stationId);
        Section findRightSection = findSectionByUpStationId(stationId);
        long distance = findLeftSection.getDistance() + findRightSection.getDistance();
        findRightSection.updateDistance(distance);
        findRightSection.updateUpStationId(findLeftSection.getUpStationId());
        sectionList.remove(findLeftSection);
    }

    private Section findSectionByUpStationId(Long stationId) {
        return sectionList.stream().map(item -> {
            if (stationId.equals(item.getUpStationId())) {
                return item;
            }
            return null;
        }).findFirst().orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
    }

    private Section findSectionByDownStationId(Long stationId) {
        return sectionList.stream().map(item -> {
            if (stationId.equals(item.getDownStationId())) {
                return item;
            }
            return null;
        }).findFirst().orElseThrow(() -> new IllegalArgumentException("일치하는 구간이 없습니다."));
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

}
