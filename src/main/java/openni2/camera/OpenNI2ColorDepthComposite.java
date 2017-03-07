package openni2.camera;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;

import opencvj.Mats;
import opencvj.OpenCvJUtils;
import opencvj.camera.ColorDepthComposite;
import openni2.OpenNI2System;
import utils.Initializable;
import utils.UninitializedException;
import utils.config.ConfigNode;
import utils.jni.AbstractJniObjectProxy;
import utils.jni.JniUtils;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class OpenNI2ColorDepthComposite extends AbstractJniObjectProxy
										implements ColorDepthComposite, Initializable {
    public static class Params {
    	public String deviceUri =null;
    	public Size colorImageSize;
    	public Size depthImageSize;
    	public boolean frameSync = false;
    	public boolean imageRegistration = false;
    	
    	public Params(ConfigNode config) {
    		deviceUri = config.get("device_uri").asString(null);
    		colorImageSize = OpenCvJUtils.asSize(config.traverse("color.image_size"));
    		depthImageSize = OpenCvJUtils.asSize(config.traverse("depth.image_size"));
    		frameSync = config.get("frame_sync").asBoolean(false);
    		imageRegistration = config.get("image_registration").asBoolean(false);
    	}
    }
    
	// properties (BEGIN)
	private volatile OpenNI2System m_system;
	private volatile ConfigNode m_config;
	// properties (END)

	private Params m_params;
	private ColorImageStream m_color;
	private DepthImageStream m_depth;
	
	public static OpenNI2ColorDepthComposite create(OpenNI2System system, ConfigNode config)
		throws Exception {
		OpenNI2ColorDepthComposite cdc = new OpenNI2ColorDepthComposite();
		cdc.setOpenNI2System(system);
		cdc.setConfig(config);
		cdc.initialize();
		
		return cdc;
	}
	
	public OpenNI2ColorDepthComposite() { }
	
	public final void setOpenNI2System(OpenNI2System system) {
		m_system = system;
	}
	
	public final void setConfig(ConfigNode config) {
		m_config = config;
	}
	
//	public final void setConfig(String configStr) {
//		m_config = new Config(configStr);
//	}

	@Override
	public void initialize() throws Exception {
		if ( m_system == null ) {
			throw new UninitializedException("Property 'openni2System' was not specified: class="
											+ getClass().getName());
		}
		if ( m_config == null ) {
			throw new UninitializedException("Property 'config' was not specified: class="
											+ getClass().getName());
		}
		
		m_params = new Params(m_config);
		
		byte[] uriBytes = (m_params.deviceUri != null) ? JniUtils.toKsc5601Bytes(m_params.deviceUri)
														: null;
		int[] jptrs = OpenNI2CDCJNI.allocate(uriBytes,
											(int)m_params.colorImageSize.width,
											(int)m_params.colorImageSize.height,
											(int)m_params.depthImageSize.width,
											(int)m_params.depthImageSize.height);
		setJniPointer(jptrs[0]);
		
		m_color = new ColorImageStream(jptrs[1], m_params.deviceUri, m_params.colorImageSize);
		m_depth = new DepthImageStream(jptrs[2], m_params.deviceUri, m_params.depthImageSize);
		
		OpenNI2CDCJNI.setFrameSync(jptrs[0], m_params.frameSync);
		OpenNI2CDCJNI.setImageRegistration(jptrs[0], m_params.imageRegistration);
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

	@Override
	public synchronized void open() {
		m_color.open();
		try {
			m_depth.open();
		}
		catch ( Throwable e ) {
			m_color.close();
		}
	}

	@Override
	public synchronized void close() {
		m_color.close();
		m_depth.close();
		
		super.close();
	}

	@Override
	public Size getColorImageSize() {
		return m_color.getSize();
	}

	@Override
	public Size getDepthImageSize() {
		return m_depth.getSize();
	}

	@Override
	public ColorImageStream getColorCamera() {
		return m_color;
	}

	@Override
	public DepthImageStream getDepthCamera() {
		return m_depth;
	}

	@Override
	public OpenNI2DepthToColorMapper getDepthToColorMapper() {
		return new OpenNI2DepthToColorMapper(this);
	}

	@Override
	public OpenNI2ColorToDepthMapper getColorToDepthMapper() {
		return new OpenNI2ColorToDepthMapper(this);
	}

	@Override
	public void captureSynched(Mat colorImage, Mat depthImage) {
		Mats.createIfNotValid(colorImage, m_params.colorImageSize, CvType.CV_8UC3, null);
		Mats.createIfNotValid(depthImage, m_params.depthImageSize, CvType.CV_16SC1, null);

//		m_depth.capture(depthImage);
//		m_color.capture(colorImage);
		
		OpenNI2CDCJNI.captureSync(getJniPointer(), colorImage.nativeObj, depthImage.nativeObj);
	}

	@Override
	protected void deleteJniObjectInGuard(int jniPtr) {
		OpenNI2CDCJNI.release(jniPtr);
	}
}
