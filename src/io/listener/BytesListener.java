package io.listener;

public interface BytesListener {
	void onBytes(byte[] bytes, int offset, int length);
}
