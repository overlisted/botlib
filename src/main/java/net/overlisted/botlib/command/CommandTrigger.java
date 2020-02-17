package net.overlisted.botlib.command;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandTrigger {
  /**
   * @return List of arguments in /help
   */
  String value();
}
