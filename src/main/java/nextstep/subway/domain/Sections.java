package nextstep.subway.domain;

import nextstep.subway.exception.DuplicateCreationException;
import nextstep.subway.exception.IllegalAddSectionException;
import nextstep.subway.exception.NotFoundException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
  @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
  private List<Section> sections = new ArrayList<>();

  public Sections(){}

  public Sections(List<Section> sections){
    this.sections = sections;
  }

  public void addSection(Section section){
    if(sections.isEmpty()){
      sections.add(section);
      return;
    }

    validateInsertion(section);

    // 상행에서 중간으로 이어지는 중간 부분 추가
    sections.stream()
      .filter(x -> x.getUpStation().equals(section.getUpStation())).findAny().ifPresent(x -> {
        x.isValidCreationDistance(section.getDistance());
        sections.add(new Section(section.getLine(), section.getDownStation(), x.getDownStation(), x.getDistance() - section.getDistance()));
        sections.remove(x);
    });

    // 상행의 위로 이어지는 상행 첫구간 추가
    sections.stream()
      .filter(x -> x.getDownStation().equals(section.getDownStation())).findAny().ifPresent(x -> {
        x.isValidCreationDistance(section.getDistance());
        sections.add(new Section(section.getLine(), x.getUpStation(), section.getUpStation(),x.getDistance() - section.getDistance()));
        sections.remove(x);
    });

    sections.add(section);
  }

  private void validateInsertion(Section section){
    // 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음
    boolean upStationIncluded = isUpStationSameNameIncluded(section.getUpStation());
    boolean downStationIncluded = isUpStationSameNameIncluded(section.getDownStation());

    if(upStationIncluded && downStationIncluded){
      throw new DuplicateCreationException();
    }

    if((!upStationIncluded) && (!downStationIncluded)){
      throw new IllegalAddSectionException();
    }

  }

  public boolean isEmpty(){
    return sections.isEmpty();
  }

  public List<Station> getSectionStations(){
    if(isEmpty()){
      return Collections.emptyList();
    }
    List<Station> stations = new ArrayList<>();
    Section firstUpSection = getFirstUpSection();
    stations.add(firstUpSection.getUpStation());
    stations.add(firstUpSection.getDownStation());
    Optional<Section> nextSection = getNextSection(firstUpSection.getDownStation());

    while(nextSection.isPresent()){
      stations.add(nextSection.get().getDownStation());
      nextSection = getNextSection(nextSection.get().getDownStation());
    }

    return stations;
  }

  public boolean isUpStationSameNameIncluded(Station station){
    return sections.stream()
      .anyMatch(s -> s.isIncludedStationsName(station));
  }

  public List<Station> getDownStations(){
    return sections.stream()
      .map(Section::getDownStation)
      .collect(Collectors.toList());
  }

  public Optional<Section> getLastSection(){
    if(sections.isEmpty()){
      return Optional.empty();
    }

    return Optional.of(sections.get(sections.size()-1));
  }

  public void deleteLastSection(Station station){

    Optional<Section> lastSection = getLastSection();
    // 구간이 없을 경우 처리 X
    if(!lastSection.isPresent()){
      throw new IllegalArgumentException();
    }

    if(!lastSection.get().getDownStation().equals(station)){
      throw new IllegalArgumentException();
    }

    sections.remove(sections.size() -1);
  }

  private Section getFirstUpSection(){
    List<Station> downStations = getDownStations();

    return sections.stream()
      .filter(x -> !downStations.contains(x.getUpStation()))
      .findAny()
      .orElseThrow(NotFoundException::new);
  }

  private Optional<Section> getNextSection(Station downStation){
    return sections.stream()
      .filter(x -> x.getUpStation().equals(downStation))
      .findFirst();
  }

}
