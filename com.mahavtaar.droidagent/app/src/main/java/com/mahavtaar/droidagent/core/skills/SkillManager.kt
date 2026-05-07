package com.mahavtaar.droidagent.core.skills
import javax.inject.Inject
import javax.inject.Singleton
@Singleton
open class SkillManager @Inject constructor() {
    open fun getActiveSkillsPrompt() = "No active skills."
}
