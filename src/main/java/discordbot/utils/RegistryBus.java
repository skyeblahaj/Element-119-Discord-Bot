package discordbot.utils;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(SOURCE) //TODO change to RUNTIME when possible
@Target(METHOD)
public @interface RegistryBus {}