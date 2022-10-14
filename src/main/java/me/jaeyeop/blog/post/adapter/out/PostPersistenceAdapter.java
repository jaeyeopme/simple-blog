package me.jaeyeop.blog.post.adapter.out;

import java.util.Optional;
import me.jaeyeop.blog.post.adapter.in.response.PostInformation;
import me.jaeyeop.blog.post.application.port.out.PostCommandPort;
import me.jaeyeop.blog.post.application.port.out.PostQueryPort;
import me.jaeyeop.blog.post.domain.Post;
import org.springframework.stereotype.Component;

@Component
public class PostPersistenceAdapter implements PostCommandPort, PostQueryPort {

  private final PostCrudRepository postCrudRepository;

  private final PostQueryRepository postQueryRepository;

  public PostPersistenceAdapter(final PostCrudRepository postCrudRepository,
      final PostQueryRepository postQueryRepository) {
    this.postCrudRepository = postCrudRepository;
    this.postQueryRepository = postQueryRepository;
  }

  @Override
  public Post create(final Post post) {
    return postCrudRepository.save(post);
  }

  @Override
  public Optional<PostInformation> getPostInformationById(final Long id) {
    return postQueryRepository.getPostInformationById(id);
  }

  @Override
  public Optional<Post> findById(final Long id) {
    return postCrudRepository.findById(id);
  }

}
