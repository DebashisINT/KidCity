package com.kcteam.app

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.crashlytics.android.Crashlytics
import com.kcteam.app.utils.AppUtils
import com.elvishew.xlog.LogConfiguration
import com.elvishew.xlog.XLog
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor
import com.elvishew.xlog.printer.AndroidPrinter
import com.elvishew.xlog.printer.Printer
import com.elvishew.xlog.printer.file.FilePrinter
import com.elvishew.xlog.printer.file.backup.FileSizeBackupStrategy2
import com.elvishew.xlog.printer.file.clean.FileLastModifiedCleanStrategy
import com.facebook.stetho.Stetho
import com.marcinmoskala.kotlinpreferences.PreferenceHolder
import io.fabric.sdk.android.Fabric
import java.io.File


class FTSApp : MultiDexApplication() {

    lateinit var appComponent: AppComponent
    var globalFilePrinter: Printer? = null

    @Suppress("INTEGER_OVERFLOW")
    private val MAX_TIME: Long = 1000 * 60 * 60 * 24 * 300

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


    override fun onCreate() {
        super.onCreate()
//        appComponent = buildAppComponent()
        initXlog()

        AppDatabase.initAppDatabase(this)
        Stetho.initializeWithDefaults(this)
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        PreferenceHolder.setContext(applicationContext)
        Fabric.with(this, Crashlytics())

    }


    /* private fun buildAppComponent(): AppComponent {
         return DaggerAppComponent.builder()
                 .appModule(AppModule(this))
                 .build()
     }*/


    /**
     * Initialize XLog.
     */
    private fun initXlog() {


        println("initXlog - FTSApp " + AppUtils.getCurrentDateTime().toString())
        try {
            val config = LogConfiguration.Builder()
//                .logLevel(if (BuildConfig.DEBUG)
//                    LogLevel.ALL             // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
//                else
//                    LogLevel.NONE)
//                .tag(getString(R.string.global_tag))                   // Specify TAG, default: "X-LOG"
                // .t()                                                // Enable thread info, disabled by default
                // .st(2)                                              // Enable stack trace info with depth 2, disabled by default
                // .b()                                                // Enable border, disabled by default
                // .jsonFormatter(new MyJsonFormatter())               // Default: DefaultJsonFormatter
                // .xmlFormatter(new MyXmlFormatter())                 // Default: DefaultXmlFormatter
                // .throwableFormatter(new MyThrowableFormatter())     // Default: DefaultThrowableFormatter
                // .threadFormatter(new MyThreadFormatter())           // Default: DefaultThreadFormatter
                // .stackTraceFormatter(new MyStackTraceFormatter())   // Default: DefaultStackTraceFormatter
                // .borderFormatter(new MyBoardFormatter())            // Default: DefaultBorderFormatter
                // .addObjectFormatter(AnyClass.class,                 // Add formatter for specific class of object
                //     new AnyClassObjectFormatter())                  // Use Object.toString() by default
                .addInterceptor(BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
                    "blacklist1", "blacklist2", "blacklist3"))
                // .addInterceptor(new WhitelistTagsFilterInterceptor( // Add whitelist tags filter
                //     "whitelist1", "whitelist2", "whitelist3"))
                // .addInterceptor(new MyInterceptor())                // Add a log interceptor
                .build()

            val androidPrinter = AndroidPrinter()             // Printer that print the log using android.util.Log
            //val filePrinter = FilePrinter.Builder(File(getExternalStorageDirectory(), "xkcteamlogsample").path)// Printer that print the log to the file system
            val filePrinter = FilePrinter.Builder(File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "xkcteamlogsample").path)// Printer that print the log to the file system
                // Specify the path to save log file
//                .fileNameGenerator(ChangelessFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                //.backupStrategy(NeverBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
                //.backupStrategy(FileSizeBackupStrategy2(50*1024,1))             // Default: FileSizeBackupStrategy(1024 * 1024)
                .backupStrategy(FileSizeBackupStrategy2(1024*1024*40,1))
                    // Default: FileSizeBackupStrategy(1024 * 1024)
                //.logFlattener(ClassicFlattener())                  // Default: DefaultFlattener
                .cleanStrategy(FileLastModifiedCleanStrategy(MAX_TIME))
                //.cleanStrategy(FileLastModifiedCleanStrategy(9000000))  //2.5 hts
                //.cleanStrategy(FileLastModifiedCleanStrategy(57000000)) // 15.8 hrs
                .build()

            XLog.init(                                                 // Initialize XLog
                config, // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter, // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                filePrinter)

            // For future usage: partial usage in MainActivity.
            globalFilePrinter = filePrinter
            println("initXlog - filePrinter done " + AppUtils.getCurrentDateTime().toString())
        }catch (ex:Exception){
            println("initXlog - exception ${ex.message} " + AppUtils.getCurrentDateTime().toString())
        }

    }


}