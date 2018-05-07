package com.mag.denis.game.ui.main.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.mag.denis.game.ui.main.objects.FloorSet

class GameView(context: Context, attributes: AttributeSet) : SurfaceView(context, attributes), SurfaceHolder.Callback {

    private val gameThread: GameThread
    private var paint: Paint

    private var floorGameObjects: FloorSet? = null

    init {
        holder.addCallback(this)
        gameThread = GameThread(holder, this)

        paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.RED
    }

    override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
        //Nothing to do here
    }

    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        // Game objects
        floorGameObjects = FloorSet(resources)

        // Start the game thread
        if(!gameThread.isAlive){
            gameThread.setRunning(true)
            gameThread.start()
        }
    }

    fun update() {
        floorGameObjects?.update()
    }


    fun render(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        floorGameObjects?.draw(canvas, paint)
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        var retry = true
        while (retry) {
            try {
                gameThread.setRunning(false)
                gameThread.join()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            retry = false
        }
    }
}