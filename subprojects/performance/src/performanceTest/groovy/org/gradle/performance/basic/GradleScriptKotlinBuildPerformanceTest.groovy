/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.performance.basic

import org.gradle.performance.AbstractCrossVersionPerformanceTest
import spock.lang.Unroll

class GradleScriptKotlinBuildPerformanceTest extends AbstractCrossVersionPerformanceTest {

    @Unroll("#testId")
    def "configure project"() {
        given:
        runner.testId = testId
        runner.testProject = testProject
        runner.tasksToRun = ['help']
        runner.targetVersions = ['3.3-20161126000026+0000']
        runner.args = runnerArgs
        runner.gradleOpts = ["-Xms512m", "-Xmx512m"]

        when:
        def result = runner.run()

        then:
        result.assertCurrentVersionHasNotRegressed()

        where:
        testProject       | runnerArgs
        "ktsSmall"        | []
        "ktsSmall"        | ['--recompile-scripts']
        "ktsManyProjects" | []
        "ktsManyProjects" | ['--recompile-scripts']
        testIdSuffix = runnerArgs.empty ? '' : " (${runnerArgs.join(', ')})"
        testId = "configuration of $testProject$testIdSuffix"
    }
}
