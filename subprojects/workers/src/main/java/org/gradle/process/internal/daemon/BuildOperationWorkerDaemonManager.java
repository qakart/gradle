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

package org.gradle.process.internal.daemon;

import org.gradle.internal.operations.BuildOperationWorkerRegistry;

import java.io.File;

public class BuildOperationWorkerDaemonManager implements WorkerDaemonFactory {
    private final WorkerDaemonFactory parent;
    private final BuildOperationWorkerRegistry buildOperationWorkerRegistry;

    public BuildOperationWorkerDaemonManager(WorkerDaemonFactory parent, BuildOperationWorkerRegistry buildOperationWorkerRegistry) {
        this.parent = parent;
        this.buildOperationWorkerRegistry = buildOperationWorkerRegistry;
    }

    @Override
    public WorkerDaemon getDaemon(Class<? extends WorkerDaemonProtocol> serverImplementationClass, File workingDir, DaemonForkOptions forkOptions, WorkerDaemonStarter workerDaemonStarter) {
        final WorkerDaemon delegate = parent.getDaemon(serverImplementationClass, workingDir, forkOptions, workerDaemonStarter);
        return new WorkerDaemon() {
            @Override
            public <T extends WorkSpec> WorkerDaemonResult execute(WorkerDaemonAction<T> action, T spec) {
                BuildOperationWorkerRegistry.Completion workerLease = buildOperationWorkerRegistry.getCurrent().operationStart();
                try {
                    return delegate.execute(action, spec);
                } finally {
                    workerLease.operationFinish();
                }
            }
        };
    }
}
