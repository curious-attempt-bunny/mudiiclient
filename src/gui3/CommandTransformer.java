package gui3;

import domain.Configuration;
import io.listener.CodeListener;
import io.listener.TextListener;

import java.util.*;

/**
 * Created by home on 7/4/15.
 */
public class CommandTransformer implements TextListener, CodeListener {
    private Map triggerToMacroAndValue = new HashMap();
    private Map macroToValue = new HashMap();
    private Configuration configuration;

    public CommandTransformer() {
        Properties settings = configuration.getSettings();
        Enumeration names = settings.propertyNames();
        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            if (name.startsWith("trigger")) {
                String[] parts = name.split("|");
                triggerToMacroAndValue.put(parts[1], configuration.getSetting(name));
            }
        }
    }

    public String transform(String cmd) {
        String[] cmds = cmd.split("\\.", -1);
        for(int i=0; i<cmds.length; i++) {
            if (macroToValue.containsKey(cmds[i])) {
                cmds[i] = (String) macroToValue.get(i);
                break;
            }
        }

        String transformed = String.join(".", cmds);

        return transformed;
    }

    public void onText(String text) {
        Iterator triggers = triggerToMacroAndValue.keySet().iterator();
        while(triggers.hasNext()) {
            String trigger = (String) triggers.next();

            if (text.indexOf(trigger) != -1) {
                String macroAndValue = (String) triggerToMacroAndValue.get(trigger);
                String[] parts = macroAndValue.split("|");

                System.out.println("Trigger fired: \"" + trigger + "\".");
                System.out.println("Setting macro "+parts[0]+" --> "+parts[1]);

                macroToValue.put(parts[0], parts[1]);
            }
        }
    }

    public void onCode(String code) {

    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
