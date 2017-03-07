package openni2;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import opencvj.OpenCvJLoader;
import opencvj.OpenCvJSystem;
import opencvj.camera.ColorDepthComposite;
import opencvj.camera.ColorDepthCompositeLoader;
import opencvj.camera.OpenCvJCamera;
import opencvj.camera.OpenCvJCameraLoader;
import openni2.camera.OpenNI2ColorCamera;
import openni2.camera.OpenNI2ColorDepthComposite;
import openni2.camera.OpenNI2DepthCamera;
import utils.Initializable;
import utils.UninitializedException;
import utils.config.ConfigNode;
import utils.jni.JniRuntimeException;



/**
 * 
 * @author Kang-Woo Lee
 */
public final class OpenNI2System implements Initializable {
	private static final Logger s_logger = LoggerFactory.getLogger("OPENNI2");
	
	// properties (BEGIN)
	private volatile OpenCvJLoader m_loader;
	private volatile File m_dllDir;
	// properties (END)
	
	private static OpenNI2System OPENNI2_SYSTEM;
	
	public static void loadSystem(OpenCvJLoader loader, File dllDir) {
		OpenNI2System system = new OpenNI2System();
		system.setOpenCvJLoader(loader);
		system.setDllDir(dllDir);
		system.initialize();
		
		OPENNI2_SYSTEM = system;
	}
	
	public OpenNI2System() { }

	public final void setOpenCvJLoader(OpenCvJLoader loader) {
		m_loader = loader;
	}

	public void setDllDir(File dllDir) {
		m_dllDir = dllDir;
	}

	@Override
	public void initialize() {
		if ( m_loader == null ) {
			throw new UninitializedException("Property 'openCvJLoader' was not specified: class="
											+ getClass().getName());
		}
		if ( m_dllDir == null ) {
			throw new UninitializedException("Property 'dllDir' was not specified: class="
											+ getClass().getName());
		}
		
		loadLibraries(m_dllDir);
		
		OpenNI2JNI.initialize();
		
		OpenCvJSystem.registerOpenCvJCameraLoader("openni2_color", m_colorLoader);
		OpenCvJSystem.registerOpenCvJCameraLoader("openni2_depth", m_depthLoader);
		OpenCvJSystem.registerCDCLoader("openni2", m_cdcLoader);
	}

	@Override
	public void destroy() {
		OpenCvJSystem.unregisterOpenCvJCameraLoader("openni2_color");
		OpenCvJSystem.unregisterOpenCvJCameraLoader("openni2_depth");
		OpenCvJSystem.unregisterCDCLoader("openni2");
		
		OpenNI2JNI.destroy();
	}

//	public Device openDevice(String deviceUri) throws Exception {
//		List<DeviceInfo> devicesInfo = OpenNI.enumerateDevices();
//		if ( deviceUri == null ) {
//			deviceUri = devicesInfo.get(0).getUri();
//		}
//		
//		return Device.open(deviceUri);
//	}

	private final OpenCvJCameraLoader m_colorLoader = new OpenCvJCameraLoader() {
		@Override
		public OpenCvJCamera load(ConfigNode config) throws Exception {
			return OpenNI2ColorCamera.create(OpenNI2System.this, config);
		}
	};

	private final OpenCvJCameraLoader m_depthLoader = new OpenCvJCameraLoader() {
		@Override
		public OpenCvJCamera load(ConfigNode config) throws Exception {
			return OpenNI2DepthCamera.create(OpenNI2System.this, config);
		}
	};
	
	private final ColorDepthCompositeLoader m_cdcLoader = new ColorDepthCompositeLoader() {
		@Override
		public ColorDepthComposite load(ConfigNode config) throws Exception {
			return OpenNI2ColorDepthComposite.create(OpenNI2System.this, config);
		}
	};

    private static boolean s_loaded = false;
    private static final String[] LIBS = {
		"OpenNI2",
		"opencv_core2411",
		"openni2_jni",
	};
	private void loadLibraries(File dllDir) throws JniRuntimeException {
    	if ( !s_loaded ) {
        	try {
				loadLibraries(LIBS, dllDir);
		    	s_loaded = true;
			}
			catch ( IOException e ) {
				throw new JniRuntimeException("fails to load relevant DLL file: "
														+ e.getMessage());
			}
    	}
	}
    
    private void loadLibraries(String[] libNames, File dllDir)
    	throws IOException {
    	if ( dllDir != null ) {
            for ( int i =0; i < libNames.length; ++i ) {
    			File dllFile = new File(dllDir, libNames[i] + ".dll");
    			
    			if ( dllFile.isFile() ) {
    				String path = dllFile.getAbsolutePath();
    				System.load(path);
	        		
	        		if ( s_logger.isDebugEnabled() ) {
	        			s_logger.debug("loaded file=" + path);
	        		}
    			}
    			else {
    				throw new IOException("invalid file path=" + dllFile.getAbsolutePath());
    			}
            }
    	}
    	else {
    		if ( s_logger.isDebugEnabled() ) {
    			s_logger.warn("fails to find JNI library dir");
    		}
    		
	        for ( int i =0; i < libNames.length; ++i ) {
	    		System.loadLibrary(libNames[i]);
        		
        		if ( s_logger.isDebugEnabled() ) {
        			s_logger.debug("loaded from PATH: " + libNames[i]);
        		}
	        }
    	}
    }
}
