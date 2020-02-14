package atdd.station.usecase;

import java.io.Serializable;
import java.util.List;
import lombok.Getter;

@Getter
public class ListWrapper<T> implements Serializable {
  int totalResults;
  List<T> results;

  public ListWrapper(List<T> listDTOs) {
    totalResults = listDTOs.size();
    results = listDTOs;
  }
}
