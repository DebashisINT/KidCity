package com.kcteam.features.micro_learning.presentation

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.kcteam.R
import com.kcteam.app.NetworkConstant
import com.kcteam.app.Pref
import com.kcteam.app.utils.AppUtils
import com.kcteam.app.utils.Toaster
import com.kcteam.base.BaseResponse
import com.kcteam.base.presentation.BaseActivity
import com.kcteam.features.micro_learning.api.MicroLearningRepoProvider
import com.kcteam.features.micro_learning.model.MicroLearningDataModel
import com.elvishew.xlog.XLog
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player.*
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.pnikosis.materialishprogress.ProgressWheel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.text.DecimalFormat
import kotlin.math.roundToLong

class ExoPlayerActivity : AppCompatActivity() {

    private lateinit var playerView: PlayerView
    private lateinit var progress_wheel: ProgressWheel

    private lateinit var simpleExoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private lateinit var iv_full_screen: AppCompatImageView

    private var mMicroLearning: MicroLearningDataModel? = null

    private var flag = false
    private var isOnBackPressed = false
    private var percentage = 0.0f

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_exoplayer)

        initView()

        mMicroLearning = intent?.getSerializableExtra("learning") as MicroLearningDataModel?
    }

    private fun initView() {
        //iv_full_screen =  findViewById(R.id.iv_full_screen)
        playerView = findViewById(R.id.playerView)
        progress_wheel = findViewById(R.id.progress_wheel)
        progress_wheel.stopSpinning()
    }

    public override fun onStart() {
        super.onStart()

        if (Util.SDK_INT > 23)
            initializePlayer()
    }

    public override fun onResume() {
        super.onResume()

        if (Util.SDK_INT <= 23)
            initializePlayer()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun initializePlayer() {
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this)

        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))

        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse(mMicroLearning?.url))

        simpleExoPlayer.playWhenReady = mMicroLearning?.play_when_ready!!
        simpleExoPlayer.seekTo(mMicroLearning?.current_window?.toInt()!!, mMicroLearning?.play_back_position?.toLong()!!)
        simpleExoPlayer.prepare(mediaSource, false, false)

        playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        playerView.player = simpleExoPlayer
        playerView.requestFocus()

        /** Default repeat mode is REPEAT_MODE_OFF *//*
        btnChangeRepeatMode.setOnClickListener {
            when (simpleExoPlayer.repeatMode) {
                REPEAT_MODE_OFF -> simpleExoPlayer.repeatMode = REPEAT_MODE_ONE
                REPEAT_MODE_ONE -> {
                    simpleExoPlayer.repeatMode = REPEAT_MODE_ALL
                }
                else -> {
                    simpleExoPlayer.repeatMode = REPEAT_MODE_OFF
                }
            }
        }*/

        simpleExoPlayer.addListener( object : Player.EventListener{
            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {
            }

            override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                error?.printStackTrace()
            }

            /** 4 playbackState exists */
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                when(playbackState){
                    STATE_BUFFERING -> {
                        progress_wheel.spin()
                    }
                    STATE_READY -> {
                        progress_wheel.stopSpinning()
                    }
                    STATE_IDLE -> {
                    }
                    STATE_ENDED -> {
                    }
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
            }

            override fun onPositionDiscontinuity(reason: Int) {
            }

            override fun onRepeatModeChanged(repeatMode: Int) {
            }

            override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {
            }
        })

        /*iv_full_screen.setOnClickListener {
            if (flag) {
                iv_full_screen.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen))
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                flag = false
            } else {
                iv_full_screen.setImageDrawable(resources.getDrawable(R.drawable.ic_fullscreen_exit))
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                flag = true
            }
        }*/
    }

    override fun onPause() {
        super.onPause()
        /*simpleExoPlayer.playWhenReady = false
        simpleExoPlayer.playbackState*/
    }

    override fun onRestart() {
        super.onRestart()
        /*simpleExoPlayer.playWhenReady = true
        simpleExoPlayer.playbackState*/
    }

    override fun onBackPressed() {
        if (isOnBackPressed)
            return

        isOnBackPressed = true

        releasePlayer()
        updateVideoPosition()
    }

    private fun releasePlayer() {
        //simpleExoPlayer.release()
        simpleExoPlayer?.apply {
            mMicroLearning?.play_when_ready = playWhenReady
            mMicroLearning?.play_back_position = currentPosition.toString()
            mMicroLearning?.current_window = currentWindowIndex.toString()
            val diff = currentPosition.toFloat() / duration
            Log.e("Exo Player", "Current Position===========> $currentPosition")
            Log.e("Exo Player", "duration===============> $duration")
            Log.e("Exo Player", "diff===============> $diff")
            Log.e("Exo Player", "percentage===============> ${diff * 100}")
            val twoDForm = DecimalFormat("##.##")
            percentage = twoDForm.format((diff * 100)).toFloat() //(duration - currentPosition) / 100
            release()
        }
    }

    private fun updateVideoPosition() {
        if (!AppUtils.isOnline(this)) {
            isOnBackPressed = false
            Toaster.msgShort(this, getString(R.string.no_internet))
            return
        }

        progress_wheel.spin()
        val repository = MicroLearningRepoProvider.microLearningRepoProvider()
        BaseActivity.compositeDisposable.add(
                repository.updateVideoPosition(mMicroLearning?.id!!, mMicroLearning?.current_window!!, mMicroLearning?.play_back_position!!,
                        mMicroLearning?.play_when_ready!!, percentage)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({ result ->
                            val response = result as BaseResponse
                            XLog.d("UPDATE VIDEO POSITION: " + "RESPONSE : " + response.status + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + response.message)
                            progress_wheel.stopSpinning()
                            //Toaster.msgShort(this, response.message!!)

                            if (response.status == NetworkConstant.SUCCESS) {
                                Handler().postDelayed(Runnable {
                                    setResult(Activity.RESULT_OK, Intent())
                                    finish()
                                }, 500)
                            }
                            else {
                                isOnBackPressed = false
                                Toaster.msgShort(this, response.message!!)
                            }

                        }, { error ->
                            progress_wheel.stopSpinning()
                            isOnBackPressed = false
                            XLog.d("UPDATE VIDEO POSITION: " + "ERROR : " + "\n" + "Time : " + AppUtils.getCurrentDateTime() + ", USER :" + Pref.user_name + ",MESSAGE : " + error.localizedMessage)
                            error.printStackTrace()
                            Toaster.msgShort(this, getString(R.string.something_went_wrong))
                        })
        )
    }
}