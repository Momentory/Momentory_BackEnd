# 캐릭터 시스템 API 명세서 (업데이트)

## 📋 목차
1. [캐릭터 관리 API](#캐릭터-관리-api)
2. [아이템 관리 API](#아이템-관리-api)
3. [상점 API](#상점-api)
4. [옷장 API](#옷장-api)
5. [관리자 아이템 관리 API](#관리자-아이템-관리-api)
6. [공통 응답 형식](#공통-응답-형식)
7. [에러 코드](#에러-코드)

---

## 캐릭터 관리 API

### 1. 캐릭터 생성
**POST** `/api/characters`

새로운 캐릭터를 생성합니다. 같은 타입(CAT/DOG)의 캐릭터가 이미 존재하면 오류가 발생합니다.

#### Request Body
```json
{
  "characterType": "CAT"  // 또는 "DOG"
}
```

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "characterId": 1,
    "characterType": "CAT",
    "level": 1,
    "isCurrentCharacter": false,
    "equippedItems": {
      "clothing": null,
      "expression": null,
      "effect": null,
      "decoration": null
    }
  }
}
```

#### 에러 응답
- **CHARACTER4002**: 이미 해당 타입의 캐릭터가 존재합니다. (400 Bad Request)

---

### 2. 캐릭터 선택
**PATCH** `/api/characters/{characterId}/select`

특정 캐릭터를 현재 활성 캐릭터로 설정합니다.

#### Path Parameters
- `characterId` (Long): 선택할 캐릭터 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "characterId": 1,
    "characterType": "CAT",
    "level": 1,
    "isCurrentCharacter": true,
    "equippedItems": {
      "clothing": {
        "itemId": 3,
        "name": "리본셔츠",
        "imageUrl": "https://example.com/clothing3.png"
      },
      "expression": {
        "itemId": 12,
        "name": "웃는표정",
        "imageUrl": "https://example.com/expression12.png"
      },
      "effect": null,
      "decoration": null
    }
  }
}
```

#### 에러 응답
- **CHARACTER4001**: 캐릭터를 찾을 수 없습니다. (404 Not Found)
- **CHARACTER4003**: 해당 캐릭터에 접근할 권한이 없습니다. (403 Forbidden)

---

### 3. 현재 캐릭터 조회
**GET** `/api/characters/current`

현재 선택된 캐릭터와 그 캐릭터의 장착 아이템 목록을 조회합니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "characterId": 1,
    "characterType": "CAT",
    "level": 1,
    "equipped": {
      "clothing": {
        "itemId": 3,
        "name": "리본셔츠",
        "imageUrl": "https://example.com/clothing3.png"
      },
      "expression": {
        "itemId": 12,
        "name": "웃는표정",
        "imageUrl": "https://example.com/expression12.png"
      },
      "effect": null
    }
  }
}
```

#### 에러 응답
- **CHARACTER4004**: 현재 선택된 캐릭터가 없습니다. (404 Not Found)

---

### 4. 전체 캐릭터 목록 조회
**GET** `/api/characters`

사용자가 보유한 모든 캐릭터 목록을 조회합니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": [
    {
      "characterId": 1,
      "characterType": "CAT",
      "level": 1,
      "isCurrentCharacter": true,
      "equippedItems": {
        "clothing": {
          "itemId": 3,
          "name": "리본셔츠",
          "imageUrl": "https://example.com/clothing3.png"
        },
        "expression": {
          "itemId": 12,
          "name": "웃는표정",
          "imageUrl": "https://example.com/expression12.png"
        },
        "effect": null,
        "decoration": null
      }
    },
    {
      "characterId": 2,
      "characterType": "DOG",
      "level": 1,
      "isCurrentCharacter": false,
      "equippedItems": {
        "clothing": null,
        "expression": null,
        "effect": null
      }
    }
  ]
}
```

---

### 5. 아이템 착용
**PATCH** `/api/characters/{characterId}/equip/{itemId}`

캐릭터에 아이템을 착용합니다. 같은 카테고리의 아이템이 이미 착용되어 있으면 자동으로 해제됩니다.

#### Path Parameters
- `characterId` (Long): 캐릭터 ID
- `itemId` (Long): 착용할 아이템 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "characterId": 1,
    "characterType": "CAT",
    "level": 1,
    "isCurrentCharacter": true,
    "equippedItems": {
      "clothing": {
        "itemId": 3,
        "name": "리본셔츠",
        "imageUrl": "https://example.com/clothing3.png"
      },
      "expression": null,
      "effect": null,
      "decoration": null
    }
  }
}
```

#### 에러 응답
- **CHARACTER4001**: 캐릭터를 찾을 수 없습니다. (404 Not Found)
- **CHARACTER4003**: 해당 캐릭터에 접근할 권한이 없습니다. (403 Forbidden)
- **ITEM4002**: 보유하지 않은 아이템입니다. (400 Bad Request)

---

### 6. 아이템 해제
**PATCH** `/api/characters/{characterId}/unequip/{itemId}`

캐릭터에서 아이템을 해제합니다.

#### Path Parameters
- `characterId` (Long): 캐릭터 ID
- `itemId` (Long): 해제할 아이템 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "characterId": 1,
    "characterType": "CAT",
    "level": 1,
    "isCurrentCharacter": true,
    "equippedItems": {
      "clothing": null,
      "expression": null,
      "effect": null,
      "decoration": null
    }
  }
}
```

#### 에러 응답
- **CHARACTER4001**: 캐릭터를 찾을 수 없습니다. (404 Not Found)
- **CHARACTER4003**: 해당 캐릭터에 접근할 권한이 없습니다. (403 Forbidden)
- **ITEM4002**: 보유하지 않은 아이템입니다. (400 Bad Request)

---

## 아이템 관리 API

### 7. 내 아이템 목록 조회
**GET** `/api/items/mine`

사용자가 보유한 모든 아이템을 조회합니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": [
    {
      "itemId": 1,
      "name": "리본셔츠",
      "category": "CLOTHING",
      "imageUrl": "https://example.com/clothing1.png",
      "isEquipped": true
    },
    {
      "itemId": 5,
      "name": "웃는표정",
      "category": "EXPRESSION",
      "imageUrl": "https://example.com/expression5.png",
      "isEquipped": false
    },
    {
      "itemId": 10,
      "name": "별 이펙트",
      "category": "EFFECT",
      "imageUrl": "https://example.com/effect10.png",
      "isEquipped": false
    }
  ]
}
```

---

### 8. 랜덤 아이템 지급
**POST** `/api/items/random-reward`

사진 업로드 등의 이벤트로 랜덤 아이템을 지급받습니다. 이미 보유한 아이템은 제외됩니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "itemId": 15,
    "name": "신비로운 망토",
    "category": "CLOTHING",
    "imageUrl": "https://example.com/clothing15.png",
    "price": 500,
    "unlockLevel": 5,
    "isOwned": true,
    "isEquipped": false
  }
}
```

