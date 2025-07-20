# Admin Dashboard Generator

OpenAPI/Swagger 명세서로부터 React 관리자 대시보드를 생성하고 동일한 Spring Boot 백엔드 서버에서 서빙하는 종합적인 시스템입니다.

## 프로젝트 구조

```
admin-master/
├── backend-sample/           # Kotlin Spring Boot API 샘플
│   ├── src/main/kotlin/     # 백엔드 소스 코드
│   ├── src/main/resources/  # 리소스 (정적 프론트엔드 포함)
│   ├── build.gradle.kts     # 프론트엔드 통합 빌드 설정
│   └── README.md            # 백엔드 관련 문서
├── frontend-generator/       # Node.js 프론트엔드 코드 생성기
│   ├── src/                 # 생성기 소스 코드
│   ├── templates/           # Handlebars 템플릿
│   ├── package.json         # 생성기 의존성
│   └── tsconfig.json        # TypeScript 설정
├── generated-frontend/       # 생성된 React 관리자 대시보드 (빌드 중 생성)
├── docker-compose.yml       # 프로덕션 배포
├── docker-compose.dev.yml   # 개발 환경
├── Dockerfile               # 프로덕션 컨테이너
├── Dockerfile.dev           # 개발 컨테이너
└── README.md               # 이 파일
```

## 주요 기능

### 백엔드 (Kotlin + Spring Boot)
- **인증 & 권한**: JWT 기반 인증과 역할 기반 접근 제어
- **사용자 관리**: 역할 할당이 포함된 전체 CRUD 작업
- **제품 관리**: 재고 추적이 포함된 인벤토리 관리
- **주문 관리**: 상태 추적이 포함된 주문 처리
- **대시보드 분석**: 실시간 통계 및 매출 분석
- **OpenAPI 문서**: 완전한 Swagger 문서

### 프론트엔드 생성기 (Node.js + TypeScript)
- **OpenAPI 파싱**: API 명세서 자동 파싱
- **코드 생성**: 완전한 React 관리자 대시보드 생성
- **TypeScript 지원**: 생성된 인터페이스를 통한 완전한 타입 안전성
- **UI 프레임워크 지원**: Material-UI, Ant Design, Chakra UI
- **인증 플로우**: JWT 토큰 관리 및 새로고침
- **CRUD 컴포넌트**: 생성된 테이블, 폼, 상세 뷰
- **역할 기반 접근**: 백엔드 권한을 존중하는 UI 컴포넌트

### 생성된 프론트엔드 (React + TypeScript)
- **모던 스택**: React 18, TypeScript, Vite
- **상태 관리**: 서버 상태를 위한 React Query
- **라우팅**: 보호된 라우트를 가진 React Router
- **폼**: 검증이 포함된 React Hook Form
- **데이터 테이블**: 페이지네이션이 포함된 정렬 가능, 필터링 가능한 테이블
- **반응형 디자인**: 모바일 친화적 인터페이스
- **인증**: 토큰 관리를 통한 로그인/로그아웃

## 시작하기

### 전제 조건
- JDK 17 이상
- Node.js 18 이상 (개발용)
- Docker & Docker Compose (컨테이너화된 배포용)

### Docker를 이용한 빠른 시작

1. **애플리케이션 복사 및 시작**:
   ```bash
   git clone <repository-url>
   cd admin-master
   docker-compose up --build
   ```

2. **애플리케이션 접근**:
   - **프론트엔드**: http://localhost:8080
   - **API 문서**: http://localhost:8080/swagger-ui.html
   - **H2 콘솔**: http://localhost:8080/h2-console

### 개발 설정

1. **백엔드 개발**:
   ```bash
   cd backend-sample
   ./gradlew bootRun
   ```

2. **프론트엔드 생성** (다른 터미널에서):
   ```bash
   cd frontend-generator
   npm install
   npm run build
   npm run generate -- --url http://localhost:8080/api-docs --output ../generated-frontend
   ```

3. **프론트엔드 개발**:
   ```bash
   cd generated-frontend
   npm install
   npm run dev  # http://localhost:3000에서 실행
   ```

### 통합 빌드

프론트엔드와 백엔드를 함께 빌드:
```bash
cd backend-sample
./gradlew buildFullstack
```

이것은 다음을 수행합니다:
1. 프론트엔드 생성기 빌드
2. React 관리자 대시보드 생성
3. 프론트엔드 빌드
4. 프론트엔드 빌드를 Spring Boot 정적 리소스로 복사
5. 임베디드 프론트엔드와 함께 백엔드 JAR 빌드

## 설정

### 백엔드 설정
`backend-sample/src/main/resources/application.yml`의 주요 설정:
- JWT 비밀키 및 만료 시간
- 데이터베이스 설정
- 서버 포트 및 컨텍스트 경로
- CORS 설정

### 프론트엔드 생성기 설정
생성 옵션 설정:
```bash
npm run generate -- \
  --url http://localhost:8080/api-docs \
  --output ./generated-frontend \
  --name "My Admin Dashboard" \
  --ui-framework material-ui \
  --base-url ""
```

