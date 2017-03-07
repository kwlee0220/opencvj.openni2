package openni2.camera;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import opencvj.Mats;
import opencvj.camera.OpenCvJDepthCamera;
import openni2.SensorType;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
class DepthImageStream extends ImageStream implements OpenCvJDepthCamera {
	DepthImageStream(int jptr, String uri, Size resol) {
		super(jptr, uri, SensorType.DEPTH, resol);
	}

	@Override
	protected void allocateMat(Mat image) {
		Mats.createIfNotValid(image, getSize(), CvType.CV_16SC1);
	}
}
