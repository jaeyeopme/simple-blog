package me.jaeyeop.blog.comment.domain;

import java.util.ArrayList;
import java.util.List;
import me.jaeyeop.blog.comment.adapter.out.response.CommentInfo;
import me.jaeyeop.blog.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public abstract class CommentFactory {

  private static final String DEFAULT_CONTENT = "content";

  private static final String DEFAULT_AUTHOR_NAME = "author";

  public static Comment createComment1WithAuthor(final User author) {
    return Comment.builder()
        .id(1L)
        .content(DEFAULT_CONTENT)
        .author(author)
        .build();
  }

  public static Page<CommentInfo> createPageInfo(final PageRequest commentPageable) {
    List<CommentInfo> content = new ArrayList<>();

    for (int i = commentPageable.getPageNumber(); i < commentPageable.getPageSize(); i++) {
      content.add(createInfo((long) i));
    }

    return new PageImpl<>(content, commentPageable, content.size());
  }

  private static CommentInfo createInfo(final Long id) {
    return new CommentInfo(
        id,
        DEFAULT_CONTENT,
        DEFAULT_AUTHOR_NAME,
        null,
        null);
  }

}
