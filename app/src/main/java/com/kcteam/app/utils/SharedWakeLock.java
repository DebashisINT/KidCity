/*
 *
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license.
 *
 * Project Oxford: http://ProjectOxford.ai
 *
 * Project Oxford Mimicker Alarm Github:
 * https://github.com/Microsoft/ProjectOxford-Apps-MimickerAlarm
 *
 * Copyright (c) Microsoft Corporation
 * All rights reserved.
 *
 * MIT License:
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED ""AS IS"", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.kcteam.app.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

public class SharedWakeLock {
    private static final String TAG = "com.fieldtrackingsystem.app";
    private static SharedWakeLock sWakeLock;

    private PowerManager.WakeLock mFullWakeLock;
    private PowerManager.WakeLock mPartialWakeLock;

    @SuppressWarnings("deprecation")
    private SharedWakeLock(Context context) {
        Context appContext = context.getApplicationContext();
        PowerManager pm = (PowerManager) appContext.getSystemService(Context.POWER_SERVICE);
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

        if (km.inKeyguardRestrictedInputMode()) {
            km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            KeyguardManager.KeyguardLock mKeyguardLock = km.newKeyguardLock(TAG);
            mKeyguardLock.disableKeyguard();
        }
        mFullWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, TAG);
       // mFullWakeLock = pm.newWakeLock((PowerManager.FULL_WAKE_LOCK | PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP ), TAG);
        mPartialWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
    }

    // Not threadsafe
    public static SharedWakeLock get(Context context) {
        if (sWakeLock == null) {
            sWakeLock = new SharedWakeLock(context);
            Log.d("SharedWakeLock", "Initialized shared WAKE_LOCKs!");
        }
        return sWakeLock;
    }

    public void acquireFullWakeLock() {
        if (!mFullWakeLock.isHeld()) {
            mFullWakeLock.acquire(10*60*1000L /*10 minutes*/);
            Log.d("SharedWakeLock", "Acquired Full WAKE_LOCK!");
        }
    }

    public void releaseFullWakeLock() {
        if (mFullWakeLock.isHeld()) {
            mFullWakeLock.release();
            Log.d("SharedWakeLock", "Released Full WAKE_LOCK!");
        }
    }

    public void acquirePartialWakeLock() {
        if (!mPartialWakeLock.isHeld()) {
            mPartialWakeLock.acquire();
            Log.d("SharedWakeLock", "Acquired Partial WAKE_LOCK!");
        }
    }

    public void releasePartialWakeLock() {
        if (mPartialWakeLock.isHeld()) {
            mPartialWakeLock.release();
            Log.d("SharedWakeLock", "Released Partial WAKE_LOCK!");
        }
    }


}
