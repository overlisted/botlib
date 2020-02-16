package net.overlisted.botlib;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.overlisted.botlib.command.CommandTrigger;
import net.overlisted.botlib.command.CommandsController;
import net.overlisted.botlib.command.CommandsEventListener;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BotLib {
  public Map<String, JDA> bots = new HashMap<>();

  /**
   * Constructs all BotLib features and a new JDA.
   *
   * @param botPackage a package to search for commands in (in java 13 search of whole classpath doesn't work).
   *
   * @see BotLib#bots
   */
  public BotLib(String botPackage) {
    new Reflections(botPackage).getTypesAnnotatedWith(CommandsController.class).forEach(it -> {
      try {
        List<Method> commandTypes = new ArrayList<>();

        String token = it.getAnnotation(CommandsController.class).token();

        for(Method item: it.getMethods()) {
          if(item.isAnnotationPresent(CommandTrigger.class)) commandTypes.add(item);
        }

        if(!this.bots.containsKey(token)) {
          this.bots.put(
            token,
            new JDABuilder()
              .setToken(token)
              .addEventListeners(new CommandsEventListener(commandTypes.toArray(new Method[0])))
              .build()
          );
        } else {
          this.bots.get(token).addEventListener(new CommandsEventListener(commandTypes.toArray(new Method[0])));
        }
      } catch(Throwable e) {
        e.printStackTrace();
      }
    });
  }
}
