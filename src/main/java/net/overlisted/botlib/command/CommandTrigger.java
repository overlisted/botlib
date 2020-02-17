package net.overlisted.botlib.command;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandTrigger {
  /**
   * @return Command name in /help
   */
  String beatifiedName();

  /**
   * @return List of arguments in /help
   */
  String args();
}
