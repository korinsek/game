package com.mag.denis.game.ui

import com.mag.denis.game.ui.finish.FinishActivity
import com.mag.denis.game.ui.finish.FinishModule
import com.mag.denis.game.ui.main.MainActivity
import com.mag.denis.game.ui.main.MainModule
import com.mag.denis.game.ui.map.MapActivity
import com.mag.denis.game.ui.map.MapModule
import com.mag.denis.game.ui.menu.MenuActivity
import com.mag.denis.game.ui.menu.MenuModule
import com.mag.denis.game.ui.score.ScoreActivity
import com.mag.denis.game.ui.score.ScoreModule
import com.mag.denis.game.ui.settings.SettingsActivity
import com.mag.denis.game.ui.settings.SettingsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class InjectorModule {

    @ActivityScope @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributeMainActivityInjector(): MainActivity

    @ActivityScope @ContributesAndroidInjector(modules = [SettingsModule::class])
    abstract fun contributeSettingsActivityInjector(): SettingsActivity

    @ActivityScope @ContributesAndroidInjector(modules = [MapModule::class])
    abstract fun contributeMapActivityInjector(): MapActivity

    @ActivityScope @ContributesAndroidInjector(modules = [MenuModule::class])
    abstract fun contributeMenuActivityInjector(): MenuActivity

    @ActivityScope @ContributesAndroidInjector(modules = [ScoreModule::class])
    abstract fun contributeScoreActivityInjector(): ScoreActivity

    @ActivityScope @ContributesAndroidInjector(modules = [FinishModule::class])
    abstract fun contributeFinishActivityInjector(): FinishActivity
}