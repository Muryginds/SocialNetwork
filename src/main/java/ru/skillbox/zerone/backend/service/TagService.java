package ru.skillbox.zerone.backend.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.zerone.backend.repository.TagRepository;

@Service
@AllArgsConstructor
public class TagService {

  private final TagRepository tagRepository;

}