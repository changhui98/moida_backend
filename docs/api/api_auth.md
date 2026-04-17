# Auth API 명세서

> Base URL: `http://localhost:8080/api/v1/auth`
> 인증 불필요 (Public)

---

## 1. 회원가입

| 항목 | 내용 |
|------|------|
| **메서드** | `POST` |
| **엔드포인트** | `/api/v1/auth/sign-up` |
| **인증** | 불필요 |

### Request Body

```json
{
  "username": "user4",
  "password": "Test1234!",
  "nickname": "유저사",
  "userEmail": "user4@moida.com",
  "address": "서울 강동구 천호동"
}
```

| 필드 | 타입 | 필수 | 제약 |
|------|------|------|------|
| username | String | ✓ | 영문자만, 공백 불가 |
| password | String | ✓ | 최소 8자, 대문자·소문자·특수문자 각 1개 이상 |
| nickname | String | ✓ | 4~10자, 한글·영문·숫자만 |
| userEmail | String | ✓ | 이메일 형식 |
| address | String | ✓ | 한글·영문·숫자·공백만 |

### Response `201 Created`

```json
{
  "id": "b3f2a1e0-1234-5678-abcd-000000000010",
  "username": "user4",
  "createdAt": "2026-04-17T15:00:00"
}
```

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 중복 username | `409 Conflict` |
| 유효성 검사 실패 | `400 Bad Request` |

---

## 2. 로그인

| 항목 | 내용 |
|------|------|
| **메서드** | `POST` |
| **엔드포인트** | `/api/v1/auth/sign-in` |
| **인증** | 불필요 |

### Request Body

```json
{
  "username": "user1",
  "password": "Test1234!"
}
```

### Response `200 OK`

- Body 없음
- **Response Header**에 JWT 토큰 반환

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYWFhYWFhYS0wMDAwLTAwMDAtMDAwMC0wMDAwMDAwMDAwMDIiLCJ1c2VybmFtZSI6InVzZXIxIiwicm9sZXMiOiJVU0VSIiwiaWF0IjoxNzQ1MDAwMDAwLCJleHAiOjE3NDUwMDM2MDB9.example_signature
```

> 이후 모든 인증 필요 API는 `Authorization: Bearer {token}` 헤더에 포함

### Error

| 상황 | 상태 코드 |
|------|-----------|
| 아이디/비밀번호 불일치 | `401 Unauthorized` |
