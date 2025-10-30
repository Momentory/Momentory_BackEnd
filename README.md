# Momentory_BackEnd

## 옷장(Wardrobe) 기능 요약

- 현재 선택된 캐릭터의 착용 상태(CLOTHING, EXPRESSION, EFFECT)를 슬롯으로 저장합니다.
- 옷장 슬롯은 이름 없이 저장되며, 슬롯 ID(`wardrobeId`)로 식별합니다.
- 저장된 슬롯을 적용하면 현재 캐릭터의 착용 상태가 해당 슬롯의 아이템 조합으로 갱신됩니다.

### 엔드포인트

- POST `/api/wardrobe` — 현재 착용 상태를 슬롯으로 저장 (Request Body 없음)
- GET `/api/wardrobe` — 내 옷장 슬롯 목록 조회
- PATCH `/api/wardrobe/{wardrobeId}/apply` — 슬롯 적용

### 응답 예시 (목록)

```json
[
  {
    "wardrobeId": 1,
    "clothing": { "itemId": 3, "name": "리본셔츠", "imageUrl": "..." },
    "expression": { "itemId": 12, "name": "웃는표정", "imageUrl": "..." },
    "effect": null
  }
]
```