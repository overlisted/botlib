package net.overlisted.botlib.command;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandsEventListener extends ListenerAdapter {
  private static final Pattern commandPattern = Pattern.compile(
    "^/(?<commandGroup>[^\\s]+)\\s(?<commandName>[^\\s]+)(?<commandArgs>\\s.+)*$"
  );

  private final Method[] commandTriggers;

  public CommandsEventListener(Method[] commandTriggers) {
    this.commandTriggers = commandTriggers;
  }

  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    try {
      Matcher matcher = commandPattern.matcher(event.getMessage().getContentDisplay());

      if(matcher.matches()) {
        List<Object> args = new ArrayList<>();
        args.add(event.getMessage());
        if(matcher.group("commandArgs") != null) {
          char[] argumentsCharArray = matcher.group("commandArgs").toCharArray();

          StringBuilder iArgument = new StringBuilder();
          for(int i = 0; i < argumentsCharArray.length; i++) {
            if(argumentsCharArray[i] == ' ') continue;
            iArgument.append(argumentsCharArray[i]);
            if(
              (argumentsCharArray.length > i + 1 && argumentsCharArray[i + 1] == ' ')
              || i == argumentsCharArray.length - 1
            ) {
              args.add(iArgument.toString());
              iArgument = new StringBuilder();
            }
          }
        }

        for(Method it: this.commandTriggers) {
          if(
            matcher.group("commandGroup").equals(
              it.getDeclaringClass().getAnnotation(CommandsController.class).commandsGroup()
            )
              && matcher.group("commandName").equals(it.getName())
              && args.size() == it.getParameterCount()
          ) {
            it.invoke(it.getDeclaringClass().getDeclaredConstructor().newInstance(), args.toArray(new Object[0]));
          }
        }
      }
    } catch(Throwable e) {
      e.printStackTrace();
    }
  }
}
