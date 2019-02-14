package com.wrbug.jsposedgradleplugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.api.ApplicationVariantImpl
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class JsPosedPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.
                println "this is JsPosedPlugin!!"
        StringBuilder builder = new StringBuilder()
        project.afterEvaluate {
            Project appProject
            Set<Project> libs = project.rootProject.subprojects
            libs.each { p ->
                if (p.plugins.hasPlugin(AppPlugin)) {
                    appProject = p
                }
                builder.append(p.name.replace("-", "_").toLowerCase()).append(",")

            }
            if (appProject == null) {
                return
            }
            builder.deleteCharAt(builder.length() - 1)
            println(appProject.getBuildDir())
            println builder
            ((BaseAppModuleExtension) appProject.android).applicationVariants.all { ApplicationVariantImpl variant ->
                variant.buildConfigField("String", "MODULES_NAME", "\"$builder\"")
            }
        }
    }
}