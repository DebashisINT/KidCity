package com.kcteam.app.utils

import android.app.KeyguardManager
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import android.util.Log
import java.security.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class CheckFingerPrint {

    val TAG = "CheckFingerPrint"
    //lateinit var cryptoPrefs: CryptoPrefs
    lateinit var fingerPrintManager: FingerprintManager
    lateinit var keyguardManager: KeyguardManager
    lateinit var keyStore: KeyStore
    lateinit var keyGenerator: KeyGenerator
    lateinit var cryptoObj: FingerprintManager.CryptoObject
    lateinit var fingerprintHandler: FingerprintHandler
    lateinit var fingerPrintListener: FingerPrintListener
    lateinit var context: Context
    var isFingerPrintSuccess:Boolean = false
    //var signal = CancellationSignal()

    var signal: CancellationSignal? = null

    /* //Fingerprint for samsung device
     var mSpassFingerprint: SpassFingerprint? = null
     var needRetryIdentify = false
     var onReadyIdentify = false*/

    interface FingerPrintListener {
        fun onSuccess(signal: CancellationSignal?)
        fun onError(msg: String)
        fun isFingerPrintSupported(status:Boolean)
    }

    fun checkFingerPrint(context: Context, fingerPrintListener: FingerPrintListener) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                this.context = context
                this.fingerPrintListener = fingerPrintListener
                if (checkCompatibility()) {
                    fingerPrintListener.isFingerPrintSupported(true)
                    /* Step 4 : Generate the cipher and create crypto Object with it */
                    cryptoObj = FingerprintManager.CryptoObject(generateCipher())
                    fingerprintHandler = FingerprintHandler()
                } else {
                    fingerPrintListener.isFingerPrintSupported(false)
                }
            }
        }catch(e:Exception){
            e.printStackTrace()
        }
    }

    private fun checkCompatibility(): Boolean {
        /* Step 1 : Check whether the android version is above Marshmallow or not */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            /* Step 2 : Check whether the Keyguard is secured with a PIN or password or pattern */
            if (isHardwareCompatible()) {
                /*cryptoPrefs = CryptoPrefs(context, Constants.Keys._CryptoPrefsFilename, Constants.Keys._KeyCryptoPreference, true)
                cryptoPrefs.put(Constants.Keys._KEYSTORE_ALIASNAME, Constants.Keys._KEYSTORE_ALIASNAME)*/
                /* Step 3 : The next step is accessing to the Android keystore and generate the key to encrypt the data */
                try {
                    generateKeyProcess()
                }catch(e:Exception){
                    e.printStackTrace()
                }
                return true
            }
        }
        // fingerPrintListener.onError("Device is not compatible to fingerprint")
        return false
    }

    //@RequiresApi(Build.VERSION_CODES.M)
    // @RequiresApi(Build.VERSION_CODES.M)
    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateKeyProcess() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
            keyStore.load(null)
            keyGenerator.init(KeyGenParameterSpec.Builder("_KEYSTORE_ALIASNAME",
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator.generateKey()
        }catch(e:Exception){
            e.printStackTrace()
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun isHardwareCompatible(): Boolean {
        keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        try {
            fingerPrintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        }catch(e:Exception){
            e.printStackTrace()
        }

        try {
            // Check if the fingerprint sensor is present
            if (!fingerPrintManager.isHardwareDetected) {
                // Update the UI with a message
                return false
            }
            if (!fingerPrintManager.hasEnrolledFingerprints()) {
                return false
            }
            // Whether screen lock is configured or not
            if (!keyguardManager.isKeyguardSecure) {
                return false
            }
        } catch (se: SecurityException) {
            Log.e(TAG, se.toString())
        }

        return true
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun generateCipher(): Cipher {
        try {
            val cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            val key = keyStore.getKey("_KEYSTORE_ALIASNAME", null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return cipher
        } catch (exc: NoSuchAlgorithmException) {
            Log.e(TAG, exc.toString())
            throw Exception(exc.toString())
        } catch (exc: NoSuchPaddingException) {
            Log.e(TAG, exc.toString())
            throw Exception(exc.toString())
        } catch (exc: InvalidKeyException) {
            Log.e(TAG, exc.toString())
            throw Exception(exc.toString())
        } catch (exc: UnrecoverableKeyException) {
            Log.e(TAG, exc.toString())
            throw Exception(exc.toString())
        } catch (exc: KeyStoreException) {
            Log.e(TAG, exc.toString())
            throw Exception(exc.toString())
        }
    }



    /* Class to handle finger print callback methods */
    @RequiresApi(Build.VERSION_CODES.M)
    inner class FingerprintHandler : FingerprintManager.AuthenticationCallback() {

        init {
            doAuth()
        }

        @RequiresApi(Build.VERSION_CODES.M)
        fun doAuth() {
            signal = CancellationSignal()
            try {
                if (signal?.isCanceled!!) {
                    signal = CancellationSignal()
                }
                fingerPrintManager.authenticate(cryptoObj, signal, 0, this, null);
            } catch (sce: SecurityException) {
                throw Exception(sce.toString())
            }
        }

        /**
         * Called when an unrecoverable error has been encountered and the operation is complete.
         * No further callbacks will be made on this object.
         * @param errorCode An integer identifying the error message
         * @param errString A human-readable error string that can be shown in UI
         */
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
            super.onAuthenticationError(errorCode, errString)
            //doAuth()
            if(errorCode == FingerprintManager.FINGERPRINT_ERROR_CANCELED){
                stopListening()
                //startListening(c)
            }
            fingerPrintListener.onError(errString.toString())
        }


        fun stopListening() {
            if (checkCompatibility()) {
                try {
                    //var mCancellationSignal = CancellationSignal()
                    // signal.cancel(
                    signal = CancellationSignal()
                    if (signal != null)
                        signal!!.cancel()
                    //signal = null

                    //  doAuth()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }

        /**
         * Called when a fingerprint is recognized.
         * @param result An object containing authentication-related data
         */
        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            //doAuth()
            //isFingerPrintSuccess = true
            if(!isFingerPrintSuccess) {
                isFingerPrintSuccess = true
                fingerPrintListener.onSuccess(signal)
            }
            signal!!.cancel()
        }

        /**
         * Called when a recoverable error has been encountered during authentication. The help
         * string is provided to give the user guidance for what went wrong, such as
         * "Sensor dirty, please clean it."
         * @param helpCode An integer identifying the error message
         * @param helpString A human-readable string that can be shown in UI
         */
        override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
            super.onAuthenticationHelp(helpCode, helpString)
        }

        // Called when a fingerprint is valid but not recognized.
        override fun onAuthenticationFailed() {
            try {
                super.onAuthenticationFailed()
                AppUtils.changeLanguage(context, "en")
                fingerPrintListener.onError("Error : Fingerprint is valid but not recognized")
                signal = CancellationSignal()
                signal!!.cancel()

            }catch (e:Exception){
                e.printStackTrace()
            }
        }
    }
}
