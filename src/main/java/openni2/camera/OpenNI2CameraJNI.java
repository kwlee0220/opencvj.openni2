package openni2.camera;



/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2CameraJNI {
	static native int allocateColor(byte[] deviceUri, int width, int height);
	static native int allocateDepth(byte[] deviceUri, int width, int height);
	static native void release(int jptr);
	
	static native void open(int jptr);
	static native void close(int jptr);
	
	static native void capture(int jptr, long matPtr);
}
