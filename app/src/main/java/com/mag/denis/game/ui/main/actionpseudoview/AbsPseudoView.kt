package com.mag.denis.game.ui.main.actionpseudoview

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentManager
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.mag.denis.game.R
import com.mag.denis.game.ui.main.model.Command
import io.reactivex.BackpressureStrategy
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.partial_pseudo_view.view.*


abstract class AbsPseudoView(context: Context, attributes: AttributeSet) : ConstraintLayout(context, attributes), TextWatcher, View.OnKeyListener {
    internal open val reservedLoopDefinition = "for"
    internal val reservedConditionIf = "if"
    internal val reservedConditionElse = "else"
    internal val reservedIn = "in"
    internal val reservedRange = "range"
    internal val moveUp = "moveUp()"
    internal val moveDown = "moveDown()"
    internal val moveRight = "moveRight()"
    internal val moveLeft = "moveLeft()"
    internal val conditionGreenLeaf = "isGreenLeaf()"
    internal val conditionBrownLeaf = "isBrownLeaf()"
    internal val space = "    "

    internal var backspacePressed = false
    internal var maxLastIndex = -1

    private var textChangeListener: Disposable? = null
    private val textChangeSubscribe = PublishSubject.create<Editable>()

    init {
        inflate(context, R.layout.partial_pseudo_view, this)
    }

    fun setupViews(fragmentManager: FragmentManager) {
        super.onAttachedToWindow()
        etCode.addTextChangedListener(this)
        etCode.setOnKeyListener(this)

        textChangeListener?.dispose()
        textChangeListener = textChangeSubscribe
                .toFlowable(BackpressureStrategy.LATEST)
                .map { colorAndAddSpacing(it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    etCode.removeTextChangedListener(this)
                    etCode.setText(it, TextView.BufferType.SPANNABLE)
                    etCode.setSelection(maxLastIndex)
                    etCode.addTextChangedListener(this)
                }, {
                    //TODO handle error
                })
    }

    override fun afterTextChanged(s: Editable) {
        textChangeSubscribe.onNext(s)
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                backspacePressed = true
            }
        }
        return false
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        //Nothing to do
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        //Nothing to do
    }

    abstract fun getActions(): ArrayList<Command>

    abstract fun colorAndAddSpacing(s: Editable): Editable

    internal fun getCondition(child: String): String {
        val conditionStartIndex = child.indexOfFirst { it == '(' } + 1
        val conditionEndIndex = child.indexOfLast { it == ')' }
        if (conditionStartIndex == -1 || conditionEndIndex == -1 || (conditionStartIndex > conditionEndIndex)) {
            //TODO handle error
            throw IllegalStateException("Problem with condition")
        }
        return child.substring(conditionStartIndex, conditionEndIndex).trim()
    }
}
