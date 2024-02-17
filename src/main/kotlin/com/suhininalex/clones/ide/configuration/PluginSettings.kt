package com.suhininalex.clones.ide.configuration

import com.suhininalex.clones.core.utils.BooleanProperty
import com.suhininalex.clones.core.utils.IdeaSettings
import com.suhininalex.clones.core.utils.IntProperty

object PluginSettings: IdeaSettings(nameSpace = "clone_finder") {

    private object Defaults {
        val minCloneLength = 40
        val coverageSkipFilter = 70
        val enabledForProject = true
        val disableTestFolder = true
        val kotlinSearchEnabled = true
        val javaSearchEnabled = true
        val maxMemory = 350
        val enableGaps = false
        val minFragment = 10
    }

    val minFragment = Defaults.minFragment

    var enableGaps by BooleanProperty(Defaults.enableGaps)

    var maxMemory by IntProperty(Defaults.maxMemory)

    var minCloneLength by IntProperty(Defaults.minCloneLength)

    var coverageSkipFilter by IntProperty(Defaults.coverageSkipFilter)

    var enabledForProject by BooleanProperty(Defaults.enabledForProject, projectScope = true)

    var disableTestFolder by BooleanProperty(Defaults.disableTestFolder)

    var kotlinSearchEnabled by BooleanProperty(Defaults.kotlinSearchEnabled)

    var javaSearchEnabled by BooleanProperty(Defaults.javaSearchEnabled)

    fun reset(){
        minCloneLength = Defaults.minCloneLength
        coverageSkipFilter = Defaults.coverageSkipFilter
        enabledForProject = Defaults.enabledForProject
        disableTestFolder = Defaults.disableTestFolder
    }
}

