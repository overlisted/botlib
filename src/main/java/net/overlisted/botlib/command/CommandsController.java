package net.overlisted.botlib.command;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandsController {
  /**
   * @return Token of the bot to control.
   */
  String token();

  /**
   * @return Group of commands
   */
  String commandsGroup();
}
