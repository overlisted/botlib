# botlib
Start by adding a class with commands:
```java
package net.overlisted.mybot;

import net.overlisted.botlib.command.CommandTrigger;
import net.overlisted.botlib.command.CommandsController;

@CommandsController(
  token = "USE YOUR TOKEN",
  commandsGroup = "mybot"
)
public class MyBotCommands {
  @CommandTrigger
  public String test(Message message, String text) {
    return text;
  }
}
```

To initialize BotLib create a new instance of it:
```java
package net.overlisted.mybot;

import net.overlisted.botlib.BotLib;

public class StaticRunner {
  public static void main(String... args) {
    new BotLib("net.overlisted.mybot");
  }
}
```

Then add your bot to a Discord server and send "/mybot test anyText", bot will answer "anyText".

You can add unlimited amount of arguments (you will have to separate them with space to use the command) to commands, for example:
```java
@CommandTrigger
public void test(Message message, String text, String moreText, String text2) {
  message.getChannel().sendMessage(text).append(' ').append(moreText).append(' ').append(text2).submit();
}
```
Ad-Hoc also works for commands.
