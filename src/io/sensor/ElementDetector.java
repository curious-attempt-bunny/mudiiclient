package io.sensor;

import domain.State;
import io.element.ElementHandler;
import io.element.ElementMatcher;
import io.element.PatternMatcher;
import io.listener.StateListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ElementDetector implements Sensor, StateListener {
    private int nesting;
    private boolean isRequireNewline;
    private final StringBuffer buf;
    private final StringBuffer text;
    private final Map mapMatcherToHandler;
    private boolean playing;
    private boolean leading01 = false;

    public ElementDetector() {
        mapMatcherToHandler = new HashMap();
        buf = new StringBuffer();
        text = new StringBuffer();
        nesting = 0;
        isRequireNewline = false;
    }

    public void onCode(String code) {
//        System.err.println(code + " " + playing);
        if (playing) {
            if (text.length() > 0) {
                if (buf.length() == 0) {
                    isRequireNewline = true;
                }

                String text = this.text.toString();
                while (true) {
                    if (text.contains("\r")) {
//						Assert.assertEquals('\n', text.charAt(text
//								.indexOf('\r') + 1));
                        addElement(text.substring(0, text.indexOf('\r') + 2));
                        text = text.substring(text.indexOf('\r') + 2);
                    } else {
                        addElement(text);
                        break;
                    }
                }
                this.text.delete(0, this.text.length());
            }
            if (code.equals("<>")) {
                nesting--;
            } else if (code.equals("<01>")) {
                leading01 = true; // eat erroneously sent <01> codes (maybe as a result of FES statements?)
                if (nesting == 0 && buf.length() > 0) {
                    addElement(buf.toString());
                    buf.delete(0, buf.length());
                }
            } else {
                if (leading01 && code.startsWith("<01")) {
                    nesting++;
                }
                nesting++;
                leading01 = false;
            }
//            addElement(code);
            addElement("");
        }
    }

    public void onText(String text) {
//        System.err.println(text);
        if (playing) {
            this.text.append(text);
        }
        leading01 = false;
    }

    private void addElement(String element) {
//        if (element.length() > 0) {
            buf.append(element);
            // System.out.println("EL[" + nesting + "]: " + element);
            boolean isNewline = element.contains("\r")
                    && (element.indexOf('\r') == 0 || element.charAt(element
                    .indexOf('\r') - 1) != ',');
//            System.err.println("Build up (nesting "+nesting+"): "+buf.toString());
            if ((nesting == 0 && (!isRequireNewline || isNewline))
                    || nesting < 0) {
                String str = buf.toString();
                // System.out.println("TOT " + str);
                if (str.trim().length() > 0 && !str.equals(".\r")) {
                    onElement(str);
                }
                isRequireNewline = false;
                nesting = 0;
                buf.delete(0, buf.length());
            }
//        }
    }

    private void onElement(String str) {
        System.err.println("*** " + str + " ***");
        Iterator iterator = mapMatcherToHandler.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            ElementMatcher elementMatcher = (ElementMatcher) entry.getKey();
            ElementHandler elementHandler = (ElementHandler) entry.getValue();
            if (elementMatcher.isMatch(str)) {
                System.out.print("MATCH TO ");
                String[] parts = elementMatcher.getParts();
                for (int i =0 ; i< parts.length; i++) {
                    String part = parts[i];
                    System.out.print("(" + part + ") ");
                }
                System.out.println();
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
