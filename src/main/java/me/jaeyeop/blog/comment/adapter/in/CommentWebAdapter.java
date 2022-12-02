package me.jaeyeop.blog.comment.adapter.in;

import static me.jaeyeop.blog.comment.adapter.in.CommentRequest.Create;
import static me.jaeyeop.blog.comment.adapter.in.CommentRequest.Update;
import static me.jaeyeop.blog.comment.adapter.out.CommentResponse.Info;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import me.jaeyeop.blog.comment.adapter.in.CommentRequest.Delete;
import me.jaeyeop.blog.comment.adapter.in.CommentRequest.Find;
import me.jaeyeop.blog.comment.application.port.in.CommentCommandUseCase;
import me.jaeyeop.blog.comment.application.port.in.CommentQueryUseCase;
import me.jaeyeop.blog.config.oas.spec.CommentOAS;
import me.jaeyeop.blog.config.security.authentication.Principal;
import me.jaeyeop.blog.config.security.authentication.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author jaeyeopme Created on 10/18/2022.
 */
@Validated
@RequestMapping(CommentWebAdapter.COMMENT_API_URI)
@RestController
public class CommentWebAdapter implements CommentOAS {

  public static final String COMMENT_API_URI = "/api/v1/comments";

  private final CommentCommandUseCase commentCommandUseCase;

  private final CommentQueryUseCase commentQueryUseCase;

  public CommentWebAdapter(
      final CommentCommandUseCase commentCommandUseCase,
      final CommentQueryUseCase commentQueryUseCase) {
    this.commentCommandUseCase = commentCommandUseCase;
    this.commentQueryUseCase = commentQueryUseCase;
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("/{id}")
  @Override
  public void delete(
      @Principal UserPrincipal principal,
      @PathVariable Long id) {
    final var request = new Delete(id);
    commentCommandUseCase.delete(principal.id(), request);
  }

  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{postId}")
  @Override
  public Page<Info> findPage(
      @PathVariable Long postId,
      @RequestParam(defaultValue = "0") @Min(0) @Max(99) int page,
      @RequestParam(defaultValue = "10") @Min(0) @Max(99) int size) {
    final var request = new Find(postId, PageRequest.of(page, size));
    return commentQueryUseCase.findCommentPage(request);
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PatchMapping("/{id}")
  @Override
  public void update(
      @Principal UserPrincipal principal,
      @PathVariable Long id,
      @RequestBody Update request) {
    commentCommandUseCase.update(principal.id(), id, request);
  }


  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping
  @Override
  public void create(
      @Principal UserPrincipal principal,
      @RequestBody Create request) {
    commentCommandUseCase.create(principal.id(), request);
  }

}
