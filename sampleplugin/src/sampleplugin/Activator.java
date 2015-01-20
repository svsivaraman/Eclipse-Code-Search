package sampleplugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import sampleplugin.views.ShowNotification;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "samplePlugin"; //$NON-NLS-1$

	//Display the user notification
	ShowNotification errorMessage = new ShowNotification();
	
	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) {
		
		try {
		super.start(context);
		plugin = this;
		} catch(Exception startErrMsg) {
			errorMessage.showMessage("The following error(s) occured in Start Method:\n " + startErrMsg.getMessage(),0);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	
		try {
		plugin = null;
		super.stop(context);
	} catch(Exception stopErrMsg) {
		errorMessage.showMessage("The following error(s) occured in Stop Method:\n " + stopErrMsg.getMessage(),0);
	}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
/*24 LOC*/