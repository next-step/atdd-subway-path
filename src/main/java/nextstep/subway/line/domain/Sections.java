package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.criteria.CriteriaBuilder.In;
import nextstep.subway.common.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

  private static final int MIN = 1;
  private static final int EMPTY = 0;

  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
      CascadeType.MERGE}, orphanRemoval = true)
  private List<Section> sections = new ArrayList<>();

  public Sections() {
  }

  private void validateSection(Station upStation, Station downStation) {
    if (sections.size() < MIN) {
      return;
    }
    if (isValidUpStation(upStation)) {
      throw new InvalidSectionException("상행역은 현재 노선의 하행 종점역이어야 합니다.");
    }
    if (isValidDownStation(downStation)) {
      throw new InvalidSectionException("하행역은 노선에 이미 등록되어 있습니다.");
    }
  }

  private int calculateNewDistance(int newDistance, int oldDistance) {
    return oldDistance - newDistance;
  }
  private void validateDistance(int newDistance, int oldDistance) {
    if(newDistance >= oldDistance) throw new RuntimeException("기존 구간의 길이보다 길거나 같습니다.");
  }

  public void add(Line line, Station upStation, Station downStation, int distance) {
    //validateSection(upStation, downStation);
    if(sections.isEmpty()) {
      sections.add(new Section(line, upStation, downStation, distance));
      return;
    }
    boolean isRegisteredUpStation = isRegisteredStation(upStation);
    boolean isRegisteredDownStation = isRegisteredStation(downStation);


    if(isRegisteredDownStation && isRegisteredUpStation) {
      throw new InvalidSectionException("등록하려는 구간의 상행역/하행역 모두 이미 등록되어있습니다.");
    }

    if(!isRegisteredDownStation && !isRegisteredUpStation) {
      throw new InvalidSectionException("등록하려는 구간의 상행역과 하행역중 하나는 노선에 포함되어있어야합니다.");
    }
    if(isRegisteredUpStation) {
      prepend(line,upStation,downStation,distance);

    }
    if(isRegisteredDownStation) {
      append(line,upStation,downStation,distance);
    }




  }

  public void append(Line line, Station upStation, Station downStation, int distance) {
    Section target = sections.stream()
        .filter(section -> section.getDownStation().equals(downStation))
        .findFirst()
        .orElseGet(() -> null);

    if(Objects.isNull(target)){
      sections.add(new Section(line, upStation, downStation, distance));
      return;
    }
    validateDistance(distance,target.getDistance());
    int targetIndex = sections.indexOf(target);
    int newDistance = calculateNewDistance(distance,target.getDistance());
    sections.set(targetIndex,new Section(line,upStation,downStation,distance));
    sections.add(targetIndex,new Section(line,target.getUpStation(),upStation,newDistance));
  }

  public void prepend(Line line, Station upStation, Station downStation, int distance) {
    Section target = sections.stream()
        .filter(section -> section.getUpStation().equals(upStation))
        .findFirst()
        .orElseGet(()->null);
    if(Objects.isNull(target)){
      sections.add(new Section(line, upStation, downStation, distance));
      return;
    }
    validateDistance(distance,target.getDistance());
    int targetIndex = sections.indexOf(target);
    int newDistance = calculateNewDistance(distance,target.getDistance());
    sections.set(targetIndex,new Section(line,upStation,downStation,distance));
    sections.add(targetIndex+1,new Section(line,downStation,target.getDownStation(),newDistance));
  }

  public void remove(long stationId) {
    if (getSize() == EMPTY) {
      throw new InvalidSectionException("삭제할 구간이 없습니다.");
    }
    if (getSize() == MIN) {
      throw new InvalidSectionException("구간이 1개남은경우 삭제할 수 없습니다.");
    }
    if (!isLastStation(stationId)) {
      throw new InvalidSectionException("노선의 종점이 아닌경우 삭제할 수 없습니다.");
    }
    sections.remove(lastIndex());
  }

  public int getSize() {
    return this.sections.size();
  }

  private int lastIndex() {
    return getSize() - 1;
  }

  private boolean isValidUpStation(Station upStation) {
    return !sections.get(lastIndex())
        .getDownStation()
        .equals(upStation);
  }

  private boolean isValidDownStation(Station downStation) {
    return sections.stream()
        .anyMatch(section -> section.getUpStation().equals(downStation) || section.getDownStation()
            .equals(downStation));
  }

  public boolean isLastStation(long stationId) {
    return sections.get(lastIndex())
        .getDownStation().getId()
        .equals(stationId);
  }


  public List<Station> getSortedStations() {
    return sections.stream()
        .sorted()
        .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
        .distinct()
        .collect(Collectors.toList());
  }

  public List<Section> getSortedSection() {
    return sections.stream().sorted().collect(Collectors.toList());
  }

  public boolean isRegisteredStation(Station station) {
    return sections.stream()
        .anyMatch(section -> Objects.equals(section.getUpStation(),station)
            || Objects.equals(section.getDownStation(),station));
  }
}
