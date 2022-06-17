package com.github.andylke.demo.support;

import org.springframework.core.NestedRuntimeException;

public class DeserializationFailedException extends NestedRuntimeException {

  private static final long serialVersionUID = 1L;

  public DeserializationFailedException(String msg, Throwable cause) {
    super(msg, cause);
  }

  public DeserializationFailedException(String msg) {
    super(msg);
  }
}
