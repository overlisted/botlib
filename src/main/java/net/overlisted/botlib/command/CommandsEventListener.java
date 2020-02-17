package net.overlisted.botlib.command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

  private final Class<?> controller;
  private final Object controllerInstance;
  private final Method[] commandTriggers;

  public CommandsEventListener(Class<?> controller, Method[] commandTriggers) throws Throwable {
    this.controller = controller;
    this.controllerInstance = controller.getDeclaredConstructor().newInstance();
    this.commandTriggers = commandTriggers;
  }

  public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
    try {
      if(event.getMessage().getContentRaw().equals("/help")) {
        EmbedBuilder builder = new EmbedBuilder();

        final CommandsController controller = this.controller.getAnnotation(CommandsController.class);

        builder.setAuthor(controller.nameBeautified());
        builder.setTitle("Commands");

        for(Method commandTrigger: this.commandTriggers) {
          final CommandTrigger trigger = commandTrigger.getAnnotation(CommandTrigger.class);

          builder.addField(
            trigger.beatifiedName(),
            "/" + controller.commandsGroup() + " " + commandTrigger.getName() + " " + trigger.args(),
            false
          );
        }

        event.getChannel().sendMessage(builder.build()).submit();

        return;
      }

      Matcher matcher = commandPattern.matcher(event.getMessage().getContentRaw());

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
          boolean hasArgumentWithSpaces = it.getParameterAnnotations().length > 0
            && it.getParameterAnnotations()[1].length > 0
            && it.getParameterAnnotations()[1][0].annotationType().equals(ContainsSpaces.class);

          if(
            matcher.group("commandGroup").equals(
              it.getDeclaringClass().getAnnotation(CommandsController.class).commandsGroup()
            )
              && matcher.group("commandName").equals(it.getName())
              && (args.size() == it.getParameterCount() || hasArgumentWithSpaces)
          ) {
            if(hasArgumentWithSpaces) {
              Object message = args.get(0);
              args.remove(0);

              @SuppressWarnings("SuspiciousToArrayCall")
              Object argument = String.join(" ", args.toArray(new String[0]));

              args.clear();
              args.add(message);
              args.add(argument);
            }

            Object result = it.invoke(
              this.controllerInstance,
              args.toArray(new Object[0])
            );

            if(result instanceof String) {
              event.getChannel().sendMessage((String) result).submit();
            }

            if(result instanceof MessageEmbed) {
              event.getChannel().sendMessage((MessageEmbed) result).submit();
            }
          }
        }
      }
    } catch(Throwable e) {
      e.printStackTrace();
    }
  }
}
