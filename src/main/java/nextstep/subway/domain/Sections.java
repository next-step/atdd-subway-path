package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.exception.InvalidMatchEndStationException;
import nextstep.subway.domain.exception.NotExistSectionException;
import nextstep.subway.domain.exception.SectionDeleteException;
import nextstep.subway.domain.exception.StationAlreadyExistsException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Sections {

    private static final int EMPTY_VALUE = 0;
    private static final int ONE = 1;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> values = new ArrayList<>();

    public List<Station> getStations() {
        return values.stream()
                .map(Section::getStations)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section section) {
        if (!values.isEmpty()) {
            validateSection(section);
        }
        this.values.add(section);
    }

    private void validateSection(Section additionalSection) {
        Section lastSection = findLastSection();
        if (!lastSection.isMatchDownStation(additionalSection.getUpStation())) {
            throw new InvalidMatchEndStationException(additionalSection.getUpStation().getId());
        }
        if(this.hasStation(additionalSection.getDownStation())) {
            throw new StationAlreadyExistsException(additionalSection.getDownStation().getId());
        }
    }

    private boolean hasStation(Station station) {
        return values.stream().anyMatch(section -> section.hasStation(station));
    }

    public void delete(Station station) {
        Section lastSection = findLastSection();
        if (!lastSection.isMatchDownStation(station)) {
            throw new SectionDeleteException(station.getId());
        }
        values.remove(lastSection);
    }

    private Section findLastSection() {
        if (lastIndex() < EMPTY_VALUE) {
            throw new NotExistSectionException();
        }
        return values.get(lastIndex());
    }

    private int lastIndex() {
        return values.size() - ONE;
    }
}
