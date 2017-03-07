package openni2.camera;

import org.opencv.core.Size;

import opencvj.camera.OpenCvJDepthCamera;
import openni2.OpenNI2System;
import utils.Initializable;
import utils.config.ConfigNode;
import utils.jni.JniUtils;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2DepthCamera extends OpenNI2CameraBase
								implements OpenCvJDepthCamera, Initializable {
	// properties (BEGIN)
	// properties (END)
	
	public static OpenNI2DepthCamera create(OpenNI2System system, ConfigNode config)
		throws Exception {
		OpenNI2DepthCamera camera = new OpenNI2DepthCamera();
		camera.setOpenNI2System(system);
		camera.setConfig(config);
		camera.initialize();
		
		return camera;
	}
	
	public OpenNI2DepthCamera() { }

	@Override
	protected ImageStream allocateStream(String uri, Size resol) {
		byte[] uriBytes = (uri != null) ? JniUtils.toKsc5601Bytes(uri) : null;
		int jptr = OpenNI2CameraJNI.allocateDepth(uriBytes, (int)resol.width, (int)resol.height);
		setJniPointer(jptr);
		
		return new DepthImageStream(jptr, uri, resol);
	}
}
