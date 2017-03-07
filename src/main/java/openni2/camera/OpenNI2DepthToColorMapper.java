package openni2.camera;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import opencvj.Mats;
import opencvj.camera.DepthToColorMapper;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2DepthToColorMapper implements DepthToColorMapper {
	private final OpenNI2ColorDepthComposite m_cdc;
	
	OpenNI2DepthToColorMapper(OpenNI2ColorDepthComposite cdc) {
		m_cdc = cdc;
	}
	
	@Override
	public Point map(Point depthCoord, Mat depthImage) {
		return depthCoord;
	}

	@Override
	public Point[] map(Point[] depthCoords, Mat depthImage) {
		return depthCoords;
	}

	@Override
	public void mapMask(Mat depthMask, Mat colorMask, Mat depthImage) {
		if ( depthMask.size().equals(m_cdc.getColorImageSize()) ) {
			Mats.createIfNotValid(colorMask, m_cdc.getColorImageSize(), CvType.CV_8UC3);
			colorMask.setTo(depthMask);
		}
		else {
			Imgproc.resize(depthMask, colorMask, m_cdc.getColorImageSize());
		}
	}
}
