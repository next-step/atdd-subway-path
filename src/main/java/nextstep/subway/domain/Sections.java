package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    List<Section> values = new ArrayList<>();
    private static int MIN_SIZE = 1;

    public void add(Section section) {
        ensureCanAddition(section);
        values.add(section);
    }

    private void ensureCanAddition(Section section) {
        if (values.isEmpty()) {
            return;
        }
        if (hasSection(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록 되어있는 구간은 등록할 수 없습니다.");
        }
    }

    public void remove(Station station) {
        ensureCanRemove(station);
        values.remove(lastSectionIndex());
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    private Station downStationTerminus() {
        return values.get(lastSectionIndex()).getDownStation();
    }

    private int lastSectionIndex() {
        return values.size() - 1;
    }

    private void ensureCanRemove(Station station) {
        if (values.size() < MIN_SIZE) {
            throw new IllegalArgumentException("두개 이상의 구간이 등록되어야 제거가 가능합니다.");
        }
        if (!downStationTerminus().equals(station)) {
            throw new IllegalArgumentException("하행 종점역이 아니면 제거할 수 없습니다.");
        }
    }

    private boolean hasSection(Section section) {
        return values.stream().anyMatch(s -> s.equals(section));
    }

}
