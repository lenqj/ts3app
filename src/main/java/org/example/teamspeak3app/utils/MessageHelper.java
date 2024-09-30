package org.example.teamspeak3app.utils;


import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHelper {

    public static AbstractMap.SimpleEntry<Integer, String> handleMessage(String message) {
        if (!message.startsWith("!")) {
            return null;
        }

        String commandMessage = message.substring(1).trim();

        Pattern pattern = Pattern.compile("([^\\s]+)(?:\\s+(.+))?");
        Matcher matcher = pattern.matcher(commandMessage);

        if (matcher.find()) {
            String command = matcher.group(1);
            String arguments = matcher.group(2);

            if (isValidCommand(command)) {
                return new AbstractMap.SimpleEntry<>(availableCommands().get(command), arguments);
            }
        } else {
            return null;
        }
        return null;
    }

    public static Map<String, Integer> availableCommands() {
        Map<String, Integer> commands = new HashMap<>();
        commands.put("create-channel", 1);
        commands.put("register", 2);
        commands.put("info", 3);
        return commands;
    }

    private static boolean isValidCommand(String command) {
        return availableCommands().containsKey(command);
    }
}
