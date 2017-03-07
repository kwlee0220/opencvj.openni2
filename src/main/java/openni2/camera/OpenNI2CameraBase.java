package openni2.camera;

import org.opencv.core.Mat;
import org.opencv.core.Size;

import opencvj.OpenCvJUtils;
import opencvj.camera.OpenCvJCamera;
import openni2.OpenNI2System;
import utils.Initializable;
import utils.UninitializedException;
import utils.config.ConfigNode;
import utils.jni.AbstractJniObjectProxy;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public abstract class OpenNI2CameraBase extends AbstractJniObjectProxy
										implements OpenCvJCamera, Initializable {
    public static class Params {
    	public String deviceUri =null;
    	public Size imageSize;
    	
    	public Params(String uri, Size size) {
    		deviceUri = uri;
    		imageSize = size;
    	}
    	
    	public Params(ConfigNode config) {
    		deviceUri = config.get("device_uri").asString(null);
    		imageSize = OpenCvJUtils.asSize(config.get("image_size"), null);
    	}
    }
    
	// properties (BEGIN)
	private volatile OpenNI2System m_system;
	protected volatile ConfigNode m_config;
	// properties (END)

	protected Params m_params;
	private ImageStream m_stream;
	
	protected abstract ImageStream allocateStream(String uri, Size resol);
	
	public OpenNI2CameraBase() { }
	
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
		m_stream = allocateStream(m_params.deviceUri, m_params.imageSize);
	}

	@Override
	public void destroy() throws Exception {
		close();
	}

	@Override
	public void open() {
		m_stream.open();
	}

	@Override
	public void close() {
		m_stream.close();
	}

	@Override
	public Size getSize() {
		return m_stream.getSize();
	}

	@Override
	public void capture(Mat image) {
		m_stream.capture(image);
	}

	@Override
	protected void deleteJniObjectInGuard(int jniPtr) {
		OpenNI2CameraJNI.release(jniPtr);
	}
}
