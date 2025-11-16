# Momentory Backend

> 추억을 순간순간 기록하고 공유하는 위치 기반 사진 SNS 플랫폼

## 프로젝트 소개

Momentory는 사진을 통해 추억을 기록하고, 지역 기반 스탬프를 수집하며, 캐릭터를 성장시키는 소셜 플랫폼입니다.
사용자는 여행지에서 찍은 사진을 업로드하고, 지역 스탬프와 문화 스탬프를 수집하며, 포인트를 획득하여 캐릭터를 꾸밀 수 있습니다.

## 주요 기능

### 📸 사진 관리
- 사진 업로드 및 위치 정보 자동 추출
- 앨범별 사진 관리 및 공유
- 사진 공개/비공개 설정
- 커서 기반 페이지네이션

### 🗺️ 지역 기반 시스템
- GPS 기반 지역 스탬프 자동 수집
- 문화시설 근처 사진 촬영 시 문화 스탬프 획득
- 중복 스탬프 자동 필터링
- Tour API 연동 문화시설 정보 제공

### 🎯 포인트 & 레벨
- 활동별 포인트 획득 (사진 업로드, 좋아요, 팔로우 등)
- 일일 제한으로 공정한 포인트 시스템
- 누적 포인트 기반 레벨 자동 계산
- 레벨업 시 보너스 포인트 지급
- 동시성 제어로 중복 보상 방지

### 🎮 캐릭터 시스템
- 다양한 캐릭터 타입 (고양이, 강아지 등)
- 포인트로 아이템 구매 및 캐릭터 커스터마이징
- 의상, 표정, 효과, 장식 카테고리별 아이템 장착
- 레벨에 따른 캐릭터 성장

### 🎲 룰렛 이벤트
- 포인트를 소비하여 룰렛 실행
- 지역 스탬프 인증으로 추가 보상 획득
- 확률 기반 리워드 시스템

### 👥 커뮤니티
- 팔로우/팔로워 시스템
- 사용자 프로필 조회 및 관리
- 좋아요 및 댓글 기능

### 🔔 알림
- 실시간 알림 (WebSocket)
- 레벨업, 팔로우, 좋아요 등 다양한 알림 타입
- 알림 설정 관리

### 🔐 인증 & 보안
- JWT 기반 인증
- 카카오 소셜 로그인
- 이메일 인증 (회원가입 시)
- Refresh Token 자동 갱신

## 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.3.4**
- **Spring Data JPA** - ORM 및 데이터 접근
- **QueryDSL** - 타입 안전한 쿼리 작성
- **Spring Security** - 인증 및 권한 관리
- **JWT** - 토큰 기반 인증
- **OAuth2** - 카카오 소셜 로그인

### Database
- **MySQL 8** - 메인 데이터베이스
- Hibernate - JPA 구현체

### Infrastructure
- **AWS S3** - 이미지 파일 스토리지
- **WebSocket** - 실시간 알림

### API Documentation
- **SpringDoc OpenAPI (Swagger)** - API 문서 자동화

### External APIs
- **카카오 맵 API** - 위치 정보 변환
- **Tour API** - 문화시설 정보

## 프로젝트 구조

```
src/main/java/com/example/momentory
├── domain
│   ├── auth          # 인증/인가
│   ├── user          # 사용자 관리
│   ├── photo         # 사진 관리
│   ├── album         # 앨범 관리
│   ├── character     # 캐릭터 시스템
│   ├── point         # 포인트 시스템
│   ├── stamp         # 스탬프 수집
│   ├── map           # 지역/지도 기능
│   ├── roulette      # 룰렛 게임
│   ├── community     # 커뮤니티 기능
│   ├── notification  # 알림
│   ├── home          # 홈 화면
│   ├── file          # 파일 업로드 (S3)
│   └── tag           # 태그 시스템
└── global
    ├── config        # 설정
    ├── security      # 보안 설정
    ├── exception     # 예외 처리
    └── common        # 공통 유틸리티
```

## 시작하기

### 필수 요구사항

- Java 17 이상
- MySQL 8.0 이상
- Gradle 7.0 이상

### 환경 변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 다음 환경 변수를 설정합니다:

