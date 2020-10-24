package si.labs.augmented_reality_menu.helpers;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import si.labs.augmented_reality_menu.R;

public final class ARCheckerHelper {
    private final Activity boundActivity;
    private Session session;
    private boolean mUserRequestedInstall = true;

    public ARCheckerHelper(Activity activity) {
        this.boundActivity = activity;
    }

    public Session getSession() {
        return session;
    }

    public void requestInstall() {
        if (session != null) {
            return;
        }

        try {
            switch (ArCoreApk.getInstance().requestInstall(boundActivity, mUserRequestedInstall)) {
                case INSTALLED:
                    session = new Session(boundActivity);
                    break;
                case INSTALL_REQUESTED:
                    mUserRequestedInstall = false;
                    break;
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            Toast.makeText(boundActivity, R.string.ar_user_declined_install, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), e.getMessage());
        } catch (UnavailableArcoreNotInstalledException e) {
            Toast.makeText(boundActivity, R.string.ar_not_installed, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), e.getMessage());
        } catch (UnavailableApkTooOldException e) {
            Toast.makeText(boundActivity, R.string.ar_apk_too_old, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), e.getMessage());
        } catch (UnavailableSdkTooOldException e) {
            Toast.makeText(boundActivity, R.string.ar_sdk_too_old, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), e.getMessage());
        } catch (UnavailableDeviceNotCompatibleException e) {
            Toast.makeText(boundActivity, R.string.ar_device_incompatible, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), e.getMessage());
        } catch (Exception e) {
            Toast.makeText(boundActivity, R.string.general_exception, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), e.getMessage());
        }
    }
}
