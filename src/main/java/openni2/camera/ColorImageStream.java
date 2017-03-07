package openni2.camera;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import opencvj.Mats;
import opencvj.camera.OpenCvJCamera;
import openni2.SensorType;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
class ColorImageStream extends ImageStream implements OpenCvJCamera {
	ColorImageStream(int jptr, String uri, Size resol) {
		super(jptr, uri, SensorType.COLOR, resol);
	}

	@Override
	protected void allocateMat(Mat image) {
		Mats.createIfNotValid(image, getSize(), CvType.CV_8UC3);
	}
}
