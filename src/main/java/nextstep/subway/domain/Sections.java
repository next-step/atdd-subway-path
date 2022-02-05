package nextstep.subway.domain;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @Transient
    private static final int DELETABLE_SIZE = 2;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "section_id")
    private List<Section> sections = new LinkedList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        verifyCanBeAdded(section);

        var stations = getStations();
        if (stations.get(0).equals(section.getDownStation())) {
            sections.add(section);
            return;
        }

        if (stations.get(stations.size() - 1).equals(section.getUpStation())) {
            sections.add(section);
            return;
        }

        addSectionToBetween(section);
    }

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        var orderedStations = new ArrayList<Station>();
        var top = findFirstSection();
        orderedStations.add(top.getUpStation());

        var curSection = top;
        while (curSection != null) {
            orderedStations.add(curSection.getDownStation());
            curSection = findNextSection(curSection);
        }

        return Collections.unmodifiableList(orderedStations);
    }

    public List<Section> getOrderedSections() {
        var orderedSections = new ArrayList<Section>();

        var curSection = findFirstSection();
        while (curSection != null) {
            orderedSections.add(curSection);
            curSection = findNextSection(curSection);
        }

        return Collections.unmodifiableList(orderedSections);
    }

    public int size() {
        return sections.size();
    }

    private Section findFirstSection() {
        var downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());

        return sections.stream()
                .filter(it -> !downStations.contains(it.getUpStation()))
                .findAny()
                .orElseThrow(() -> new IllegalStateException("구간이 유효하지 않은 상태입니다."));
    }

    private Section findNextSection(Section section) {
        return sections.stream()
                .filter(it -> it.getUpStation().equals(section.getDownStation()))
                .findAny()
                .orElse(null);
    }

    private void addSectionToBetween(Section section) {
        var betweenSectionUp = sections.stream()
                .filter(it -> it.getUpStation().equals(section.getUpStation()))
                .findAny()
                .orElse(null);

        if (betweenSectionUp != null) {
            if (betweenSectionUp.getDistance() <= section.getDistance()) {
                throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같아서 추가할 수 없습니다.");
            }

            sections.add(section);
            var newSection = new Section(
                    section.getDownStation(),
                    betweenSectionUp.getDownStation(),
                    betweenSectionUp.getDistance() - section.getDistance()
            );
            sections.add(newSection);

            sections.remove(betweenSectionUp);
            return;
        }

        var betweenSectionDown = sections.stream()
                .filter(it -> it.getDownStation().equals(section.getDownStation()))
                .findAny()
                .orElse(null);

        if (betweenSectionDown != null) {
            if (betweenSectionDown.getDistance() <= section.getDistance()) {
                throw new IllegalArgumentException("기존 역 사이 길이보다 크거나 같아서 추가할 수 없습니다.");
            }

            sections.add(section);
            var newSection = new Section(
                    betweenSectionDown.getUpStation(),
                    section.getUpStation(),
                    betweenSectionDown.getDistance() - section.getDistance()
            );
            sections.add(newSection);

            sections.remove(betweenSectionDown);
        }
    }

    public void remove(Station station) {
        verifyCanBeDeleted(station);

        var selectedSection = getOrderedSections().stream()
                .filter(it -> it.hasStation(station))
                .collect(Collectors.toList());

        if (selectedSection.size() == 1) {
            sections.remove(selectedSection.get(0));
        } else {
            var upSection = selectedSection.stream()
                    .filter(it -> it.getDownStation().equals(station))
                    .findFirst()
                    .get();
            var downSection = selectedSection.stream()
                    .filter(it -> it.getUpStation().equals(station))
                    .findFirst()
                    .get();

            var newSection = new Section(
                    upSection.getUpStation(),
                    downSection.getDownStation(),
                    upSection.getDistance() + downSection.getDistance()
            );

            sections.remove(upSection);
            sections.remove(downSection);
            sections.add(newSection);
        }
    }

    private void verifyCanBeAdded(Section section) {
        if (sections.stream().filter(it -> it.hasStation(section.getUpStation())).findAny().isEmpty()
                && sections.stream().filter(it -> it.hasStation(section.getDownStation())).findAny().isEmpty()) {
            throw new IllegalArgumentException("상행역과 하행역 모두 등록되어 있지 않아서 추가할 수 없습니다.");
        }

        if (sections.stream().anyMatch(it ->
                it.getUpStation().equals(section.getUpStation())
                        && it.getDownStation().equals(section.getDownStation()))
        ) {
            throw new IllegalArgumentException("상행역과 하행역이 모두 등록되어 있어서 추가할 수 없습니다.");
        }
    }

    private void verifyCanBeDeleted(Station station) {
        if (sections.size() < DELETABLE_SIZE) {
            throw new IllegalArgumentException("노선에 구간이 부족하여 역을 삭제할 수 없습니다.");
        }
    }

}
