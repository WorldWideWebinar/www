package com.ssafy.globalcc.domain.user.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class DuplicateCheckResult {
    private boolean isAvailable;
}