#### 에러 응답
- **ITEM4004**: 사용 가능한 아이템이 없습니다. (404 Not Found)
- **ITEM4005**: 모든 아이템을 보유하고 있습니다. (400 Bad Request)

---

## 상점 API

### 9. 상점 아이템 목록 조회
**GET** `/api/shop/items`

상점에서 구매 가능한 모든 아이템 목록을 조회합니다. 가격 오름차순으로 정렬되며, 보유 여부가 표시됩니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": [
    {
      "itemId": 1,
      "name": "리본셔츠",
      "category": "CLOTHING",
      "imageUrl": "https://example.com/clothing1.png",
      "price": 300,
      "unlockLevel": 1,
      "isOwned": true
    },
    {
      "itemId": 5,
      "name": "웃는표정",
      "category": "EXPRESSION",
      "imageUrl": "https://example.com/expression5.png",
      "price": 200,
      "unlockLevel": 1,
      "isOwned": false
    },
    {
      "itemId": 10,
      "name": "별 이펙트",
      "category": "EFFECT",
      "imageUrl": "https://example.com/effect10.png",
      "price": 500,
      "unlockLevel": 3,
      "isOwned": false
    }
  ]
}
```

---

### 10. 아이템 구매
**POST** `/api/shop/buy/{itemId}`

상점에서 아이템을 포인트로 구매합니다.

#### Path Parameters
- `itemId` (Long): 구매할 아이템 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "itemId": 5,
    "name": "웃는표정",
    "category": "EXPRESSION",
    "imageUrl": "https://example.com/expression5.png",
    "price": 200,
    "unlockLevel": 1,
    "isOwned": true,
    "isEquipped": false
  }
}
```

#### 에러 응답
- **ITEM4001**: 아이템을 찾을 수 없습니다. (404 Not Found)
- **ITEM4003**: 이미 보유한 아이템입니다. (400 Bad Request)
- **ITEM4006**: 포인트가 부족합니다. (400 Bad Request)

---

## 옷장 API

### 11. 옷장 슬롯 저장
**POST** `/api/wardrobe`

현재 캐릭터의 착용 상태를 옷장 슬롯으로 저장합니다.

