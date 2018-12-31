package com.github.crotalustigris.cal_alert.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.crotalustigris.cal_alert.R;
import com.github.crotalustigris.cal_alert.app.helper.ListenerStatusHelper;
import com.github.crotalustigris.cal_alert.app.helper.PreferencesHelper;
import com.github.crotalustigris.cal_alert.u.U;

/*
 * This is the main activity for the Calendar / Notification alert
 * enhancement app.
 *
 * This is based on demonstration code by Fábio Alves Martins Pereira (Chagall)
 * found on github
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 John Moore, Tiny Vital Systems LLC, Phoenix, AZ, USA
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Initial activity class for Calendar Alert
 * <p>
 * Handles:
 * Initialization
 * Checking for and acquiring permission to listen to notifications
 * Settings dialog
 * Termination of notification listeniing
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setEnableDisableStatusAndButton();
        // Because Android strips new line characters from text held in resources, I am forced to put the text here. Ugh.
        ((EditText)findViewById(R.id.main_act_help_text)).setText("a:    (Appointment), Speak\nr:    (Reminder)\nr:sp: (Reminder) Speak\nsp:   (event) Speak\n…     (event or text)", TextView.BufferType.EDITABLE);
    }

    /**
     * The app will not work until the user has enabled it as a notification listener in the
     * Android settings. This method checks for that, and if not enabled, prompts the user.
     * <p>
     * If the user does not enable it after two tries, the application terminates.
     */
    @Override
    protected void onResume() {
        U.SC(this, "onResume()");
        super.onResume();
        ListenerStatusHelper helper = new ListenerStatusHelper();
        if (!helper.isListenerEnabled(getApplicationContext(), getPackageName())) {
            helper.askUserToEnableListener(this);
        }
    }

    /**
     * Skeleton - not currently used
     * @param menu - the menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Skeleton - not currently used
     * @param item - selected menu item
     * @return true or false
     */
    @SuppressWarnings("EmptyMethod")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the button to enable/disable notification handling.
     * Also sets value to display whether it is enabled or not.
     */
    private void setEnableDisableStatusAndButton() {
        Button button = this.findViewById(R.id.main_act_button_enable_disable);
        TextView status = this.findViewById(R.id.main_act_textview_enable_disable);
        if (PreferencesHelper.getEnabledState(this)) {
            button.setText(R.string.main_act_button_disable_alerts);
            status.setText(R.string.main_act_enabled);
            status.setTextColor(this.getResources().getColor(R.color.main_view_green));
            status.clearAnimation();
        } else {
            button.setText((R.string.main_act_button_enable_alerts));
            status.setText(R.string.main_act_disabled);
            status.setTextColor(this.getResources().getColor(R.color.main_view_red));
            Animation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(400);
            anim.setRepeatMode(Animation.REVERSE);
            anim.setRepeatCount(Animation.INFINITE);
            status.startAnimation(anim);
        }
        button.setOnClickListener((view) -> {
            PreferencesHelper.saveEnabledState(this, !PreferencesHelper.getEnabledState(this));
            setEnableDisableStatusAndButton();
        });
    }
}

