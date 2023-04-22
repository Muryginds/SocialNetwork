package ru.skillbox.zerone.backend.testData;

import ru.skillbox.zerone.backend.model.entity.Post;
import ru.skillbox.zerone.backend.model.entity.User;

public interface PostMockUtils {

  default Post getTestPost() {

    return Post.builder()
        .id(1L)
        .author(new User().setId(1L))
        .postText("TestText")
        .isDeleted(false)
        .isBlocked(false)
        .build();
  }
}