```properties
# Database
DB_URL=jdbc:mysql://localhost:3306/momentory
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password

# JWT
JWT_SECRET=your_jwt_secret_key
JWT_EXPIRATION=86400000
REFRESH_TOKEN_EXPIRATION=604800000

# AWS S3
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
AWS_REGION=ap-northeast-2
AWS_BUCKET=your_bucket_name

# Kakao OAuth
KAKAO_CLIENT_ID=your_kakao_client_id
KAKAO_CLIENT_SECRET=your_kakao_client_secret

# Email
APP_EMAIL=your_email@gmail.com
APP_PASSWORD=your_email_app_password

# Kakao Map API
KAKAO_REST_API_KEY=your_kakao_rest_api_key

# Tour API
TOUR_API_KEY=your_tour_api_key
```

### 빌드 및 실행

```bash
# 프로젝트 클론
git clone https://github.com/your-username/momentory-backend.git
cd momentory-backend

# 의존성 설치 및 빌드
./gradlew clean build

# 애플리케이션 실행
./gradlew bootRun
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

### API 문서 확인

애플리케이션 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다:

```
http://localhost:8080/swagger-ui/index.html
```

## 주요 API 엔드포인트

### 인증
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/signin` - 로그인
- `POST /api/auth/refresh` - 토큰 갱신
- `GET /login/oauth2/code/kakao` - 카카오 로그인 콜백

### 사진
- `POST /api/photos` - 사진 업로드
- `GET /api/photos/{photoId}` - 사진 조회
- `PUT /api/photos/{photoId}` - 사진 수정
- `DELETE /api/photos/{photoId}` - 사진 삭제

### 스탬프
- `GET /api/stamps` - 내 스탬프 목록 조회
- `POST /api/stamps/cultural` - 문화 스탬프 획득

### 캐릭터
- `GET /api/characters/current` - 현재 캐릭터 조회
- `POST /api/characters` - 캐릭터 생성
- `POST /api/characters/{characterId}/select` - 캐릭터 선택
- `POST /api/characters/{characterId}/equip/{itemId}` - 아이템 장착

### 포인트
- `GET /api/points` - 포인트 조회
- `GET /api/points/history` - 포인트 내역 조회

## 데이터베이스 스키마

주요 테이블:
- `users` - 사용자 정보
- `user_profiles` - 사용자 프로필
- `photos` - 사진 정보
- `albums` - 앨범
- `characters` - 캐릭터
- `items` - 아이템
- `user_items` - 사용자 보유 아이템
- `stamps` - 스탬프
- `point_histories` - 포인트 내역
- `level_up_histories` - 레벨업 이력 (중복 방지)
- `notifications` - 알림

## 핵심 기능 구현

### 중복 방지 시스템

**레벨업 보상 중복 방지**
```java
// level_up_histories 테이블에 (user_id, reached_level) 유니크 제약조건
// 동시성 문제는 DataIntegrityViolationException으로 처리
```

**문화 스탬프 중복 방지**
```java
// 이미 받은 스탬프는 hasNearbyCulturalSpots = false
boolean alreadyHasStamp = stampRepository.existsByUserAndSpotName(user, spotName);
```

### 포인트 시스템

**일일 제한**
- 사진 업로드: 3회/일
- 좋아요: 50회/일
- 팔로우: 20회/일

**포인트 종류**
- 회원가입: 500p
- 사진 업로드: 50p
- 레벨업: 200p
- 좋아요 받기: 5p
- 팔로워 증가: 10p
- 룰렛 인증: 500p

### 레벨 시스템

레벨은 누적 포인트 기반으로 자동 계산:
- 레벨 1: 0p
- 레벨 2: 100p
- 레벨 3: 300p
- 레벨 N: `100 * (N-1) * N / 2`

## 보안

- JWT 기반 Stateless 인증
- Refresh Token으로 보안성 강화
- CORS 설정으로 허용된 출처만 접근 가능
- SQL Injection 방지 (Prepared Statement)
- XSS 방지 (입력 검증)

## 개발 환경

```yaml
spring:
  profiles:
    active: local
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

## 트러블슈팅

### 빌드 실패 시
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### 데이터베이스 연결 실패 시
- MySQL 서버 실행 확인
- `.env` 파일의 DB 설정 확인
- 데이터베이스 생성 확인: `CREATE DATABASE momentory;`

## 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 라이선스

This project is licensed under the MIT License.

## 연락처

프로젝트 관련 문의: momomentory@gmail.com

---

⭐ Star this repository if you find it helpful!
