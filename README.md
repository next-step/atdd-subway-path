# 지하철 노선도 미션

[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

## 🚀 실습 - 단위 테스트 작성

### 기능 요구사항

- [x] 지하철 구간 관련 단위 테스트를 완성하세요.
- [x] 구간 단위 테스트 (LineTest)
- [x] 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
- [x] 구간 서비스 단위 테스트 without Mock (LineServiceTest)
- [x] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하세요.

## 🚀 1단계 - 지하철 구간 추가 기능 개선

### 기능 요구사항

- [x] 요구사항 설명에서 제공되는 추가된 요구사항을 기반으로 지하철 구간 관리 기능을 리팩터링하세요.
- [x] 추가된 요구사항을 정의한 인수 조건을 도출하세요.
- [x] 인수 조건을 검증하는 인수 테스트를 작성하세요.
- [x] 예외 케이스에 대한 검증도 포함하세요.

### 프로그래밍 요구사항

- [x] 인수 테스트 주도 개발 프로세스에 맞춰서 기능을 구현하세요.
- [x] 요구사항 설명을 참고하여 인수 조건을 정의
- [x] 인수 조건을 검증하는 인수 테스트 작성
- [x] 인수 테스트를 충족하는 기능 구현
- [x] 인수 조건은 인수 테스트 메서드 상단에 주석으로 작성하세요.
- [x] 뼈대 코드의 인수 테스트를 참고
- [x] 인수 테스트 이후 기능 구현은 TDD로 진행하세요.
- [x] 도메인 레이어 테스트는 필수
- [x] 서비스 레이어 테스트는 선택

## 🚀 2단계 - 지하철 구간 제거 기능 개선

### 기능 요구사항

- [ ] 요구사항 설명에서 제공되는 추가된 요구사항을 기반으로 지하철 구간 관리 기능을 리팩터링하세요.
- [x] 추가된 요구사항을 정의한 인수 조건을 도출하세요.
- [x] 인수 조건을 검증하는 인수 테스트를 작성하세요.
- [x] 예외 케이스에 대한 검증도 포함하세요.

### 변경된 스펙

- [ ] 구간 삭제에 대한 제약 사항 변경 구현
- [ ] 기존에는 마지막 역 삭제만 가능했는데 위치에 상관 없이 삭제가 가능하도록 수정
    - 예시) 노선에 등록되어있지 않은 역을 제거하려 한다.
    - 구간이 하나인 노선에서 마지막 구간을 제거 할 수 없음
- [ ] 종점이 제거될 경우 다음으로 오던 역이 종점이 됨
- [ ] 중간역이 제거될 경우 재배치를 함
- [ ] 노선에 A - B - C 역이 연결되어 있을 때 B역을 제거할 경우 A - C로 재배치 됨
- [ ] 거리는 두 구간의 거리의 합으로 정함