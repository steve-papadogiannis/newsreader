package gr.papadogiannis.stefanos.newsreader;

import android.media.AudioManager;
import android.util.Log;
import java.lang.reflect.Method;

/**
 * Contains methods to handle registering/unregistering remote control clients.  These methods only
 * run on ICS devices.  On previous devices, all methods are no-ops.
 */
@SuppressWarnings({"unchecked", "rawtypes"})
class RemoteControlHelper {

    private static final String TAG = "RemoteControlHelper";
    private static boolean sHasRemoteControlAPIs = false;
    private static Method sRegisterRemoteControlClientMethod;
    private static Method sUnregisterRemoteControlClientMethod;

    static {
        try {
            ClassLoader classLoader = RemoteControlHelper.class.getClassLoader();
            Class sRemoteControlClientClass =
                    RemoteControlClientCompat.getActualRemoteControlClientClass(classLoader);
            sRegisterRemoteControlClientMethod = AudioManager.class.getMethod(
                    "registerRemoteControlClient", sRemoteControlClientClass);
            sUnregisterRemoteControlClientMethod = AudioManager.class.getMethod(
                    "unregisterRemoteControlClient", sRemoteControlClientClass);
            sHasRemoteControlAPIs = true;
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalArgumentException | SecurityException e) {
            // Silently fail when running on an OS before ICS.
        }
    }

    static void registerRemoteControlClient(AudioManager audioManager,
                                            RemoteControlClientCompat remoteControlClient) {
        if (!sHasRemoteControlAPIs) {
            return;
        }
        try {
            sRegisterRemoteControlClientMethod.invoke(audioManager,
                    remoteControlClient.getActualRemoteControlClientObject());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }


    public static void unregisterRemoteControlClient(AudioManager audioManager,
            RemoteControlClientCompat remoteControlClient) {
        if (!sHasRemoteControlAPIs) {
            return;
        }
        try {
            sUnregisterRemoteControlClientMethod.invoke(audioManager,
                    remoteControlClient.getActualRemoteControlClientObject());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

}

