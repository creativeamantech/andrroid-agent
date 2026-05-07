package com.mahavtaar.droidagent.core.tools

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Tool(val name: String, val description: String, val requiresPermission: String = "")
