package io.sensor;

import backend2.CommandSender;
import domain.Configuration;
import gui3.CommandTransformer;
import io.element.ElementHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by merlyn on 8/18/15.
 */
public class MausoleumPuzzleSensor implements CommandSender {
    private Configuration configuration;
    private Map mapDirToToken;
    private Map mapDirToTrigger;
    private CommandTransformer commandTransformer;
    private LineDetector lineDetector;
    private String lastCommand;
    private CommandSender commandSender;

    public MausoleumPuzzleSensor() {
        mapDirToToken = new HashMap();
        mapDirToToken.put("north", "n ");
        mapDirToToken.put("east", "e ");
        mapDirToToken.put("southeast", "se");
        mapDirToToken.put("south", "s ");
        mapDirToToken.put("southwest", "sw");
        mapDirToToken.put("west", "w ");
        mapDirToTrigger = new HashMap();
    }

    public void init() {
        lineDetector.addPatternMatcherAndHandler("Written on the %d tomb is: \"%s", new ElementHandler() {
            public void processElement(String element, String[] parts) {
                String token = (String) mapDirToToken.get(parts[0]);
//                System.out.println(token + " -> "+parts[1]);
                if (token != null) {
                    mapDirToTrigger.put(token, parts[1]);
                }
            }
        });
        lineDetector.addPatternMatcherAndHandler("You hear a%W, as the entrance to the %d tomb swings aside.", new ElementHandler() {
            public void processElement(String element, String[] parts) {
                String token = (String) mapDirToToken.get(parts[1]);
                if (token != null && mapDirToTrigger.get(token) != null && lastCommand != null && lastCommand.length()>=3 && (lastCommand.contains("\"") || lastCommand.contains("'"))) {
//                    System.out.println("opened " + parts[1] + " with " + lastCommand);
                    String key = "trigger|" + mapDirToTrigger.get(token);
                    if (configuration.getSetting(key) == null) {
                        configuration.setSetting(key, "op " + token + "|" + lastCommand);
                        commandTransformer.init();
                    }
                }
            }
        });
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setCommandTransformer(CommandTransformer commandTransformer) {
        this.commandTransformer = commandTransformer;
    }

    public void setLineDetector(LineDetector lineDetector) {
        this.lineDetector = lineDetector;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

    public void send(String text) {
        lastCommand = text.trim();
//        System.out.println("CMD: "+lastCommand);
        commandSender.send(text);
    }

    public void send(byte[] bytes) {
        commandSender.send(bytes);
    }
}
