package io.protocol;

import io.listener.BytesListener;
import io.listener.CodeListener;
import io.listener.TextListener;

public interface MudClientProtocolHandler extends BytesListener {
	void addCodeListener(CodeListener codeListener);
	void addTextListener(TextListener textListener);
}
