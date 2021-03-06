package com.mag.denis.game.ui.level

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.mag.denis.game.R
import com.mag.denis.game.service.MusicService
import com.mag.denis.game.ui.BaseActivity
import com.mag.denis.game.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_map.*
import javax.inject.Inject


class LevelActivity : BaseActivity(), LevelView {

    @Inject lateinit var presenter: LevelPresenter

    private var interstitialAd: InterstitialAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        btBack.setOnClickListener { presenter.onBackClicked() }
        ibNext.setOnClickListener { presenter.onNextClick() }
        ibPrevious.setOnClickListener { presenter.onPrevClick() }

        presenter.onCreate()

        MobileAds.initialize(this, ADMOB_ACCOUNT_ID)
        interstitialAd = InterstitialAd(this)
        interstitialAd?.adUnitId = ADMOB_APP_ID_INTERSTITIAL
        interstitialAd?.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        presenter.onResume()
        super.onResume()
        if (interstitialAd?.isLoaded() == true) {
            interstitialAd?.show()
        } else {
            Log.d("TAG", "The interstitial wasn't loaded yet.");
        }
    }

    override fun onPause() {
        presenter.onPause()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GAME_PLAY -> {
                if (resultCode == Activity.RESULT_OK) {
                    presenter.onLevelComplete()
                }
            }
            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun openGameView() {
        startActivityForResult(MainActivity.newIntent(this), REQUEST_GAME_PLAY)
    }

    override fun closeView() {
        finish()
    }

    override fun enableNext(enabled: Boolean) {
        ibNext.visibility = if (enabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    override fun enablePrev(enabled: Boolean) {
        ibPrevious.visibility = if (enabled) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    override fun setStageTitle(@StringRes titleId: Int) {
        tvStage.setText(titleId)
    }

    override fun animateLevels() {
        glLevels.visibility = View.GONE
        TransitionManager.beginDelayedTransition(glLevels)
        glLevels.visibility = View.VISIBLE
        TransitionManager.beginDelayedTransition(glLevels)
    }

    override fun setupLevel(level: Int, enabled: Boolean, stars: Int, animate: Boolean) {
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(10, 0, 10, 10)

        val tv = Button(this).apply {
            id = level
            text = level.toString()
            setBackgroundResource(R.drawable.bg_placeholder_table)
            layoutParams = params
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
            gravity = Gravity.CENTER
            setOnClickListener { presenter.onLevelClicked(level) }
        }

        val img = if (enabled) {
            val drawable = when (stars) {
                0 -> R.drawable.ic_star_0
                1 -> R.drawable.ic_star_1
                2 -> R.drawable.ic_star_2
                3 -> R.drawable.ic_star_3
                else -> throw IllegalStateException("Drawable for so many stars not supported")
            }

            ContextCompat.getDrawable(this, drawable)?.apply { setBounds(0, 0, 150, 80) }
        } else {
            ContextCompat.getDrawable(this, R.drawable.ic_lock)?.apply { setBounds(0, 0, 80, 80) }
        }


        tv.setCompoundDrawables(null, null, null, img)
        tv.isEnabled = enabled
        glLevels.addView(tv)
        if (animate) {
            animate(tv)
        }
    }

    override fun clearLevels() {
        glLevels.removeAllViews()
    }

    override fun startMusicService() {
        ActivityCompat.startForegroundService(this, MusicService.newIntent(this))
    }

    override fun stopMusicService() {
        stopService(MusicService.newIntent(this))
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    private fun animate(view: View) {
        val anim = ObjectAnimator.ofFloat(view, "rotation", 360f)
        anim.duration = 500
        anim.start()
    }

    companion object {
        private const val ADMOB_ACCOUNT_ID = "ca-app-pub-8587807693891191~9353326209"
        private const val ADMOB_APP_ID_INTERSTITIAL = "ca-app-pub-8587807693891191/6161268220"

        const val REQUEST_GAME_PLAY = 1

        fun newIntent(context: Context): Intent {
            return Intent(context, LevelActivity::class.java)
        }
    }
}
