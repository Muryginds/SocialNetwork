package ru.skillbox.zerone.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tag")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull
  @Column(name = "tag")
  private String tag;

  @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY)
  private List<PostToTag> postToTags = new ArrayList<>();

  public void addPostToTag(PostToTag postToTag) {
    if (!postToTags.contains(postToTag)) {
      postToTags.add(postToTag);
      postToTag.setTag(this);
    }
  }

  public void removePostToTag(PostToTag postToTag) {
    if (postToTags.contains(postToTag)) {
      postToTags.remove(postToTag);
      postToTag.setTag(null);
    }
  }
}
