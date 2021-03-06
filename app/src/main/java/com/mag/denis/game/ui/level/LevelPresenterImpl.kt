package com.mag.denis.game.ui.level

import com.mag.denis.game.R
import com.mag.denis.game.manager.GameManager
import com.mag.denis.game.manager.GameManager.Companion.STAGE_BLOCK
import com.mag.denis.game.manager.GameManager.Companion.STAGE_FLOW
import com.mag.denis.game.manager.GameManager.Companion.STAGE_PSEUDO
import com.mag.denis.game.manager.LevelManager
import com.mag.denis.game.ui.ActivityScope
import javax.inject.Inject

@ActivityScope
class LevelPresenterImpl @Inject constructor(private val view: LevelView, private val gameManager: GameManager,
        private val levelManager: LevelManager) : LevelPresenter {

    private var currentStage = 0
    private var maxStage = 0
    private lateinit var stages: ArrayList<String>

    override fun onCreate() {
        stages = gameManager.getStages()

        if (gameManager.getCurrentStage() != stages[currentStage]) {
            currentStage = stages.indexOf(gameManager.getCurrentStage())
            gameManager.setCurrentStage(stages[currentStage])
        }

        view.setStageTitle(getStageTitle())
        maxStage = stages.size - 1
        checkButtons()
        setupLevels()
    }

    override fun onResume() {
        view.startMusicService()
    }

    override fun onPause() {
        view.stopMusicService()
    }

    override fun onLevelClicked(level: Int) {
        gameManager.setCurrentLevel(level)
        view.openGameView()
    }

    override fun onBackClicked() {
        view.closeView()
    }

    override fun onNextClick() {
        if (currentStage < maxStage) {
            currentStage++
        }
        gameManager.setCurrentStage(stages[currentStage])
        checkButtons()
        view.setStageTitle(getStageTitle())
        setupLevels()
        view.animateLevels()
    }

    override fun onPrevClick() {
        if (currentStage > 0) {
            currentStage--
        }
        gameManager.setCurrentStage(stages[currentStage])
        checkButtons()
        view.setStageTitle(getStageTitle())
        setupLevels()
        view.animateLevels()
    }

    override fun onLevelComplete() {
        val currentLevel = gameManager.getCurrentLevel()
        if (currentLevel < 8) {
            gameManager.setMaxLevelAchieved(currentLevel + 1)
            setupLevels()
        }
    }

    private fun checkButtons() {
        view.enablePrev(currentStage > 0)
        view.enableNext(currentStage < maxStage)
    }

    private fun setupLevels() {
        view.clearLevels()
        for (i in 1..levelManager.numOfLevelsForStage()) {
            view.setupLevel(i, i <= gameManager.getMaxLevelAchieved(), levelManager.getStarsForLevel(i),
                    i == gameManager.getMaxLevelAchieved())
        }
    }

    private fun getStageTitle(): Int {
        return when (stages[currentStage]) {
            STAGE_BLOCK -> R.string.map_stage_title_blocks
            STAGE_FLOW -> R.string.map_stage_title_flow
            STAGE_PSEUDO -> R.string.map_stage_title_pseudo
            else -> throw IllegalStateException("Stage dont exists")
        }
    }

    override fun onDestroy() {
        levelManager.saveScores()
    }
}
