package me.jaeyeop.blog.commons.error.exception;

import lombok.Getter;
import me.jaeyeop.blog.commons.error.Error;

/**
 * @author jaeyeopme Created on 10/04/2022.
 */
@Getter
public abstract class AbstractBaseException extends RuntimeException {

  private final Error code;

  protected AbstractBaseException(final Error code) {
    super(code.message());
    this.code = code;
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

}
