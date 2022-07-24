package nextstep.subway.applicaion.strategy;

import lombok.RequiredArgsConstructor;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

@RequiredArgsConstructor
public class MiddleSectionAdditor implements AdditionalStrategy{

    private final Sections sections;

    @Override
    public void add(Section newSection) {
        if(hasSameDownStation(newSection.getDownStation())) {
            Section connectSection = getSectionToConnectDownStation(newSection);
            connectSameDownStation(connectSection, newSection);
            return;
        }
        if(hasSameUpStation(newSection.getUpStation())) {
            Section connectSection = getSectionToConnectUpStation(newSection);
            connectSameUpStation(connectSection, newSection);
        }
    }

    private void connectSameUpStation(Section originSection, Section newSection) {
        checkToAddNewSection(originSection.getDistance(), newSection.getDistance());
        originSection.changeUpStation(newSection.getDownStation());
        originSection.decreaseDistance(newSection.getDistance());
        sections.add(newSection);
    }

    private void connectSameDownStation(Section originSection, Section newSection) {
        checkToAddNewSection(originSection.getDistance(), newSection.getDistance());
        originSection.changeDownStation(newSection.getUpStation());
        originSection.decreaseDistance(newSection.getDistance());
        sections.add(newSection);
    }

    private boolean hasSameDownStation(Station downStation) {
        return sections.getValues().stream()
                .anyMatch(origin -> origin.isEqualToDownStation(downStation));
    }

    private boolean hasSameUpStation(Station upStation) {
        return sections.getValues().stream()
                       .anyMatch(origin -> origin.isEqualToUpStation(upStation));
    }

    private Section getSectionToConnectDownStation(Section section) {
        return sections.getValues().stream()
                       .filter(s -> s.isEqualToDownStation(section.getDownStation()))
                       .findAny()
                       .orElseThrow(IllegalAccessError::new);
    }

    private Section getSectionToConnectUpStation(Section section) {
        return sections.getValues().stream()
                       .filter(s -> s.isEqualToUpStation(section.getUpStation()))
                       .findAny()
                       .orElseThrow(IllegalAccessError::new);
    }

    private void checkToAddNewSection(Integer originalDistance, Integer newDistance) {
        if (!isOriginalDistanceLongThenNew(originalDistance, newDistance)) {
            throw new IllegalArgumentException("역 사이에 등록할 경우 기존의 거리보다 짧은 구간만 등록이 가능합니다.");
        }
    }

    private boolean isOriginalDistanceLongThenNew(Integer originalDistance, Integer newDistance) {
        return newDistance < originalDistance;
    }


}
