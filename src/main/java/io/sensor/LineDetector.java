package io.sensor;

import domain.State;
import io.element.ElementHandler;
import io.element.ElementMatcher;
import io.element.PatternMatcher;
import io.listener.StateListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineDetector implements Sensor, StateListener {
    private final StringBuffer buf;
    private final Map mapMatcherToHandler;
    private boolean playing;
    private Pattern lineMatcher;

    public LineDetector() {
        mapMatcherToHandler = new HashMap();
        buf = new StringBuffer();
        lineMatcher = Pattern.compile("(?ms)(.*?[!.]\\s)");
    }

    public void onCode(String code) {

    }

    public void onText(String text) {
//        System.err.println(text);
        if (playing) {
            buf.append(text);
            String buffer = buf.toString();
            Matcher matcher = lineMatcher.matcher(buffer);
            while(matcher.find()) {
                String line = matcher.group();
                buf.delete(0, line.length());
                onLine(line);
//                System.err.println("--> "+buf.toString());
            }
        }
    }


    private void onLine(String str) {
//        System.err.println("*** " + str + " ***");
        LinkedHashMap fired = new LinkedHashMap();
        Iterator iterator = mapMatcherToHandler.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ElementMatcher elementMatcher = (ElementMatcher) entry.getKey();
            ElementHandler elementHandler = (ElementHandler) entry.getValue();
            if (elementMatcher.isMatch(str)) {
                fired.put(elementMatcher, str);
            }
        }

        // prevent concurrent modification problems
        if (!fired.isEmpty()) {
            iterator = fired.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                ElementMatcher elementMatcher = (ElementMatcher) entry.getKey();
                str = (String) entry.getValue();
                ElementHandler elementHandler = (ElementHandler) mapMatcherToHandler.get(elementMatcher);

                //                System.out.print("MATCH TO ");
                String[] parts = elementMatcher.getParts();
//                for (int i =0 ; i< parts.length; i++) {
//                    String part = parts[i];
//                    System.out.print("(" + part + ") ");
//                }
//                System.out.println();
                elementHandler.processElement(str, parts);

            }
        }
    }

    public void addElementMatcherAndHandler(ElementMatcher elementMatcher,
                                            ElementHandler elementHandler) {
        mapMatcherToHandler.put(elementMatcher, elementHandler);
    }

    public void onState(String key, Object value) {
        if (key.equals(State.KEY_PLAYING)) {
            if (Boolean.TRUE.equals(value) && !playing) {
                onCode("<2001>");
                onCode("<0201>");
            }
            playing = Boolean.TRUE.equals(value);
        }
    }

    public void addPatternMatcherAndHandler(String pattern,
                                            ElementHandler handler) {
        addElementMatcherAndHandler(new PatternMatcher(pattern), handler);
    }
}
