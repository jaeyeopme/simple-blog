package me.jaeyeop.blog.post.application.port.out;

import me.jaeyeop.blog.post.domain.Post;

public interface PostCommandPort {

  Post create(Post post);

}
