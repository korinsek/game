package com.mag.denis.game.ui.main.view.blocks.interactionview

import android.content.ClipData
import android.content.Context
import android.os.Build
import android.support.constraint.ConstraintLayout
import android.support.v4.app.FragmentManager
import android.util.AttributeSet
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import com.mag.denis.game.R
import com.mag.denis.game.manager.LevelManager
import com.mag.denis.game.manager.LevelManager.Companion.COMMAND_CONDITION
import com.mag.denis.game.manager.LevelManager.Companion.COMMAND_LOOP
import com.mag.denis.game.ui.main.MainActivity
import com.mag.denis.game.ui.main.model.*
import com.mag.denis.game.ui.main.view.blocks.actionview.ActionImageView
import com.mag.denis.game.ui.main.view.blocks.actionview.ConditionView
import com.mag.denis.game.ui.main.view.blocks.actionview.LoopView
import com.mag.denis.game.ui.main.view.blocks.actionview.PlaceholderView
import kotlinx.android.synthetic.main.partial_action_view.view.*

class ActionBlockView : ConstraintLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    init {
        inflate(context, R.layout.partial_action_view, this)
        this.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val paddingPx = resources.getDimensionPixelSize(R.dimen.actionPadding)
        this.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
    }

    fun setupViews(fragmentManager: FragmentManager, availableCommands: List<Int>) {
        super.onAttachedToWindow()

        if (availableCommands.contains(LevelManager.COMMAND_ACTIONS)) {
            val actionUp = ActionImageView(context, R.drawable.ic_arrow_upward, MainActivity.ACTION_UP)
            val actionRight = ActionImageView(context, R.drawable.ic_arrow_right, MainActivity.ACTION_RIGHT)
            val actionLeft = ActionImageView(context, R.drawable.ic_arrow_left, MainActivity.ACTION_LEFT)
            val actionDown = ActionImageView(context, R.drawable.ic_arrow_down, MainActivity.ACTION_DOWN)

            llActions.addView(actionUp)
            llActions.addView(actionRight)
            llActions.addView(actionLeft)
            llActions.addView(actionDown)

            llActions.setOnDragListener { v, event -> getDragListener(v, event) }
            listOf(actionUp, actionRight, actionLeft, actionDown).forEach { view ->
                view.setOnTouchListener { v, event -> getTouchListener(v, event) }
            }
        }
        if (availableCommands.contains(COMMAND_LOOP)) {
            //Loop
            val loop1 = LoopView(context)
            val loop2 = LoopView(context)
            llActions.addView(loop1)

            loop1.apply {
                setOnTouchListener { v, event -> getTouchListener(v, event) }
                setOnDragListener { v, event -> getDragListener(v, event) }
            }

            loop2.apply {
                setOnTouchListener { v, event -> getTouchListener(v, event) }
                setOnDragListener { v, event -> getDragListener(v, event) }
            }
        }

        if (availableCommands.contains(COMMAND_CONDITION)) {

            val if1 = ConditionView(context, fragmentManager)
            llActions.addView(if1)

            if1.apply {
                setOnTouchListener { v, event ->
                    getTouchListener(v, event)
                }
                getTruePlaceholder().setOnDragListener { v, event ->
                    getDragListener(v, event)
                }
                getFalsePlaceholder().setOnDragListener { v, event ->
                    getDragListener(v, event)
                }
            }
        }


        llActionHolder1.setOnDragListener { v, event ->
            getDragListener(v, event)
        }
    }

    private fun getTouchListener(v: View, event: MotionEvent): Boolean {
        return when {
            event.action == MotionEvent.ACTION_DOWN -> {
                v.visibility = View.VISIBLE
                v.alpha = 0.5f
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
                v.alpha = 1f
                true
            }
            else -> false
        }
    }

    private fun getDragListener(v: View, e: DragEvent): Boolean {
        if (e.action == DragEvent.ACTION_DROP || e.action == DragEvent.ACTION_DRAG_ENDED) {
            when (e.localState) {
                is ActionImageView ->
                    when (v) {
                        is PlaceholderView -> handleActionToPlaceholderDrag(v, e)
                        is LoopView -> handleActionToLoopDrag(v, e)
                        is LinearLayout -> handleActionToConditionDrag(v, e)
                    }
                is LoopView -> when (v) {
                    is PlaceholderView -> handleLoopToPlaceholderDrag(v, e)
                    is LoopView -> handleLoopToLoopDrag(v, e)
                    is LinearLayout -> handleLoopToConditionDrag(v, e)
                }
                is ConditionView -> when (v) {
                    is PlaceholderView -> handleConditionToPlaceholderDrag(v, e)
                    is LoopView -> handleConditionToLoopDrag(v, e)
                    is LinearLayout -> handleConditionToConditionDrag(v, e)
                }
            }
        }
        return true
    }

    private fun handleActionToPlaceholderDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as ActionImageView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent != llActions) {
                    val owner = draggedView.parent as ViewGroup
                    if (owner != llActions) {
                        owner.removeView(draggedView)
                    }

                    val newImageView = ActionImageView(context, draggedView.drawableId, draggedView.type)
                    newImageView.setOnTouchListener { v, event ->
                        getTouchListener(v, event)
                    }
                    val container = view as PlaceholderView
                    if (container != llActions) {
                        container.addView(newImageView)
                    }
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleActionToConditionDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as ActionImageView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent.parent.parent != llActions) {
                    val owner = draggedView.parent as ViewGroup
                    if (owner != llActions) {
                        owner.removeView(draggedView)
                    }

                    val newImageView = ActionImageView(context, draggedView.drawableId, draggedView.type)
                    newImageView.setOnTouchListener { v, event ->
                        getTouchListener(v, event)
                    }

                    val container = view as LinearLayout
                    container.addView(newImageView)
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleLoopToConditionDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as LoopView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent.parent.parent != llActions) {
                    val parent = draggedView.parent
                    if (parent != llActions) {
                        val owner = parent as ViewGroup
                        val safe = isSafeToAdd(owner, draggedView)
                        if (safe) {
                            owner.removeView(draggedView)
                            val container = view as LinearLayout
                            container.addView(draggedView)
                        }
                    } else {
                        val owner = parent as ViewGroup
                        if (owner != llActions) {
                            owner.removeView(draggedView)
                        }

                        val newLoop = LoopView(context)
                        newLoop.setOnTouchListener { v, event ->
                            getTouchListener(v, event)
                        }
                        newLoop.setOnDragListener { v, event ->
                            getDragListener(v, event)
                        }

                        val container = view as LinearLayout
                        if (container != llActions) {
                            container.addView(newLoop)
                        }
                    }
                }
                draggedView.alpha = 1f
                draggedView.visibility = View.VISIBLE
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleConditionToConditionDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as ConditionView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent.parent.parent != llActions) {
                    val container = view as LinearLayout
                    if (draggedView.parent != llActions) {
                        val owner = draggedView.parent as ViewGroup
                        val safe = isSafeToAdd(owner, draggedView)
                        if (draggedView != container.parent.parent && safe
                                && draggedView.javaClass != container.parent.parent.javaClass) {
                            owner.removeView(draggedView)
                            container.addView(draggedView)
                        }
                    } else {
                        val owner = draggedView.parent as ViewGroup
                        if (owner != llActions) {
                            owner.removeView(draggedView)
                        }

                        val newCondition = ConditionView(context, draggedView.supportFragmentManager)
                        newCondition.setOnTouchListener { v, event ->
                            getTouchListener(v, event)
                        }

                        newCondition.getTruePlaceholder().setOnDragListener { v, event ->
                            getDragListener(v, event)
                        }
                        newCondition.getFalsePlaceholder().setOnDragListener { v, event ->
                            getDragListener(v, event)
                        }

                        if (container != llActions && draggedView != container.parent) {
                            container.addView(newCondition)
                        }
                    }
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }


    private fun handleLoopToPlaceholderDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as LoopView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent != llActions) {
                    val owner = draggedView.parent as ViewGroup
                    if (owner != llActions) {
                        owner.removeView(draggedView)
                    }

                    val newLoop = LoopView(context)
                    newLoop.setOnTouchListener { v, event ->
                        getTouchListener(v, event)
                    }
                    newLoop.setOnDragListener { v, event ->
                        getDragListener(v, event)
                    }

                    val container = view as PlaceholderView
                    if (container != llActions) {
                        container.addView(newLoop)
                    }
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleConditionToPlaceholderDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as ConditionView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent != llActions) {
                    val owner = draggedView.parent as ViewGroup
                    if (owner != llActions) {
                        owner.removeView(draggedView)
                    }

                    val newCondition = ConditionView(context, draggedView.supportFragmentManager)
                    newCondition.setOnTouchListener { v, event ->
                        getTouchListener(v, event)
                    }

                    newCondition.getTruePlaceholder().setOnDragListener { v, event ->
                        getDragListener(v, event)
                    }
                    newCondition.getFalsePlaceholder().setOnDragListener { v, event ->
                        getDragListener(v, event)
                    }

                    val container = view as PlaceholderView
                    if (container != llActions) {
                        container.addView(newCondition)
                    }
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleActionToLoopDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as ActionImageView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent != llActions) {
                    val owner = draggedView.parent as ViewGroup
                    if (owner != llActions) {
                        owner.removeView(draggedView)
                    }

                    val newImageView = ActionImageView(context, draggedView.drawableId, draggedView.type)
                    newImageView.setOnTouchListener { v, event ->
                        getTouchListener(v, event)
                    }

                    val container = view as LoopView
                    container.getPlaceholder().addView(newImageView)
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleLoopToLoopDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as LoopView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent != llActions) {
                    if (draggedView.parent != llActions) {
                        val container = view as LoopView
                        val owner = draggedView.parent as ViewGroup
                        val safe = isSafeToAdd(owner, draggedView)
                        if (draggedView != container && safe && draggedView.javaClass != container.parent.parent.javaClass) {
                            owner.removeView(draggedView)
                            container.getPlaceholder().addView(draggedView)
                        }
                    } else {
                        val owner = parent as ViewGroup
                        if (owner != llActions) {
                            owner.removeView(draggedView)
                        }

                        val newLoop = LoopView(context)
                                .apply {
                                    setOnTouchListener { v, event -> getTouchListener(v, event) }
                                    setOnDragListener { v, event -> getDragListener(v, event) }
                                }

                        val container = view as LoopView
                        container.getPlaceholder().addView(newLoop)
                    }
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    private fun handleConditionToLoopDrag(view: View, e: DragEvent) {
        val draggedView = e.localState as ConditionView
        when (e.action) {
            DragEvent.ACTION_DROP -> {
                if (view.parent != llActions) {
                    val parent = draggedView.parent
                    if (parent != llActions) {
                        val owner = parent as ViewGroup
                        owner.removeView(draggedView) //odstrani view ce ga hocemo prestavit, view nasem primeru ga pustimo tam
                        val container = view as LoopView
                        container.getPlaceholder().addView(draggedView)
                    } else {

                        val owner = draggedView.parent as ViewGroup
                        if (owner != llActions) {
                            owner.removeView(draggedView)
                        }

                        val newCondition = ConditionView(context, draggedView.supportFragmentManager)
                        newCondition.setOnTouchListener { v, event ->
                            getTouchListener(v, event)
                        }

                        newCondition.getTruePlaceholder().setOnDragListener { v, event ->
                            getDragListener(v, event)
                        }
                        newCondition.getFalsePlaceholder().setOnDragListener { v, event ->
                            getDragListener(v, event)
                        }

                        val container = view as LoopView
                        container.getPlaceholder().addView(newCondition)
                    }
                }
                draggedView.visibility = View.VISIBLE
                draggedView.alpha = 1f
            }
            DragEvent.ACTION_DRAG_ENDED -> {
                draggedView.visibility = View.VISIBLE
            }
        }
    }

    fun getActions(): ArrayList<Command> {
        return getAllChildren(llActionHolder1)
    }

    private fun getAllChildren(v: View): ArrayList<Command> {
        val viewGroup = v as ViewGroup
        if (viewGroup.childCount == 0) {
            throw IllegalStateException("Block field is empty.")
        }

        val list = ArrayList<Command>()

        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)

            when (child) {
                is ActionImageView -> list.add(Action(child.type))
                is LoopView -> {
                    val repeatEt = child.findViewById<EditText>(R.id.etLoopValue)
                    val containerll = child.findViewById<LinearLayout>(R.id.llLoopPlaceholder)
                    val value = if (!repeatEt.text.isNullOrEmpty()) {
                        repeatEt.text.toString().toInt()
                    } else {
                        throw IllegalStateException("Missing repeat value")
                    }

                    list.add(Loop(value, getAllChildren(containerll))) //Recursive call
                }
                is ConditionView -> {
                    val condition = child.findViewById<EditText>(R.id.etIfValue)?.text?.toString()?.toIntOrNull()
                            ?: 1
                    val containerTrue = child.findViewById<LinearLayout>(R.id.llIfTruePlaceholder)
                    val containerFalse = child.findViewById<LinearLayout>(R.id.llIfFalsePlaceholder)
                    list.add(IfCondition(ColorCondition(condition, Condition.TYPE_TRUE),
                            getAllChildren(containerTrue), //Recursive call
                            getAllChildren(containerFalse))) //Recursive call
                }
                else -> return getAllChildren(child) //Recursive call
            }
        }
        return list
    }

    private fun isSafeToAdd(v: View, target: View): Boolean {
        val viewGroup = v as ViewGroup
        for (i in 0 until viewGroup.childCount) {
            val child = viewGroup.getChildAt(i)

            if (child == target) {
                return false
            }
            when (child) {
                is ActionImageView -> return child != target
                is LoopView -> {
                    val containerll = child.findViewById<LinearLayout>(R.id.llLoopPlaceholder)
                    return isSafeToAdd(containerll, target)
                }
                is ConditionView -> {
                    val containerTrue = child.findViewById<LinearLayout>(R.id.llIfTruePlaceholder)
                    val containerFalse = child.findViewById<LinearLayout>(R.id.llIfFalsePlaceholder)

                    return isSafeToAdd(containerTrue, target) && isSafeToAdd(containerFalse, target)
                }
                else -> return isSafeToAdd(child, target) //Recursive call
            }
        }
        return true
    }
}
