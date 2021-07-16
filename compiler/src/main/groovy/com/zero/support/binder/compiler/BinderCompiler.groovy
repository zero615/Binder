package com.zero.support.binder.compiler


import com.android.build.gradle.tasks.GenerateBuildConfig
import com.google.auto.service.AutoService
import com.zero.support.binder.compiler.RuntimeGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

import javax.annotation.Nonnull

@AutoService
class BinderCompiler implements Plugin<Project> {


    @Override
    void apply(@Nonnull Project project) {
        project.tasks.whenTaskAdded {
            task ->
                Task t = task
                if (t.name.endsWith("generateDebugBuildConfig") || t.name.endsWith("generateReleaseBuildConfig")) {
                    println(t)
                    t.doLast {
                        it.outputs.files.each { File file ->
                            GenerateBuildConfig config = t;
                            RuntimeGenerator generator = new RuntimeGenerator();
                            generator.generateDefault(config.buildConfigPackageName.get(), file)
                        }
                    }
                }

        }
    }
}