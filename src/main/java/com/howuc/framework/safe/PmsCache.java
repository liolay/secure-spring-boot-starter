package com.howuc.framework.safe;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PmsCache {
    private long expireTime;
    private Set<String> pms;

    @JsonIgnore
    public boolean isExpired() {
        return Instant.now().getEpochSecond() > expireTime;
    }
}
