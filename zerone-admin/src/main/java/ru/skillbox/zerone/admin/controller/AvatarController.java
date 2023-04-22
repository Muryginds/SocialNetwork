package ru.skillbox.zerone.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.zerone.admin.config.properties.AvatarProperties;
import ru.skillbox.zerone.admin.service.AvatarService;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/admin/avatars")
public class AvatarController {
  private static final String AVATARS = "avatars";
  private final AvatarProperties avatarProperties;
  private final AvatarService avatarService;

  @GetMapping
  public String index(Model model) {
    String avatarUrl = avatarProperties.getUrl();
    model.addAttribute("avatarUrl", avatarUrl);
    return AVATARS;
  }

  @PostMapping
  public String postFolderWithAvatars(@RequestParam("file") MultipartFile[] fileList, Model model) {
    avatarService.updateStartAvatars(fileList);
    model.addAttribute("success", "Аватарки успешно обновлены!!!");
    return AVATARS;
  }

  @GetMapping("/fill")
  public String fillAvatarsForNullPhotos(Model model) {
    avatarService.fillAvatarsForNullPhotos();
    model.addAttribute("filled", "Аватарки присоединены к пользователям");
    return AVATARS;
  }

  @GetMapping("/deleteStart")
  public String deleteStartAvatars(Model model) {
    avatarService.deleteStartAvatars();
    model.addAttribute("startDeleted", "Стартовые аватарки удалены");
    return AVATARS;
  }

  @GetMapping("/deleteAll")
  public String deleteAllAvatars(Model model) {
    avatarService.deleteAllAvatars();
    model.addAttribute("allDeleted", "Все аватарки удалены");
    return AVATARS;
  }
}
