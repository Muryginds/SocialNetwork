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

  public static LikeType findByName(String name) {
    for (LikeType typeLike : values()) {
      if (typeLike.getType().equalsIgnoreCase(name)) {
        return typeLike;
      }
    }
    throw new IllegalArgumentException(String.format("No enum value found for type %s", name));
  }
}
