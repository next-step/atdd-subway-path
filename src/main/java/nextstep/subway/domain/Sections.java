package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.SubwayException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Section> list = new ArrayList<>();
    private static int MIN_SIZE = 1;

    public void add(Section section) {
        ensureCanAddition(section.getUpStation());
        list.add(section);
    }

    private void ensureCanAddition(Station station) {
        if (list.isEmpty()) {
            return;
        }
        if (!downStationTerminus().equals(station)) {
            throw new IllegalArgumentException("상행역과 하행역 종점역이 동일한 경우에 등록할 수 있습니다.");
        }
    }

    public void remove(Station station) {
        ensureCanRemove(station);
        list.remove(lastSectionIndex());
    }

    private void ensureCanRemove(Station station) {
        if (list.isEmpty()) {
            throw new SubwayException("등록된 구간이 없습니다.");
        }
        if (!downStationTerminus().equals(station)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 제거할 수 없습니다.");
        }
        if (list.size() <= MIN_SIZE) {
            throw new IllegalArgumentException("두개 이상의 구간이 등록되어야 제거가 가능합니다.");
        }
    }

    public boolean isEmptySections() {
        return list.isEmpty();
    }

    private Station downStationTerminus() {
        return list.get(lastSectionIndex()).getDownStation();
    }

    private int lastSectionIndex() {
        return list.size() - 1;
    }

}
