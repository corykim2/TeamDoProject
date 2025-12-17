package com.TeamAA.TeamDo.validation;

import jakarta.validation.GroupSequence;

/**
 * 로그인 요청 검증 순서
 * 1. 공백 체크
 * 2. 길이 체크
 */
@GroupSequence({
        BlankCheck.class,
        SizeCheck.class
})

public interface LoginValidationSequence {
}
