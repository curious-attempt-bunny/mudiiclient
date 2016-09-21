package io.sensor;

import domain.State;
import io.listener.CodeListener;

/**
 * Created by home on 9/20/16.
 */
public class SyncSensor implements CodeListener {
    private long lastInvalidation = 0;
    private State state;

    public void onCode(String code) {
        if (code.equals("<040003>")) {
            if (System.currentTimeMillis() - lastInvalidation > 2000) {
                state.onState(State.KEY_SYNC, new Integer((int)(System.currentTimeMillis() % 2000)));
            }
        } else if (code.startsWith("<07") || code.startsWith("<08")) {
            lastInvalidation = System.currentTimeMillis();
        }
    }

    public void setState(State state) {
        this.state = state;
    }
}
