package com.mag.denis.game.ui.main.actions.actionblockview

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentManager
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.LinearLayout
import com.mag.denis.game.R
import com.mag.denis.game.ui.main.MainActivity
import com.mag.denis.game.ui.main.MainActivity.Companion.ACTION_RIGHT
import com.mag.denis.game.ui.main.model.Action
import com.mag.denis.game.ui.main.model.Command
import com.mag.denis.game.ui.main.model.Loop
import com.mag.denis.game.ui.main.view.action.ActionImageView
import com.mag.denis.game.ui.main.view.action.ConditionView
import com.mag.denis.game.ui.main.view.action.LoopView
import kotlinx.android.synthetic.main.partial_action_animation.view.*

class AnimationIntroView : ConstraintLayout {
    private var callback: AnimationIntroCallback? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        inflate(context, R.layout.partial_action_animation, this)
        this.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
    }

    fun startAnimationAction() {
        val actionRightAnim = ActionImageView(context, R.drawable.ic_arrow_right, MainActivity.ACTION_RIGHT, viewWidth = resources.getDimensionPixelSize(R.dimen.actionWidth))
        val handPointer = ImageView(context)
        handPointer.setImageResource(R.drawable.ic_touch)
        animationConstraint.addView(actionRightAnim)
        animationConstraint.addView(handPointer)

        val anim = TranslateAnimation(0f, 300f, 50f, 50f)
        anim.duration = 2000

        anim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {

                val anim1 = TranslateAnimation(300f, width.toFloat() / 3 - handPointer.width / 2, 50f, height.toFloat() - handPointer.height * 1.5f)
                anim1.duration = 2000

                anim1.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        val arrowJump = AppCompatImageView(context)
                        arrowJump.setImageResource(R.drawable.ic_curved_arrow)
                        arrowJump.x = animationConstraint.width / 2.5f
                        arrowJump.y = animationConstraint.height / 7f

                        animationConstraint.addView(arrowJump)

                        callback?.animationWantResetGame()
                        callback?.onStartAnimationClick(getActions())
                    }
                })

                anim1.fillAfter = true
                handPointer.startAnimation(anim1)
            }
        })

        anim.fillAfter = true
        handPointer.startAnimation(anim)
        actionRightAnim.startAnimation(anim)

        btSkipIntro.visibility = View.VISIBLE
        btSkipIntro.setOnClickListener {
            hide()
            callback?.animationWantResetGame()
        }
    }

    fun startAnimationLoop() {
        val actionRightAnim = ActionImageView(context, R.drawable.ic_arrow_right, MainActivity.ACTION_RIGHT, viewWidth = resources.getDimensionPixelSize(R.dimen.actionWidth))
        val handPointer = ImageView(context)
        val loop1 = LoopView(context)

        actionRightAnim.visibility = View.GONE
        handPointer.setImageResource(R.drawable.ic_touch)
        animationConstraint.addView(actionRightAnim)
        animationConstraint.addView(handPointer)
        animationConstraint.addView(loop1)

        val anim = TranslateAnimation(0f, 300f, 0f, 50f)
        anim.duration = 2000

        anim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                loop1.setValue(4)
                actionRightAnim.visibility = View.VISIBLE
                val anim = TranslateAnimation(0f, 300f, 0f, 80f)
                anim.duration = 2000

                anim.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        animationConstraint.removeView(actionRightAnim)
                        loop1.getPlaceholder().addView(actionRightAnim)
                        actionRightAnim.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

                        val anim1 = TranslateAnimation(300f, width.toFloat() / 3 - handPointer.width / 2, 50f, height.toFloat() - handPointer.height * 1.5f)
                        anim1.duration = 2000

                        anim1.setAnimationListener(object : Animation.AnimationListener {

                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationRepeat(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {
                                callback?.animationWantResetGame()
                                callback?.onStartAnimationClick(getActions())
                            }
                        })

                        anim1.fillAfter = true
                        handPointer.startAnimation(anim1)
                    }
                })

                anim.fillAfter = true
                handPointer.startAnimation(anim)
                actionRightAnim.startAnimation(anim)
            }
        })

        anim.fillAfter = true
        handPointer.startAnimation(anim)
        loop1.startAnimation(anim)

        btSkipIntro.visibility = View.VISIBLE
        btSkipIntro.setOnClickListener {
            hide()
            callback?.animationWantResetGame()
        }
    }

    fun startAnimationLoopAndIf(supportFragmentManager: FragmentManager) {
        val actionRightAnim = ActionImageView(context, R.drawable.ic_arrow_right, MainActivity.ACTION_RIGHT, viewWidth = resources.getDimensionPixelSize(R.dimen.actionWidth))
        val handPointer = ImageView(context)
        val loop1 = LoopView(context)
        val ifView = ConditionView(context,supportFragmentManager)

        actionRightAnim.visibility = View.GONE
        handPointer.setImageResource(R.drawable.ic_touch)
        animationConstraint.addView(actionRightAnim)
        animationConstraint.addView(handPointer)
        animationConstraint.addView(loop1)
        animationConstraint.addView(ifView)

        val anim = TranslateAnimation(0f, 300f, 0f, 50f)
        anim.duration = 2000

        anim.setAnimationListener(object : Animation.AnimationListener {

            override fun onAnimationStart(animation: Animation) {}

            override fun onAnimationRepeat(animation: Animation) {}

            override fun onAnimationEnd(animation: Animation) {
                loop1.setValue(4)
                actionRightAnim.visibility = View.VISIBLE
                val anim = TranslateAnimation(0f, 300f, 0f, 80f)
                anim.duration = 2000

                anim.setAnimationListener(object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationRepeat(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        animationConstraint.removeView(ifView)
                        loop1.getPlaceholder().addView(ifView)
                        ifView.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

                        val anim = TranslateAnimation(0f, 300f, 0f, 80f)
                        anim.duration = 2000

                        anim.setAnimationListener(object : Animation.AnimationListener {

                            override fun onAnimationStart(animation: Animation) {}

                            override fun onAnimationRepeat(animation: Animation) {}

                            override fun onAnimationEnd(animation: Animation) {

                                val anim1 = TranslateAnimation(300f, width.toFloat() / 3 - handPointer.width / 2, 50f, height.toFloat() - handPointer.height * 1.5f)
                                anim1.duration = 2000

                                anim1.setAnimationListener(object : Animation.AnimationListener {

                                    override fun onAnimationStart(animation: Animation) {}

                                    override fun onAnimationRepeat(animation: Animation) {}

                                    override fun onAnimationEnd(animation: Animation) {
                                        callback?.animationWantResetGame()
                                        callback?.onStartAnimationClick(getActions())
                                    }
                                })

                                anim1.fillAfter = true
                                handPointer.startAnimation(anim1)
                            }
                        })

                        anim.fillAfter = true
                        handPointer.startAnimation(anim)
                        actionRightAnim.startAnimation(anim)
                    }
                })

                anim.fillAfter = true
                handPointer.startAnimation(anim)
                ifView.startAnimation(anim)
            }
        })

        anim.fillAfter = true
        handPointer.startAnimation(anim)
        loop1.startAnimation(anim)

        btSkipIntro.visibility = View.VISIBLE
        btSkipIntro.setOnClickListener {
            hide()
            callback?.animationWantResetGame()
        }
    }


    fun setAnimationIntroCallback(callback: AnimationIntroCallback) {
        this.callback = callback
    }

    interface AnimationIntroCallback {
        fun onStartAnimationClick(commands: ArrayList<Command>)
        fun animationWantResetGame()
    }

//    private fun getActions(): ArrayList<Command> {
//        return arrayListOf(Action(ACTION_RIGHT))
//    }

    private fun getActions(): ArrayList<Command> {
        return arrayListOf(Loop(4, listOf(Action(ACTION_RIGHT))))
    }

    fun hide() {
        this.visibility = View.GONE
    }
}
