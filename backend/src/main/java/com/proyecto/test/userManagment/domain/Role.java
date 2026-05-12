package com.proyecto.test.userManagment.domain;

import java.util.Set;

public interface Role {
    String getNameRole();
    Set<String> getPermissions();
}
