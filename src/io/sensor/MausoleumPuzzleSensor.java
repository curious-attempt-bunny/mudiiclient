package io.sensor;

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
public class MausoleumPuzzleSensor implements ElementHandler {
    private final Pattern openPattern;
    private final Pattern writtenPattern;
    private String previousText = null;
    private Configuration configuration;
    private Map mapDirToToken;
    private Map mapDirToTrigger;
    private CommandTransformer commandTransformer;
    private LineDetector lineDetector;

    public MausoleumPuzzleSensor() {
        openPattern = Pattern.compile("You hear a.*, as the entrance to the ([a-z]+) tomb swings aside.");
        writtenPattern = Pattern.compile(".*Written on the ([a-z]+) tomb is: \"");
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
        lineDetector.addPatternMatcherAndHandler("Written on the %d tomb is: \"%s\"", this);
    }

    public void processElement(String element, String[] parts) {
        System.err.println(element+": "+parts[0]+", "+parts[1]);
    }

    public void onText(String text) {
        String trimmed = text.trim();
        Matcher matcher = openPattern.matcher(trimmed);
        if (matcher.matches()) {
            String token = (String) mapDirToToken.get(matcher.group(1));
            System.err.println(matcher.group(1) + " & " + previousText + " & "+token);
            if (token != null && mapDirToTrigger.containsKey(token)) {
                configuration.setSetting("trigger|"+mapDirToTrigger.get(token), "op "+token+"|"+previousText);
                commandTransformer.init();
            }
            return;
        }
        if (text.endsWith("?")) {
            matcher = writtenPattern.matcher(previousText);
            if (matcher.matches()) {
                String token = (String) mapDirToToken.get(matcher.group(1));
                System.err.println(matcher.group(1) + " & " + trimmed + " & " + token);
                if (token != null) {
                    mapDirToTrigger.put(token,trimmed);
                }
                return;
            }
        }
        previousText = trimmed;
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

}
