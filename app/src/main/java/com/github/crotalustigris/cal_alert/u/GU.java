package com.github.crotalustigris.cal_alert.u;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.github.crotalustigris.cal_alert.R;

/**
 * GUI Utilities
 */
public class GU {

    /**
     * Show a Yes-No dialog and call the appropriate callback
     *
     * @param from - calling object, used only for diagnostic loggin
     * @param ctx - a context
     * @param titleId - title resource ID
     * @param explanationId - explanation resource ID
     * @param yesListener - callback for YES
     * @param noListener - callback for NO
     */
    public static void showYesNoDialog(Object from, Context ctx, int titleId, int explanationId,
                                       DialogInterface.OnClickListener yesListener, DialogInterface.OnClickListener noListener) {

        U.SC(from, "showYesNoDialog()");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setTitle(titleId);
        alertDialogBuilder.setMessage(explanationId);
        alertDialogBuilder.setPositiveButton(R.string.yes, yesListener);
        alertDialogBuilder.setNegativeButton(R.string.no, noListener);
        alertDialogBuilder.create().show();

    }
}
