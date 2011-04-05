package fm.jopp.android.background.util;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class Money {

    public static boolean isDonate(final Context context) {
        try {
            context.createPackageContext("org.mailboxer.saymyname.donate", 0);

            return true;
        } catch (final NameNotFoundException e) {
            return false;
        }
    }
}
