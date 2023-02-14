package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public void addSection(Section section) {
        if(this.isEmpty()) {
            this.sections.add(section);
            return;
        }

        validateSection(section);

        if(hasUpToUp(section.getUpStation())) {
            addUpToUp(section);
            return;
        }

        if(hasDownToDown(section.getDownStation())) {
            addDownToDown(section);
            return;
        }

        if(hasDownToBeginUp(section.getDownStation())) {
            addDownToBeginUp(section);
            return;
        }

        if(hasUpToEndDown(section.getUpStation())) {
            addUpToEndDown(section);
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        if(sections.isEmpty()) {
            return stations;
        }

        sections.sort(Comparator.comparingInt(Section::getOrder));

        stations.add(findFirstSection().getUpStation());
        sections.forEach((it) -> stations.add(it.getDownStation()));

        return stations;
    }

    public int getGreatestOrder() {
        return sections.stream().max(Comparator.comparing(Section::getOrder))
                .orElse(new Section()).getOrder();
    }

    public boolean isEmpty() {
        return sections.isEmpty();
    }

    public void removeLastSection() {
        sections.remove(findLastSection());
    }

    public Integer[] getDistances() {
        return sections.stream()
                .map(Section::getDistance)
                .toArray(Integer[]::new);
    }

    private void validateSection(Section section) {
        validateMatchingSection(section);
        validateUnMatchingSection(section);
        validateDistance(section);
    }

    private void validateMatchingSection(Section newSection) {
        boolean matchUpStation = hasMatchingStation(newSection.getUpStation());
        boolean matchDownStation = hasMatchingStation(newSection.getDownStation());

        if(matchUpStation && matchDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음");
        }
    }

    private void validateUnMatchingSection(Section newSection) {
        boolean matchUpStation = hasMatchingStation(newSection.getUpStation());
        boolean matchDownStation = hasMatchingStation(newSection.getDownStation());

        if(!matchUpStation && !matchDownStation) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있지 않아 추가할 수 없음");
        }
    }

    private void validateDistance(Section section) {
        validateUpStationDistance(section);
        validateDownStationDistance(section);
    }


    private void validateUpStationDistance(Section section) {
        findUpToUp(section.getUpStation())
                .filter(it -> it.getDistance() <= section.getDistance())
                .ifPresent(it -> {
                    throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                });
    }

    private void validateDownStationDistance(Section section) {
        findDownToDown(section.getDownStation())
                .filter(it -> it.getDistance() <= section.getDistance())
                .ifPresent(it -> {
                    throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                });
    }

    private boolean hasUpToUp(Station upStation) {
        return findUpToUp(upStation).isPresent();
    }

    private boolean hasDownToDown(Station downStation) {
        return findDownToDown(downStation).isPresent();
    }

    private boolean hasDownToBeginUp(Station downStation) {
        Section beginSection = findFirstSection();
        return downStation.equals(beginSection.getUpStation());
    }

    private boolean hasUpToEndDown(Station upStation) {
        Section endSection = findLastSection();
        return upStation.equals(endSection.getDownStation());
    }

    private void addUpToUp(Section section) {
        findUpToUp(section.getUpStation())
                .ifPresent(it -> {
                    sections.remove(it);
                    sections.add(section);
                    sections.add(new Section(section.getLine(), section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()));
                });
    }

    private void addDownToDown(Section section) {
        findDownToDown(section.getDownStation()).ifPresent(it -> {
            sections.remove(it);
            sections.add(new Section(section.getLine(), it.getUpStation(), section.getUpStation(), it.getDistance() - section.getDistance()));
            sections.add(section);
        });
    }

    private void addDownToBeginUp(Section section) {
        Section firstSection = findFirstSection();
        firstSection.subtractDistance(section.getDistance());

        sections.forEach(Section::increaseOrder);

        section.beginOrder();
        sections.add(section);
    }

    private void addUpToEndDown(Section section) {
        Section lastSection = findLastSection();
        lastSection.subtractDistance(section.getDistance());

        sections.add(section);
    }

    private boolean hasMatchingStation(Station station) {
        return sections.stream()
                .anyMatch((it) -> station.equals(it.getUpStation()) || station.equals(it.getDownStation()));
    }

    private Optional<Section> findUpToUp(Station upStation) {
        return sections.stream()
                .filter(it -> upStation.equals(it.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findDownToDown(Station downStation) {
        return sections.stream()
                .filter(it -> downStation.equals(it.getDownStation()))
                .findFirst();
    }

    private Section findFirstSection() {
        return sections.stream().min(Comparator.comparing(Section::getOrder))
                .orElseThrow(() -> new EntityNotFoundException("노선에 구간이 없습니다"));
    }

    public Section findLastSection() {
        return sections.stream().max(Comparator.comparing(Section::getOrder))
                .orElseThrow(() -> new EntityNotFoundException("노선에 구간이 없습니다"));
    }

    public void removeSection(Station station) {
        validateRemoveSection(station);

        if(hasDownToEndDown(station)) {
            removeLastSection();
            return;
        }

        if(hasUpToBeginUp(station)) {
            removeFirstSection();
            return;
        }

        if(hasUpToUp(station) && hasDownToDown(station)) {
            removeMiddleSection(station);
        }
    }


    private void validateRemoveSection(Station station) {
        if(isEmpty()) {
            throw new IllegalArgumentException("노선에 구간이 없습니다");
        }

        if(hasOnlyOneSection()) {
            throw new IllegalArgumentException("노선에 구간이 하나밖에 없습니다");
        }
    }

    private boolean hasOnlyOneSection() {
        return sections.size() == 1;
    }

    private boolean hasDownToEndDown(Station downStation) {
        return findLastSection().getDownStation().equals(downStation);
    }

    private boolean hasUpToBeginUp(Station upStation) {
        return findFirstSection().getUpStation().equals(upStation);
    }

    private void removeFirstSection() {
        sections.remove(findFirstSection());
    }

    private void removeMiddleSection(Station station) {
        Section upToUp = findUpToUp(station).get();
        Section downToDown = findDownToDown(station).get();

        Line line = upToUp.getLine();
        int distance = upToUp.getDistance() + downToDown.getDistance();
        Station upStation = downToDown.getUpStation();
        Station downStation = upToUp.getDownStation();
        int order = downToDown.getOrder();

        int indexToInsert = sections.indexOf(downToDown);
        sections.remove(downToDown);
        sections.remove(upToUp);
        sections.add(indexToInsert, new Section(line, upStation, downStation, distance, order));
    }
}
