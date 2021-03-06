package com.mag.denis.game.ui.settings

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mag.denis.game.R
import com.mag.denis.game.ui.BaseActivity
import com.mag.denis.game.ui.main.view.blocks.actionview.PlaceholderView
import com.mag.denis.game.ui.menu.MenuActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*
import javax.inject.Inject


class SettingsActivity : BaseActivity(), SettingsView {

    @Inject lateinit var presenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btBack.setOnClickListener { presenter.onBackClicked(getStages()) }
        btnKotlin.setOnClickListener { presenter.onKotlinClicked() }
        btnPython.setOnClickListener { presenter.onPythonClicked() }
        btnEng.setOnClickListener { presenter.onEngClicked() }
        btnSlo.setOnClickListener { presenter.onSloClicked() }
        initReorderThemes()

        presenter.onCreate()
    }

    private fun initReorderThemes() {
        llActions.setOnDragListener { v, event -> getDragListener(v, event) }
        tvStage1.setOnTouchListener { v, event -> getTouchListener(v, event) }
        tvStage2.setOnTouchListener { v, event -> getTouchListener(v, event) }
        tvStage3.setOnTouchListener { v, event -> getTouchListener(v, event) }
    }

    override fun selectProgramingLanguage(isKotlin: Boolean) {
        btnKotlin.isActivated = isKotlin
        btnPython.isActivated = !isKotlin
    }

    override fun selectLanguage(isEng: Boolean) {
        btnEng.isActivated = isEng
        btnSlo.isActivated = !isEng
    }

    private fun getDragListener(v: View, e: DragEvent): Boolean {
        if (e.action == DragEvent.ACTION_DROP || e.action == DragEvent.ACTION_DRAG_ENDED) {
            when (e.localState) {
                is TextView -> {
                    val draggedView = e.localState as TextView
                    when (e.action) {
                        DragEvent.ACTION_DROP -> {
                            val owner = draggedView.parent as ViewGroup
                            owner.removeView(draggedView)
                            val container = v as PlaceholderView
                            container.addView(draggedView)
                            draggedView.visibility = View.VISIBLE
                        }
                        DragEvent.ACTION_DRAG_ENDED -> {
                            draggedView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        return true
    }

    private fun getTouchListener(v: View, event: MotionEvent): Boolean {
        return when {
            event.action == MotionEvent.ACTION_DOWN -> {
                v.visibility = View.VISIBLE
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(v)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    v.startDragAndDrop(data, shadowBuilder, v, 0)
                } else {
                    @Suppress("DEPRECATION")
                    v.startDrag(data, shadowBuilder, v, 0)
                }
                true
            }
            event.action == MotionEvent.ACTION_UP -> {
                true
            }
            else -> false
        }
    }

    override fun closeView() {
        finish()
    }

    override fun onBackPressed() {
        presenter.onBackClicked(getStages())
        super.onBackPressed()
    }

    override fun setupStages(stage1: String, stage2: String, stage3: String) {
        tvStage1.text = stage1
        tvStage2.text = stage2
        tvStage3.text = stage3
    }

    override fun recreateView() {
        val intent = MenuActivity.newIntent(this)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivities(arrayOf(intent, SettingsActivity.newIntent(this)))
        overridePendingTransition(0, 0)
    }

    private fun getStages(): ArrayList<String> {
        return arrayListOf((llActions.getChildAt(0) as TextView).text.toString(), (llActions.getChildAt(1) as TextView).text.toString(), (llActions.getChildAt(2) as TextView).text.toString())
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}
