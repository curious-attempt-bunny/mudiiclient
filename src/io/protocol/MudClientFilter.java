package io.protocol;

import io.listener.CodeListener;
import io.listener.TextListener;

public interface MudClientFilter extends CodeListener, TextListener {

	void addCodeListener(CodeListener codeListener);

	void addTextListener(TextListener textListener);

}
