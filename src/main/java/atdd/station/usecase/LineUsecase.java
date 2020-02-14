package atdd.station.usecase;

import atdd.station.entity.Edge;
import atdd.station.entity.Line;
import java.util.List;

public interface LineUsecase {
  LineDTO addLine(LineDTO line);
  ListWrapper<LineDTO> getLines();
  LineDTO getLine(Long lineID);
  void removeLine(Long lineID);
  EdgeDTO addEdge(EdgeDTO edge);
}
