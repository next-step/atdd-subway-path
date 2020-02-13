package atdd.station.service;

import atdd.station.entity.Line;
import atdd.station.repository.LineRepository;
import atdd.station.usecase.LineDTO;
import atdd.station.usecase.LineUsecase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LineService implements LineUsecase {

  @Autowired
  LineRepository lineRepository;

  private final LineModelMapper lineModelMapper = new LineModelMapper();

  @Override
  public LineDTO addLine(LineDTO line) {
    return lineModelMapper.EntityToDTO(
        lineRepository.save(
            lineModelMapper.DTOToEntity(line)
        )
    );
  }
}
