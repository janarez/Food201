package si.labs.augmented_reality_menu.helpers.ar;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.ar.core.ArCoreApk;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;

import java.util.EnumSet;

import si.labs.augmented_reality_menu.R;

public class ARCheckerHelper {
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
                    session = new Session(boundActivity, EnumSet.of(Session.Feature.SHARED_CAMERA));
                    Config config = session.getConfig();
                    config.setInstantPlacementMode(Config.InstantPlacementMode.LOCAL_Y_UP);
                    break;
                case INSTALL_REQUESTED:
                    mUserRequestedInstall = false;
                    break;
            }
        } catch (UnavailableUserDeclinedInstallationException e) {
            Toast.makeText(boundActivity, R.string.ar_user_declined_install, Toast.LENGTH_LONG)
                    .show();
            Log.d(ARCheckerHelper.class.getName(), boundActivity.getResources().getString(R.string.ar_user_declined_install), e);
        } catch (UnavailableArcoreNotInstalledException e) {
            Toast.makeText(boundActivity, R.string.ar_not_installed, Toast.LENGTH_LONG)
                    .show();
            Log.e(ARCheckerHelper.class.getName(), boundActivity.getResources().getString(R.string.ar_not_installed), e);
        } catch (UnavailableApkTooOldException e) {
            Toast.makeText(boundActivity, R.string.ar_apk_too_old, Toast.LENGTH_LONG)
                    .show();
            Log.e(ARCheckerHelper.class.getName(), boundActivity.getResources().getString(R.string.ar_apk_too_old), e);
        } catch (UnavailableSdkTooOldException e) {
            Toast.makeText(boundActivity, R.string.ar_sdk_too_old, Toast.LENGTH_LONG)
                    .show();
            Log.e(ARCheckerHelper.class.getName(), boundActivity.getResources().getString(R.string.ar_sdk_too_old), e);
        } catch (UnavailableDeviceNotCompatibleException e) {
            Toast.makeText(boundActivity, R.string.ar_device_incompatible, Toast.LENGTH_LONG)
                    .show();
            Log.e(ARCheckerHelper.class.getName(), boundActivity.getResources().getString(R.string.ar_device_incompatible), e);
        } catch (Exception e) {
            Toast.makeText(boundActivity, R.string.general_exception, Toast.LENGTH_LONG)
                    .show();
            Log.e(ARCheckerHelper.class.getName(), boundActivity.getResources().getString(R.string.general_exception), e);
        }
    }

    public void onActivityDestroy() {
        if (session != null) {
            session.close();
            session = null;
        }
    }
}
