package com.TeamAA.TeamDo.validation;

import jakarta.validation.GroupSequence;

/**
 * 회원가입 요청 검증 순서
 * (향후 Pattern / 중복검사 그룹 확장 대비)
 */
@GroupSequence({
        BlankCheck.class,
        SizeCheck.class
})
public interface SignupValidationSequence {
}
