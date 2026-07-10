## Related issue

Refs #

<!-- default branch 대상이고 merge 시 Issue를 닫을 때만 Closes #N을 사용하세요. -->

## Summary

<!-- 무엇을 왜 변경했는지 적어주세요. -->

## Hexagonal architecture impact

<!-- 변경한 input port, use case, domain, output port, adapter와 의존성 방향을 적어주세요. -->

- Inbound adapter / input port:
- Application / domain:
- Output port / outbound adapter:
- Existing boundary debt left unchanged:

## Compatibility and operations

<!-- 해당 없음은 None으로 적어주세요. -->

- API:
- DB / migration:
- Kafka / Redis:
- Security / rollout / rollback:

## Validation

<!-- 실행한 명령과 결과를 적어주세요. -->

```text
command -> result
```

## Checklist

- [ ] Issue의 Acceptance Criteria를 충족했다.
- [ ] 새 의존성이 헥사고날 경계 안쪽을 향한다.
- [ ] 필요한 테스트를 추가하거나 갱신했다.
- [ ] 관련 테스트와 전체 빌드를 실행했다.
- [ ] 비밀정보와 로컬 산출물이 포함되지 않았다.
- [ ] 문서와 운영 설정을 필요한 범위에서 갱신했다.
