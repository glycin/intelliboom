package com.glycin.intelliboom

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class BoomStartupActivity: ProjectActivity {

    override suspend fun execute(project: Project) {
        val application = ApplicationManager.getApplication()
        application.getService(BoomService::class.java).init()
    }
}