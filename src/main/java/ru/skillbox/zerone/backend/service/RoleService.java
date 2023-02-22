package ru.skillbox.zerone.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.model.entity.Role;
import ru.skillbox.zerone.backend.repository.RoleRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class RoleService {
  private static final String ROLE_USER = "ROLE_USER";

  private final RoleRepository roleRepository;

  public Role getBasicUserRole() {
    return roleRepository.findRoleByAuthority(ROLE_USER).orElseThrow(NoSuchElementException::new);
  }
}
