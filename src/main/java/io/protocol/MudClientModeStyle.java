package io.protocol;

import io.listener.CodeListener;
import io.listener.StyleListener;

public interface MudClientModeStyle extends CodeListener {

	void addStyleListener(StyleListener styleListener);

}
