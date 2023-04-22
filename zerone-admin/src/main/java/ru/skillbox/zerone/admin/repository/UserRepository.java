package ru.skillbox.zerone.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.skillbox.zerone.admin.model.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findUserByEmail(String email);

  List<User> findAllByPhotoNull();

  @Modifying
  @Query("""
    UPDATE User u set u.photo = null 
    where u in (select u from File f, User u where f.isStartAvatar and u.photo like f.url)
""")
  void clearPhotosWithStartAvatars();

  @Modifying
  @Query("""
    UPDATE User u set u.photo = null 
""")
  void clearAllPhotos();
}