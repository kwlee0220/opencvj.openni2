package openni2.camera;

import org.opencv.core.Size;

import opencvj.camera.OpenCvJCamera;
import openni2.OpenNI2System;
import utils.Initializable;
import utils.config.ConfigNode;
import utils.jni.JniUtils;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2ColorCamera extends OpenNI2CameraBase implements OpenCvJCamera, Initializable {
	// properties (BEGIN)
	// properties (END)
	
	public static OpenNI2ColorCamera create(OpenNI2System system, ConfigNode config) throws Exception {
		OpenNI2ColorCamera camera = new OpenNI2ColorCamera();
		camera.setOpenNI2System(system);
		camera.setConfig(config);
		camera.initialize();
		
		return camera;
	}
	
	public OpenNI2ColorCamera() { }

	@Override
	protected ImageStream allocateStream(String uri, Size resol) {
		byte[] uriBytes = (uri != null) ? JniUtils.toKsc5601Bytes(uri) : null;
		int jptr = OpenNI2CameraJNI.allocateColor(uriBytes, (int)resol.width, (int)resol.height);
		setJniPointer(jptr);
		
		return new ColorImageStream(jptr, uri, resol);
	}
}
