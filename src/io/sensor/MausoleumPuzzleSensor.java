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
public class MausoleumPuzzleSensor {
    private Configuration configuration;
    private Map mapDirToToken;
    private Map mapDirToTrigger;
    private CommandTransformer commandTransformer;
    private LineDetector lineDetector;
    private String lastCommand;

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
        lineDetector.addPatternMatcherAndHandler("Written on the %d tomb is: \"%s\"", new ElementHandler() {
            public void processElement(String element, String[] parts) {
                String token = (String) mapDirToToken.get(parts[0]);
//                System.out.println(token + " -> "+parts[1]);
                if (token != null) {
                    mapDirToTrigger.put(token, parts[1]);
                }
            }
        });
        lineDetector.addPatternMatcherAndHandler("\\*(.*?)(?:\\r|\\n)", new ElementHandler() {
            public void processElement(String element, String[] parts) {
                lastCommand = parts[0];
//                System.out.println("CMD: "+lastCommand);
            }
        });
        lineDetector.addPatternMatcherAndHandler("You hear a%W, as the entrance to the %d tomb swings aside.", new ElementHandler() {
            public void processElement(String element, String[] parts) {
                String token = (String) mapDirToToken.get(parts[1]);
                if (token != null && mapDirToTrigger.get(token) != null && lastCommand != null) {
//                    System.out.println("opened " + parts[1] + " with " + lastCommand);
                    configuration.setSetting("trigger|" + mapDirToTrigger.get(token), "op " + token + "|" + lastCommand);
                    commandTransformer.init();
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

}
