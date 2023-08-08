package subway.domain;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SubwaySections {

    private final Map<Station.Id, SubwaySection> subwaySections;

    public SubwaySections(SubwaySection subwaySection) {
        subwaySections = new HashMap<>();
        subwaySections.put(subwaySection.getUpStationId(), subwaySection);
    }

    public SubwaySections(List<SubwaySection> subwaySectionList) {
        subwaySections =
                subwaySectionList
                        .stream()
                        .collect(Collectors.toMap(SubwaySection::getUpStationId, Function.identity()));
    }

    public void register(SubwaySection section) {
        subwaySections.put(section.getUpStationId(), section);
    }

    public int size() {
        return subwaySections.size();
    }

    boolean isEmpty() {
        return subwaySections.isEmpty();
    }

    public Optional<SubwaySection> getSectionMatchedUpStation(Station.Id stationId) {
        SubwaySection subwaySection = subwaySections.get(stationId);
        return Optional.ofNullable(subwaySection);
    }

    public Optional<SubwaySection> getSectionMatchedUpStation(Station station) {
        return getSectionMatchedUpStation(station.getId());
    }

    public Optional<SubwaySection> getSectionMatchedDownStation(Station station) {
        return subwaySections
                .values()
                .stream()
                .filter(subwaySection -> subwaySection.isSameDownStation(station))
                .findAny();
    }

    public Station.Id getStartStationId() {
        List<Station.Id> downStationIds = subwaySections
                .values()
                .stream()
                .map(SubwaySection::getDownStationId)
                .collect(Collectors.toList());

        Optional<Station.Id> result = this.subwaySections
                .keySet()
                .stream()
                .filter(stationId -> !downStationIds.contains(stationId))
                .findAny();

        if (result.isEmpty()) {
            validate();
        }
        return result.get();
    }

    public boolean existsUpStation(Station.Id stationId) {
        return subwaySections.containsKey(stationId);
    }

    public boolean existsUpStation(Station station) {
        return existsUpStation(station.getId());
    }

    public boolean existsDownStation(Station station) {
        return existsDownStation(station.getId());
    }

    public boolean existsDownStation(Station.Id stationId) {
        return existsStationBy(subwaySection -> subwaySection.isSameDownStation(stationId));
    }

    public void validate() {
        if (isEmpty()) {
            throw new IllegalArgumentException("구간이 비어있습니다.");
        }
        if (isDuplicated()) {
            throw new IllegalArgumentException("구간이 중복되어있습니다.");
        }
        if (isCircular()) {
            throw new IllegalArgumentException("구간이 순환되어있습니다.");
        }
        if (!isConnected()) {
            throw new IllegalArgumentException("구간이 연결되어있지 않습니다.");
        }

        subwaySections.values().forEach(SubwaySection::validate);
    }

    boolean isCircular() {
        return this.subwaySections
                .values()
                .stream()
                .filter(subwaySection -> subwaySections.containsKey(subwaySection.getDownStationId()))
                .count() == this.size();
    }

    boolean isDuplicated() {
        return subwaySections
                .values()
                .stream()
                .map(SubwaySection::getDownStationId)
                .distinct()
                .count() != this.size();
    }

    boolean isConnected() {
        return this.subwaySections
                .values()
                .stream()
                .filter(subwaySection -> subwaySections.containsKey(subwaySection.getDownStationId()))
                .count() == this.size() - 1;
    }

    public List<SubwaySection> getSections() {
        return new ArrayList<>(subwaySections.values());
    }

    public void close(Station station) {
        SubwaySection subwaySection =
                getSectionMatchedUpStation(station)
                        .orElseThrow(() -> new IllegalArgumentException(String.format("%s 역은 현재 노선에 존재하지 않은 역입니다.", station.getName())));
        close(subwaySection);
    }

    public void closeTail(Station station) {
        SubwaySection subwaySection =
                getSectionMatchedDownStation(station)
                        .orElseThrow(() -> new IllegalArgumentException(String.format("%s 역은 현재 노선에 존재하지 않은 역입니다.", station.getName())));
        close(subwaySection);
    }

    private void close(SubwaySection section) {
        Objects.requireNonNull(this.subwaySections.remove(section.getUpStationId()), String.format("%d 역은 현재 노선에 존재하지 않은 역입니다.", section.getUpStationId().getValue()));
        return;
    }

    private Optional<SubwaySection> getSectionBy(Predicate<SubwaySection> predicate) {
        return subwaySections
                .values()
                .stream()
                .filter(predicate)
                .findAny();
    }

    public void reduceSection(SubwaySection newSection) {
        SubwaySection subwaySection = getDuplicatedSection(newSection)
                .orElseThrow(IllegalArgumentException::new);
        close(subwaySection);
        subwaySection.reduce(newSection);
        register(subwaySection);
    }

    private Optional<SubwaySection> getDuplicatedSection(SubwaySection newSection) {
        Optional<SubwaySection> upStationOptional = getSectionMatchedUpStation(newSection.getUpStationId());

        if (upStationOptional.isPresent()) {
            return upStationOptional;
        }
        Optional<SubwaySection> downStationOptional = getSectionMatchedUpStation(newSection.getDownStationId());

        if (downStationOptional.isPresent()) {
            return downStationOptional;
        }

        return getSectionBy(section -> section.isSameDownStation(newSection.getDownStation()));
    }

    public boolean hasDuplicateSection(SubwaySection section) {
        if (this.existsUpStation(section.getUpStationId()))
            return true;
        return existsDownStation(section.getDownStationId());
    }

    private boolean existsStationBy(Predicate<SubwaySection> predicate) {
        return subwaySections
                .values()
                .stream()
                .anyMatch(predicate);
    }

    public void extendSection(Station station) {
        SubwaySection closeSection = getSectionMatchedUpStation(station)
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s 역은 현재 노선에 존재하지 않은 역입니다.", station.getName())));
        SubwaySection extendSection = getSectionMatchedDownStation(station)
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s 역은 현재 노선에 존재하지 않은 역입니다.", station.getName())));
        extendSection.extend(closeSection);
    }
}
