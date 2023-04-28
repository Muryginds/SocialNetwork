package ru.skillbox.zerone.backend.model.enumerated;


import lombok.Getter;

@Getter
public enum LikeType {
  POST("Post"), COMMENT("Comment");

  private final String type;

  LikeType(String type) {
    this.type = type;
  }

  public String getType() {
    return type;
  }
}
