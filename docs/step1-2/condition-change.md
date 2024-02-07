# 요구사항 변경에 따른 기존과 변경 비교 분석 및 대응 전략


## 기존 요구사항

1. 새로운 구간의 상행역은 해당 노선에 등록되어 있는 하행 종점역이어야 함.
2. 이미 해당 노선에 등록되어 있는 역은 새로운 구간의 하행역이 될 수 없음.
3. 새로운 구간 등록 시 위 조건에 부합하지 않는 경우 에러 처리.

## 변경된 요구사항

1. 노선에 역을 위치에 상관없이 추가할 수 있음 (노선 가운데, 노선 처음).
2. 이미 등록되어 있는 역은 노선에 등록될 수 없음.

## 비교 분석

### 베타적 조건의 변화 
  - 위치에 상관없이 역을 추가할 수 있게 함으로써, 상행역이 하행 종점역이어야 한다는 조건이 해제된다.
  - 위치에 상관없이 역을 추가할 수 있게 함으로써, 해당 노선에 등록되어있는 역은 새로운 구간의 하행역이 될 수 없다는 조건이 해제된다.
### 검증 조건의 변화
- 변경된 요구사항에 따라, 기존에는 상행역이 하행 종점역이어야 한다는 조건과 하행역이 이미 노선에 등록되어 있으면 안 된다는 조건을 검증해야 했지만, 이제는 위치에 상관없이 역을 추가할 수 있으므로, 상행역이 하행 종점역인지에 대한 검증은 더 이상 필요하지 않다. 
- 대신, 역이 이미 노선에 등록되어 있는지 여부만 검증하면 된다.

## 수정 

### 기존 조건 제거
- 상행역이 하행 종점역이어야 한다는 조건과 해당 노선에 등록되어 있는 역은 새로운 구간의 하행역이어야 한다는 조건을 제거해야 한다.

### 코드에서의 검증 로직 삭제 

```java
@Override
@Transactional
public SectionInfo addSection(Long lineId, SectionCreateCommand createCommand) {
    // 생략 
    
    if (isNotSatisfiedByCreationRule(createCommand, line)) {
        throw new SectionCreationNotValidException();
    }
    
    // 생략 
}

private boolean isNotSatisfiedByCreationRule(SectionCreateCommand createCommand, Line line) {
	return isRequestedDownStationDuplicate(createCommand, line) || isRequestedUpStationNotAnExpectedDownStation(createCommand, line);
	}

private boolean isRequestedUpStationNotAnExpectedDownStation(SectionCreateCommand createCommand, Line line) {
	return line.isNotDownEndStation(createCommand.getUpStationId());
	}

private boolean isRequestedDownStationDuplicate(SectionCreateCommand createCommand, Line line) {
	return line.isContainsAnyStation(createCommand.getDownStationId());
	}
```

### 새로운 조건 추가 
- 노선 가운데나 처음에 역을 추가할 수 있도록, 구간을 조정하는 `adjustSections` 메서드의 로직을 추가하여, 새로운 구간이 기존 구간 사이에 삽입될 수 있도록 구현 해야 한다. 
- 또한 기존의 검증 로직을 삭제하면서, 변경된 요구사항에 따른 `이미 등록되어있는 역은 노선에 등록될 수 없다.`는 요구사항에 대한 검증 로직을 추가한다.
