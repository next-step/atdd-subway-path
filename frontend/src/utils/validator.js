const validator = {
  path: {
    source: [],
    target: []
  },
  departureTime: {
    dayTime: [],
    hour: [],
    minute: []
  },
  stationName: [(v) => !!v || '이름 입력이 필요합니다.', (v) => v.length > 0 || '이름은 1글자 이상 입력해야 합니다.'],
  line: {
    name: [(v) => !!v || '이름 입력이 필요합니다.'],
    color: [(v) => !!v || '색상 입력이 필요합니다.'],
  },
  section: {
    upStationId: [(v) => !!v || '상행역을 선택하세요.'],
    downStationId: [(v) => !!v || '하행역을 선택하세요.'],
    distance: [(v) => !!v || '거리 입력이 필요합니다.']
  },
  member: {
    email: [(v) => !!v || '이메일 입력이 필요합니다.', (v) => /.+@.+/.test(v) || '유효한 이메일을 입력해주세요'],
    age: [(v) => !!v || '나이 입력이 필요합니다.', (v) => v > 0 || '나이는 1살 이상 이어야 합니다.'],
    password: [(v) => !!v || '비밀번호 입력이 필요합니다.'],
    confirmPassword: [(v) => !!v || '비밀번호 확인이 필요합니다.', (v, c) => v === c || '비밀번호가 일치하지 않습니다.']
  }
}

export default validator
