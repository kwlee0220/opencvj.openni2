package openni2.camera;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import net.jcip.annotations.GuardedBy;
import opencvj.OpenCvJUtils;
import opencvj.camera.OpenCvJCamera;
import openni2.SensorType;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
abstract class ImageStream implements OpenCvJCamera {
	private int m_jptr;
	private final String m_uri;
	private final SensorType m_type;
	@GuardedBy("this") private boolean m_opened = false;
	private Size m_resol;
	
	protected abstract void allocateMat(Mat image);
	
	protected ImageStream(int jniPtr, String uri, SensorType type, Size resol) {
		m_jptr = jniPtr;
		m_uri = uri;
		m_type = type;
		m_resol = resol;
	}

	@Override
	public Size getSize() {
		return m_resol;
	}

	@Override
	public synchronized void open() {
		if ( m_opened ) {
			throw new IllegalStateException("already openned: " + this);
		}
		
		OpenNI2CameraJNI.open(m_jptr);
		m_opened = true;
	}

	@Override
	public synchronized void close() {
		if ( m_opened ) {
			m_opened = false;
			OpenNI2CameraJNI.close(m_jptr);
		}
	}

	@Override
	public synchronized void capture(Mat image) {
		if ( !m_opened ) {
			throw new IllegalStateException("not openned: " + this);
		}
		
		allocateMat(image);
		OpenNI2CameraJNI.capture(m_jptr, image.getNativeObjAddr());
	}
	
	@Override
	public String toString() {
		return String.format("%s[uri=%s type=%s resol=%s]", getClass().getSimpleName(),
								m_uri, ""+m_type, OpenCvJUtils.toString(getSize()));
	}
}
