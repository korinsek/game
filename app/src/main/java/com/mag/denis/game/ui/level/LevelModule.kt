package com.mag.denis.game.ui.level

import dagger.Binds
import dagger.Module

@Module
interface LevelModule {

    @Binds fun bindView(activity: LevelActivity): LevelView

    @Binds fun bindPresenter(presenterImpl: LevelPresenterImpl): LevelPresenter
}
