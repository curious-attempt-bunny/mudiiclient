package gui3;

import backend2.CommandSender;
import domain.Configuration;
import io.element.ElementHandler;
import io.element.ElementMatcher;
import io.listener.CodeListener;
import io.listener.TextListener;
import io.sensor.LineDetector;

import java.util.*;

/**
 * Created by home on 7/4/15.
 */
public class CommandTransformer {
//    private Map triggerToMacroAndValue = new HashMap();
    private Map macroToValue = new HashMap();
    private Configuration configuration;
    private LineDetector lineDetector;

    private CommandSender commandSender;

    public CommandTransformer() {

    }

    public void init() {
        Properties settings = configuration.getSettings();
        Enumeration names = settings.propertyNames();
        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            if (name.startsWith("trigger")) {
                String[] parts = name.split("\\|");
                final String trigger = parts[1].replaceAll("([*{}?.+\\]])", "\\\\$1");
                String macroAndValue = configuration.getSetting(name);
                if (macroAndValue.indexOf("|") == -1) {
                    final String triggerCommand = macroAndValue;
                    lineDetector.addPatternMatcherAndHandler(trigger, new ElementHandler() {
                        public void processElement(String element, String[] parts) {
                            commandSender.send(triggerCommand+ "\r");
                        }
                    });
                } else {
                    parts = macroAndValue.split("\\|");
                    final String macro = parts[0];
                    final String value = parts[1];
                    //                System.out.println("Trigger: "+trigger);
                    //                triggerToMacroAndValue.put(parts[1], configuration.getSetting(name));
                    lineDetector.addPatternMatcherAndHandler(trigger, new ElementHandler() {
                        public void processElement(String element, String[] parts) {
                            //                        System.out.println("Trigger fired: \"" + trigger + "\".");
                            //                        System.out.println("Setting macro "+macro+" --> "+value);

                            macroToValue.put(macro, value);
                        }
                    });
                }
            }
        }
    }

    public String transform(String cmd) {
        String[] cmds = cmd.split("\\.", -1);
        for(int i=0; i<cmds.length; i++) {
            if (macroToValue.containsKey(cmds[i])) {
                cmds[i] = (String) macroToValue.get(cmds[i]);
                break;
            }
        }

        String transformed = String.join(".", cmds);

        return transformed;
    }

//    public void onText(String text) {
//        Iterator triggers = triggerToMacroAndValue.keySet().iterator();
//        while(triggers.hasNext()) {
//            String trigger = (String) triggers.next();
//
//            if (text.indexOf(trigger) != -1) {
//                String macroAndValue = (String) triggerToMacroAndValue.get(trigger);
//                String[] parts = macroAndValue.split("\\|");
//
//                System.out.println("Trigger fired: \"" + trigger + "\".");
//                System.out.println("Setting macro "+parts[0]+" --> "+parts[1]);
//
//                macroToValue.put(parts[0], parts[1]);
//            }
//        }
//    }
//
//    public void onCode(String code) {
//
//    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setLineDetector(LineDetector lineDetector) {
        this.lineDetector = lineDetector;
    }

    public void setCommandSender(CommandSender commandSender) {
        this.commandSender = commandSender;
    }

}
