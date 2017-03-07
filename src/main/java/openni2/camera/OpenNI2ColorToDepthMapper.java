package openni2.camera;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import opencvj.Mats;
import opencvj.camera.ColorToDepthMapper;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2ColorToDepthMapper implements ColorToDepthMapper {
	private final OpenNI2ColorDepthComposite m_cdc;
	
	OpenNI2ColorToDepthMapper(OpenNI2ColorDepthComposite cdc) {
		m_cdc = cdc;
	}
	
	@Override
	public Point map(Point colorCoord, Mat depthImage) {
		return colorCoord;
	}

	@Override
	public Point[] map(Point[] colorCoords, Mat depthImage) {
		return colorCoords;
	}

	@Override
	public void mapMask(Mat colorMask, Mat depthMask, Mat depthImage) {
		if ( colorMask.size().equals(m_cdc.getColorImageSize()) ) {
			Mats.createIfNotValid(depthMask, m_cdc.getDepthImageSize(), CvType.CV_16SC1);
			depthMask.setTo(colorMask);
		}
		else {
			Imgproc.resize(colorMask, depthMask, m_cdc.getDepthImageSize());
		}
	}
}
