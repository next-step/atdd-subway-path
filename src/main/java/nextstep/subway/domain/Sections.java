package nextstep.subway.domain;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.ui.BusinessException;
import org.hibernate.annotations.SortComparator;

@Embeddable
public class Sections {

  @SortComparator(SectionComparator.class)
  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private final SortedSet<Section> sections = new TreeSet<>();

  public void addSection(final Section newSection) {
    // 첫 번째 구간은 검증 제외
    AddSectionValidator.validate(this, newSection);

    // TODO 로직 이관
    /**
     *    중간 구간을 앞에서부터 분할
     *          origin
     *   |-------------------|
     *       new
     *   |---------|
     *
     *           result
     *       new      origin
     *   |---------|---------|
     */
    getBeforeSection(newSection).ifPresent(
        it -> it.updateDownStation(newSection.getUpStation(), it.getDistance() - newSection.getDistance())
    );

    // TODO 로직 이관
    /**
     *    중간 구간을 뒤에서부터 분할
     *          origin
     *   |-------------------|
     *                 new
     *             |---------|
     *
     *           result
     *     origin      new
     *   |---------|---------|
     */
    getAfterSection(newSection).ifPresent(
        it -> it.updateUpStation(newSection.getDownStation(), it.getDistance() - newSection.getDistance())
    );

    this.sections.add(newSection);
  }

  public void removeSection(final Long stationId) {
    RemoveSectionValidator.validate(this);

    // TODO 2단계 과제
    final var section = sections.stream()
        .filter(it -> it.getDownStation().getId().equals(stationId))
        .findAny()
        .orElseThrow(() -> new BusinessException("구간 정보를 찾을 수 없습니다."));

    this.sections.remove(section);
  }

  public List<Station> getStations() {
    if (sections.isEmpty()) {
      return Collections.emptyList();
    }

    final var stations = sections.stream()
        .map(Section::getUpStation)
        .collect(Collectors.toList());
    stations.add(sections.last().getDownStation());

    return stations;
  }

  public Optional<Station> getFirstStation() {
    if (sections.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(sections.first().getUpStation());
  }

  public Optional<Station> getLastStation() {
    if (sections.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(sections.last().getDownStation());
  }

  // TODO 메소드 이름 변경
  public Optional<Section> getBeforeSection(final Section section) {
    return sections.stream()
        .filter(it -> it.isDownStation(section))
        .findFirst();
  }

  // TODO 메소드 이름 변경
  public Optional<Section> getAfterSection(final Section section) {
    return sections.stream()
        .filter(it -> it.isUpStation(section))
        .findFirst();
  }

  public boolean contains(final Section section) {
    return sections.contains(section);
  }

  public int size() {
    return sections.size();
  }

  protected boolean isEmpty() {
    return sections.isEmpty();
  }

  public static class SectionComparator implements Comparator<Section> {

    @Override
    public int compare(Section left, Section right) {
      if (left.getUpStation().getId().equals(right.getUpStation().getId())
          && left.getDownStation().getId().equals(right.getDownStation().getId())) {
        return 0;
      }

      return left.getDownStation().getId().equals(right.getUpStation().getId()) ? -1 : 1;
    }
  }
}
