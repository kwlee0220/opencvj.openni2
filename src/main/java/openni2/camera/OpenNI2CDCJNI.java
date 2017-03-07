package openni2.camera;



/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2CDCJNI {
	static native int[] allocate(byte[] deviceUri, int colorWidth, int colorHeight,
								int depthWidth, int depthHeight);
	static native void release(int jptr);
	
	static native void open(int jptr);
	static native void close(int jptr);
	static native void captureSync(int jptr, long colorMatPtr, long depthMatPtr);
	
	static native void setFrameSync(int jptr, boolean sync);
	static native void setImageRegistration(int jptr, boolean reg);
}