Request Body 없음

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "wardrobeId": 1,
    "clothing": {
      "itemId": 3,
      "name": "리본셔츠",
      "imageUrl": "https://example.com/clothing3.png"
    },
    "expression": {
      "itemId": 12,
      "name": "웃는표정",
      "imageUrl": "https://example.com/expression12.png"
    },
    "effect": null,
    "decoration": null
  }
}
```

#### 에러 응답
- **CHARACTER4004**: 현재 선택된 캐릭터가 없습니다. (404 Not Found)

---

### 12. 옷장 목록 조회
**GET** `/api/wardrobe`

저장된 옷장 슬롯 목록을 조회합니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": [
    {
      "wardrobeId": 1,
      
      "clothing": {
        "itemId": 3,
        "name": "리본셔츠",
        "imageUrl": "https://example.com/clothing3.png"
      },
      "expression": {
        "itemId": 12,
        "name": "웃는표정",
        "imageUrl": "https://example.com/expression12.png"
      },
      "effect": null,
      "decoration": null
    },
    {
      "wardrobeId": 2,
      
      "clothing": {
        "itemId": 7,
        "name": "민소매",
        "imageUrl": "https://example.com/clothing7.png"
      },
      "expression": null,
      "effect": {
        "itemId": 15,
        "name": "해 이펙트",
        "imageUrl": "https://example.com/effect15.png"
      }
    }
  ]
}
```

---

### 13. 옷장 스타일 적용
**PATCH** `/api/wardrobe/{wardrobeId}/apply`

저장된 옷장 스타일을 현재 캐릭터에 적용합니다.

#### Path Parameters
- `wardrobeId` (Long): 적용할 옷장 슬롯 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "wardrobeId": 1,
    "clothing": {
      "itemId": 3,
      "name": "리본셔츠",
      "imageUrl": "https://example.com/clothing3.png"
    },
    "expression": {
      "itemId": 12,
      "name": "웃는표정",
      "imageUrl": "https://example.com/expression12.png"
    },
      "effect": null,
      "decoration": null
  }
}
```

#### 에러 응답
- **WARDROBE4001**: 옷장 슬롯을 찾을 수 없습니다. (404 Not Found)
- **WARDROBE4002**: 해당 옷장에 접근할 권한이 없습니다. (403 Forbidden)
- **CHARACTER4004**: 현재 선택된 캐릭터가 없습니다. (404 Not Found)

---

## 관리자 아이템 관리 API

### 14. 아이템 생성
**POST** `/api/admin/items`

새로운 캐릭터 아이템을 생성합니다.

#### Request Body
```json
{
  "name": "신비로운 망토",
  "category": "CLOTHING",
  "imageUrl": "https://example.com/clothing15.png",
  "price": 500,
  "unlockLevel": 3
}
```

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "itemId": 15,
    "name": "신비로운 망토",
    "category": "CLOTHING",
    "imageUrl": "https://example.com/clothing15.png",
    "price": 500,
    "unlockLevel": 3
  }
}
```

---

### 15. 아이템 목록 조회
**GET** `/api/admin/items`

모든 캐릭터 아이템 목록을 조회합니다.

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": [
    {
      "itemId": 1,
      "name": "리본셔츠",
      "category": "CLOTHING",
      "imageUrl": "https://example.com/clothing1.png",
      "price": 300,
      "unlockLevel": 1
    },
    {
      "itemId": 5,
      "name": "웃는표정",
      "category": "EXPRESSION",
      "imageUrl": "https://example.com/expression5.png",
      "price": 200,
      "unlockLevel": 1
    }
  ]
}
```

---

### 16. 아이템 상세 조회
**GET** `/api/admin/items/{itemId}`

특정 아이템의 상세 정보를 조회합니다.

#### Path Parameters
- `itemId` (Long): 조회할 아이템 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "itemId": 15,
    "name": "신비로운 망토",
    "category": "CLOTHING",
    "imageUrl": "https://example.com/clothing15.png",
    "price": 500,
    "unlockLevel": 3
  }
}
```

#### 에러 응답
- **ITEM4001**: 아이템을 찾을 수 없습니다. (404 Not Found)

---

### 17. 아이템 수정
**PUT** `/api/admin/items/{itemId}`

기존 아이템의 정보를 수정합니다.

#### Path Parameters
- `itemId` (Long): 수정할 아이템 ID

