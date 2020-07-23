package com.howuc.framework.secure;

import java.util.Set;

public interface AuthorizingService {
    Set<String> roles(long accountId);

    Set<String> permissions(long accountId);
}
