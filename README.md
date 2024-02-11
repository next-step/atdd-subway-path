# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

---
# 🚀 실습 - 단위 테스트 작성

## 기능 요구사항
- [x] 지하철 구간 관련 단위 테스트를 완성하세요.
  - [x] 구간 단위 테스트 (SectionsTest)
  - [x] 구간 서비스 단위 테스트 with Mock (LineServiceMockTest)
  - [x] 구간 서비스 단위 테스트 without Mock (LineServiceTest)
  - [x] 단위 테스트를 기반으로 비즈니스 로직을 리팩터링 하세요.

## 24.02.06 Feedback
- [x] Line을 생성할 때 구간을 1개 포함하도록 모델링 수정(테스트-프로덕션 간 상태 불일치 해소)
  - Line 생성 시 생성자에 Section 생성을 위한 인자를 받도록 수정

---
# 🚀 1단계 - 구간 추가 요구사항 반영

## 요구사항
- 사용자 스토리를 기반으로 기능 요구사항이 도출되었다.
- 완료 조건을 검증 인수 조건을 시나리오 형태로 도출하고, 인수 테스트를 작성한다.
### 사용자 스토리
> 사용자로서 지하철 노선도를 조금 더 편리하게 관리하기 위해
> 
> 위치에 상관 없이 지하철 노선에 역을 추가할 수 있다.
### 기능 요구사항(완료 조건)
> 노선에 역 추가시 노선 가운데 추가 할 수 있다. ( A-B + A-C = A-C-B )
> 
> 노선에 역 추가시 노선 처음에 추가할 수 있다. ( A-B + C-A = C-A-B )
> 
> 이미 등록된 역은 노선에 등록될 수 없다.
- 인수 테스트 시나리오 작성
  - [x] 노선에 역 추가시 노선 가운데 추가 할 수 있다.
    - given A-C 구간을 보유한 노선을 생성하고
    - when A-B 구간을 추가하면
    - then 2개의 구간을 가진 노선 정보를 응답받을 수 있다.
  - [x] 노선에 역 추가시 노선 처음에 추가 할 수 있다.
    - given B-C 구간을 보유한 노선을 생성하고
    - when A-B 구간을 추가하면
    - then A, B, C 3개의 역을 가진 노선 정보를 응답받을 수 있다.

- addSection() 수행시 고려 사항
  - [x] 기존 구간과 완전히 같은 구간정보는 등록할 수 없다. ( A-B-C-D + B-C = Exception )
  - [x] 이미 등록된 역은 노선에 등록될 수 없다.  ( A-B-C-D + C-A = Exception )
  - [x] 추가 위치가 중간 / 처음 / 마지막인지 파악한다.
    - [x] 중간인 경우 상행역 정보를 기준으로 추가된다.
      - [x] 새 구간의 길이는 새로운 길이를 뺀 나머지이다.
    - [x] 처음인 경우 노선의 상행 종점역으로 판단한다.
    - [x] 마지막인 경우 노선의 하행 종점역으로 판단한다.
## 프로그래밍 요구사항
- [x] 인수 테스트 주도 개발 프로세스에 맞춰 기능 구현
  - 인수 조건 정의 > 인수 테스트 작성 > 인수 테스트를 충족하는 기능 구현
- [x] 인수 조건은 인수 테스트 상단에 주석으로 작성

## Feedback 24.02.09
- [x] 중간 구간 추가 시 기존 역 존재 여부 확인 필요
  - Sections 단위 테스트 추가
- [x] 3항 연산을 더욱 간소화할 수 있다.
- [x] Sections의 isFirstSection() 메서드를 조금 더 명확하게 구현
- [x] 미사용 import문 제거

---
# 🚀 2단계 - 구간 제거 요구사항 반영

## 요구사항
- 완료 조건을 검증 인수 조건을 시나리오 형태로 도출하고, 인수 테스트를 작성한다.
### 사용자 스토리
> 사용자로서 지하철 노선도를 조금 더 편리하게 관리하기 위해
>
> 위치에 상관없이 지하철 노선에 역을 제거 할 수 있다
### 기능 요구사항(완료 조건)
> 노선에 등록된 역 제거 시 해당 역이 노선 가운데 있어도 제거할 수 있다.
> 
> 노선에 등록된 역 제거 시 해당 역이 상행 종점역이어도 제거할 수 있다.
- 인수 테스트 시나리오 작성
  - [ ] 노선의 중간 역을 제거할 수 있다.
    - given A-B-C 구간을 보유한 노선을 생성하고
    - when B 역을 삭제하면
    - then A-C 1개의 구간을 가진 노선 정보를 응답받을 수 있다.
  - [x] 노선의 상행 종점역을 제거할 수 있다.
    - given A-B-C 구간을 보유한 노선을 생성하고
    - when A 역을 삭제하면
    - then B-C 1개의 구간을 가진 노선 정보를 응답받을 수 있다.
  - [x] 노선의 하행 종점역을 제거할 수 있다.
    - given A-B-C 구간을 보유한 노선을 생성하고
    - when C 역을 삭제하면
    - then A-B 1개의 구간을 가진 노선 정보를 응답받을 수 있다.
- deleteSection() 수행시 고려 사항
  - [ ] 중간 역을 삭제하는 경우 거리는 다음으로 이어진 두 구간의 합으로 재정의된다.

## 프로그래밍 요구사항
- [ ] 인수 테스트 주도 개발 프로세스에 맞춰 기능 구현
  - 인수 조건 정의 > 인수 테스트 작성 > 인수 테스트를 충족하는 기능 구현
- [ ] 인수 조건은 인수 테스트 상단에 주석으로 작성
