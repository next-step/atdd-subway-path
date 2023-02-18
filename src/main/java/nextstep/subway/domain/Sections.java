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

    public boolean isEmpty() {
        return sections.isEmpty();
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
        findUpSection(section.getUpStation())
                .filter(it -> it.getDistance() <= section.getDistance())
                .ifPresent(it -> {
                    throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                });
    }

    private void validateDownStationDistance(Section section) {
        findDownSection(section.getDownStation())
                .filter(it -> it.getDistance() <= section.getDistance())
                .ifPresent(it -> {
                    throw new IllegalArgumentException("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음");
                });
    }

    private boolean hasUpToUp(Station upStation) {
        return findUpSection(upStation).isPresent();
    }

    private boolean hasDownToDown(Station downStation) {
        return findDownSection(downStation).isPresent();
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
        findUpSection(section.getUpStation())
                .ifPresent(it -> {
                    sections.remove(it);
                    sections.add(section);
                    sections.add(new Section(section.getLine(), section.getDownStation(), it.getDownStation(), it.getDistance() - section.getDistance()));
                });
    }

    private void addDownToDown(Section section) {
        findDownSection(section.getDownStation()).ifPresent(it -> {
            sections.remove(it);
            sections.add(new Section(section.getLine(), it.getUpStation(), section.getUpStation(), it.getDistance() - section.getDistance()));
            sections.add(section);
        });
    }

    private void addDownToBeginUp(Section section) {
        sections.forEach(Section::increaseOrder);

        section.beginOrder();
        sections.add(section);
    }

    private void addUpToEndDown(Section section) {
        sections.add(section);
    }

    private boolean hasMatchingStation(Station station) {
        return sections.stream()
                .anyMatch((it) -> station.equals(it.getUpStation()) || station.equals(it.getDownStation()));
    }

    private Optional<Section> findUpSection(Station station) {
        return sections.stream()
                .filter(it -> station.equals(it.getUpStation()))
                .findFirst();
    }

    private Optional<Section> findDownSection(Station station) {
        return sections.stream()
                .filter(it -> station.equals(it.getDownStation()))
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

    private void validateRemoveSection(Station station) {
        if(isEmpty()) {
            throw new IllegalArgumentException("노선에 구간이 없습니다");
        }

        if(hasOnlyOneSection()) {
            throw new IllegalArgumentException("노선에 구간이 하나밖에 없습니다");
        }

        if(notRegisteredStation(station)) {
            throw new IllegalArgumentException("노선에 등록되지 않은 역입니다");
        }
    }

    private boolean notRegisteredStation(Station station) {
        return !this.getStations().contains(station);
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

    private void removeLastSection() {
        sections.remove(findLastSection());
    }

    private void removeMiddleSection(Station station) {
        Section upSection = findUpSection(station).get();
        Section downSection = findDownSection(station).get();

        Line line = upSection.getLine();
        int distance = upSection.getDistance() + downSection.getDistance();
        Station upStation = downSection.getUpStation();
        Station downStation = upSection.getDownStation();
        int order = downSection.getOrder();

        int indexToInsert = sections.indexOf(downSection);
        sections.remove(downSection);
        sections.remove(upSection);
        sections.add(indexToInsert, new Section(line, upStation, downStation, distance, order));
    }
}