옵션:
- `--url`: OpenAPI 명세서 URL
- `--output`: 생성된 코드의 출력 디렉토리
- `--name`: 프로젝트 이름
- `--ui-framework`: UI 라이브러리 (material-ui, ant-design, chakra-ui)
- `--base-url`: API 기본 URL (같은 서버의 경우 비워둠)

## 배포

### Docker를 이용한 프로덕션 배포

1. **빌드 및 배포**:
   ```bash
   docker-compose up --build -d
   ```

2. **애플리케이션 스케일링**:
   ```bash
   docker-compose up --scale admin-app=3 -d
   ```

3. **로그 보기**:
   ```bash
   docker-compose logs -f admin-app
   ```

### 수동 배포

1. **애플리케이션 빌드**:
   ```bash
   cd backend-sample
   ./gradlew buildFullstack
   ```

2. **JAR 실행**:
   ```bash
   java -jar build/libs/admin-api-0.0.1-SNAPSHOT.jar
   ```

### 환경 변수

환경 변수를 사용하여 애플리케이션 설정:
- `SPRING_PROFILES_ACTIVE`: 활성 Spring 프로파일
- `JWT_SECRET`: JWT 서명 비밀키
- `JWT_EXPIRATION`: JWT 토큰 만료 시간
- `DATABASE_URL`: 데이터베이스 연결 URL (외부 DB 사용 시)

## 기본 사용자

애플리케이션에 미리 설정된 사용자들:

| 사용자명 | 비밀번호 | 역할 |
|----------|----------|------|
| admin | password123 | ADMIN |
| manager | password123 | MANAGER |
| john.doe | password123 | USER |
| jane.smith | password123 | USER |

## API 문서

### 인증 엔드포인트
- `POST /api/auth/login` - 사용자 로그인
- `POST /api/auth/refresh` - JWT 토큰 새로고침
- `POST /api/auth/logout` - 사용자 로그아웃

### 관리 엔드포인트
- `GET /api/users` - 사용자 목록 (관리자만)
- `GET /api/products` - 제품 목록
- `GET /api/orders` - 주문 목록
- `GET /api/dashboard/stats` - 대시보드 통계

애플리케이션이 실행 중일 때 `/swagger-ui.html`에서 전체 API 문서를 사용할 수 있습니다.

## 개발 워크플로우

### 새로운 엔티티 추가

1. **백엔드 업데이트**:
   - `backend-sample/src/main/kotlin/com/example/admin/entity/`에 엔티티 클래스 추가
   - 리포지토리 인터페이스 생성
   - 서비스 레이어 구현
   - 적절한 OpenAPI 어노테이션으로 컨트롤러 추가

2. **프론트엔드 재생성**:
   ```bash
   cd frontend-generator
   npm run generate -- --url http://localhost:8080/api-docs --output ../generated-frontend
   ```

3. **애플리케이션 재빌드**:
   ```bash
   cd backend-sample
   ./gradlew buildFullstack
   ```

### 생성된 프론트엔드 커스터마이징

`frontend-generator/templates/`의 Handlebars 템플릿을 수정하여 생성된 코드를 커스터마이징:

- `components/` - React 컴포넌트 템플릿
- `pages/` - 페이지 컴포넌트 템플릿
- `services/` - API 서비스 템플릿
- `types/` - TypeScript 인터페이스 템플릿

### 테스팅

**백엔드 테스트**:
```bash
cd backend-sample
./gradlew test
```

**프론트엔드 테스트** (테스트 지원으로 생성된 경우):
```bash
cd generated-frontend
npm test
```

## 아키텍처 결정

### 단일 서버 배포
프론트엔드는 Spring Boot 백엔드의 정적 리소스로 서빙되어 CORS 문제를 제거하고 배포를 단순화합니다.

### 코드 생성 접근법
일반적인 관리자 템플릿보다는 API 스키마에 특별히 맞춤화된 코드를 생성하여 더 나은 타입 안전성과 커스터마이징 옵션을 제공합니다.

### 빌드 통합
프론트엔드 빌드가 Gradle 빌드 프로세스에 통합되어 일관성을 보장하고 단일 명령 배포를 가능하게 합니다.

## 문제 해결

### 일반적인 문제

1. **포트 충돌**: 8080 및 3000 포트가 사용 가능한지 확인
2. **Node.js 버전**: Node.js 18 이상 사용
3. **메모리 문제**: `-Xmx2g`로 JVM 힙 크기 증가
4. **권한 오류**: `gradlew`가 실행 가능한지 확인: `chmod +x gradlew`

### 도움 받기

- 로그 확인: `docker-compose logs admin-app`
- API 실행 확인: `curl http://localhost:8080/actuator/health`
- 프론트엔드 빌드 확인: `ls -la backend-sample/src/main/resources/static/`

더 많은 도움이 필요하면 리포지토리에 이슈를 열어주세요.