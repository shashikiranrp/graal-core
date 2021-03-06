/*
 * Copyright (c) 2015, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.graalvm.compiler.hotspot;

import org.graalvm.compiler.code.CompilationResult;
import org.graalvm.compiler.debug.Debug;
import org.graalvm.compiler.debug.GraalDebugConfig;

import jdk.vm.ci.code.CompiledCode;
import jdk.vm.ci.code.InstalledCode;
import jdk.vm.ci.hotspot.HotSpotCodeCacheProvider;
import jdk.vm.ci.hotspot.HotSpotVMEventListener;

public class HotSpotGraalVMEventListener implements HotSpotVMEventListener {

    private final HotSpotGraalRuntime runtime;

    HotSpotGraalVMEventListener(HotSpotGraalRuntime runtime) {
        this.runtime = runtime;
    }

    @Override
    public void notifyShutdown() {
        runtime.shutdown();
    }

    @Override
    public void notifyInstall(HotSpotCodeCacheProvider codeCache, InstalledCode installedCode, CompiledCode compiledCode) {
        if (Debug.isDumpEnabled(Debug.BASIC_LEVEL)) {
            CompilationResult compResult = Debug.contextLookup(CompilationResult.class);
            assert compResult != null : "can't dump installed code properly without CompilationResult";
            Debug.dump(Debug.BASIC_LEVEL, installedCode, "After code installation");
        }
        if (Debug.isLogEnabled()) {
            Debug.log("%s", codeCache.disassemble(installedCode));
        }
    }

    @Override
    public void notifyBootstrapFinished() {
        runtime.notifyBootstrapFinished();
        if (GraalDebugConfig.Options.ClearMetricsAfterBootstrap.getValue(runtime.getOptions())) {
            runtime.clearMeters();
        }
    }
}
