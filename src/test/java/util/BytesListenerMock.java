package util;

import io.listener.BytesListener;

import java.io.ByteArrayOutputStream;

public class BytesListenerMock implements BytesListener {
	ByteArrayOutputStream byteArrayOutputStream;
	
	public BytesListenerMock() {
		byteArrayOutputStream = new ByteArrayOutputStream();
	}
	
	public void onBytes(byte[] bytes, int offset, int length) {
		byteArrayOutputStream.write(bytes, offset, length);
	}

	public byte[] getBytes() {
		return byteArrayOutputStream.toByteArray();
	}

}