#### Request Body
```json
{
  "name": "수정된 망토",
  "category": "CLOTHING",
  "imageUrl": "https://example.com/clothing15_updated.png",
  "price": 600,
  "unlockLevel": 4
}
```

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": {
    "itemId": 15,
    "name": "수정된 망토",
    "category": "CLOTHING",
    "imageUrl": "https://example.com/clothing15_updated.png",
    "price": 600,
    "unlockLevel": 4
  }
}
```

#### 에러 응답
- **ITEM4001**: 아이템을 찾을 수 없습니다. (404 Not Found)

---

### 18. 아이템 삭제
**DELETE** `/api/admin/items/{itemId}`

아이템을 삭제합니다.

#### Path Parameters
- `itemId` (Long): 삭제할 아이템 ID

#### Response (Success - 200)
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": "아이템이 성공적으로 삭제되었습니다."
}
```

#### 에러 응답
- **ITEM4001**: 아이템을 찾을 수 없습니다. (404 Not Found)

---

## 공통 응답 형식

모든 API는 다음과 같은 공통 응답 형식을 사용합니다:

### 성공 응답
```json
{
  "isSuccess": true,
  "code": "COMMON200",
  "message": "요청에 성공하였습니다.",
  "result": { ... }  // 또는 [ ... ]
}
```

### 에러 응답
```json
{
  "isSuccess": false,
  "code": "ERROR_CODE",
  "message": "에러 메시지",
  "result": null
}
```

---

## 데이터 타입 및 Enum

### CharacterType
- `CAT`: 고양이
- `DOG`: 강아지

### ItemCategory
- `CLOTHING`: 의상
- `EXPRESSION`: 표정
- `EFFECT`: 이펙트
- `DECORATION`: 장식

---

## 에러 코드

### 캐릭터 관련 에러
| 코드 | HTTP 상태 | 메시지 |
|------|----------|--------|
| CHARACTER4001 | 404 | 캐릭터를 찾을 수 없습니다. |
| CHARACTER4002 | 400 | 이미 해당 타입의 캐릭터가 존재합니다. |
| CHARACTER4003 | 403 | 해당 캐릭터에 접근할 권한이 없습니다. |
| CHARACTER4004 | 404 | 현재 선택된 캐릭터가 없습니다. |

### 아이템 관련 에러
| 코드 | HTTP 상태 | 메시지 |
|------|----------|--------|
| ITEM4001 | 404 | 아이템을 찾을 수 없습니다. |
| ITEM4002 | 400 | 보유하지 않은 아이템입니다. |
| ITEM4003 | 400 | 이미 보유한 아이템입니다. |
| ITEM4004 | 404 | 사용 가능한 아이템이 없습니다. |
| ITEM4005 | 400 | 모든 아이템을 보유하고 있습니다. |
| ITEM4006 | 400 | 포인트가 부족합니다. |

### 옷장 관련 에러
| 코드 | HTTP 상태 | 메시지 |
|------|----------|--------|
| WARDROBE4001 | 404 | 옷장 슬롯을 찾을 수 없습니다. |
| WARDROBE4002 | 403 | 해당 옷장에 접근할 권한이 없습니다. |

---

## 인증

모든 API는 JWT 인증이 필요합니다. 요청 헤더에 다음을 포함해야 합니다:

```
Authorization: Bearer {JWT_TOKEN}
```

---

## 주요 기능 요약

1. **캐릭터 생성 및 선택**: 사용자는 CAT, DOG 타입의 캐릭터를 생성하고 선택할 수 있습니다.
2. **포인트 기반 레벨 시스템**: 사용자의 누적 포인트를 기준으로 캐릭터 레벨이 자동 계산됩니다.
3. **아이템 착용/해제**: 캐릭터에 아이템을 착용하거나 해제할 수 있습니다. 같은 카테고리의 아이템은 1개만 착용 가능합니다.
4. **상점 시스템**: 포인트로 아이템을 구매할 수 있습니다.
5. **랜덤 보상**: 사진 업로드 등 이벤트로 랜덤 아이템을 받을 수 있습니다.
6. **옷장 기능**: 현재 스타일을 저장하고 나중에 다시 적용할 수 있습니다.
7. **관리자 아이템 관리**: 관리자가 아이템을 생성, 수정, 삭제할 수 있습니다.

---

## 레벨 시스템

### 레벨 계산 공식
- **레벨 공식**: `Level = 1 + (누적 포인트 / 100)`
- **100포인트당 1레벨 증가**
- **최소 레벨**: 1

### 포인트 획득 방법
- 사진 업로드
- 사진 편집 사용
- 룰렛 사용
- 기타 이벤트

### 포인트 사용 방법
- 상점에서 아이템 구매

---

**최종 수정일**: 2024년

