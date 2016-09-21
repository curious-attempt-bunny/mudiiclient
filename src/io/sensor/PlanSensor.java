package io.sensor;

import domain.Configuration;
import io.listener.TextListener;

import java.util.*;

/**
 * Created by home on 9/21/16.
 */
public class PlanSensor implements TextListener {
    private HashMap objectNameToIndex = new HashMap();
    private String[] labels = new String[100];
    private List remainingLabels = new ArrayList();
    private Configuration configuration;
    private List listeners = new ArrayList();

    public void init() {
        Properties settings = configuration.getSettings();
        Enumeration names = settings.propertyNames();
        while(names.hasMoreElements()) {
            String name = (String)names.nextElement();
            String alternates = configuration.getSetting(name);

            String[] parts = name.split("|");
            if (parts[0].equals("plan")) {
                int index = Integer.parseInt(parts[1]);
                String label = parts[2];

                String[] objectNames = new String[] { label };
                if (!alternates.trim().isEmpty()) {
                    objectNames = alternates.split("|");
                }

                for(int i=0; i<objectNames.length; i++) {
                    objectNameToIndex.put(objectNames[i], new Integer(index));
                }
                labels[index] = label;
                remainingLabels.add(label);
            }

            for(int i=0; i<labels.length; i++) {
                if (labels[i] != null) {
                    remainingLabels.add(labels[i]);
                }
            }
        }
    }

    public void onText(String text) {
        text = text.trim();
        if (text.endsWith("taken.")) {
            String[] words = text.split(" ");
            for(int i=0; i<words.length; i++) {
                if (objectNameToIndex.containsKey(words[i])) {
                    int index = ((Integer)objectNameToIndex.get(words[i])).intValue();
                    remainingLabels.remove(labels[index]);
                    objectNameToIndex.remove(words[i]);
                    firePlanChanged();
                }
            }
        }
    }

    private void firePlanChanged() {
        Iterator iterator = listeners.iterator();
        while(iterator.hasNext()) {
            ((PlanListener)iterator.next()).onPlanChanged();
        }
    }

    public void addPlanListener(PlanListener listener) {
        listeners.add(listener);
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
