/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.stdlib.websocket.compiler;

import io.ballerina.projects.DiagnosticResult;
import io.ballerina.projects.Package;
import io.ballerina.projects.PackageCompilation;
import io.ballerina.projects.ProjectEnvironmentBuilder;
import io.ballerina.projects.directory.BuildProject;
import io.ballerina.projects.environment.Environment;
import io.ballerina.projects.environment.EnvironmentBuilder;
import io.ballerina.stdlib.websocket.plugin.WebSocketServiceValidator;
import io.ballerina.stdlib.websocket.plugin.WebSocketUpgradeServiceValidator;
import io.ballerina.tools.diagnostics.Diagnostic;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is the compiler plugin for Ballerina WebSocket package.
 */
public class UpgradeServiceValidationTest {
    private static final Path RESOURCE_DIRECTORY = Paths.get("src", "test", "resources", "ballerina_sources")
            .toAbsolutePath();
    private static final PrintStream OUT = System.out;
    private static final Path DISTRIBUTION_PATH = Paths.get("build", "target", "ballerina-distribution")
            .toAbsolutePath();

    @Test
    public void testNumberOfResource() {
        Package currentPackage = loadPackage("sample_package_1");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().code(), "WS_101");
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.INVALID_RESOURCE_ERROR);
    }

    @Test
    public void testParametersOfResource() {
        Package currentPackage = loadPackage("sample_package_2");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testNoParametersOfResource() {
        Package currentPackage = loadPackage("sample_package_3");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testMoreThanOneParametersOfResource() {
        Package currentPackage = loadPackage("sample_package_4");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().code(), "WS_101");
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.MORE_THAN_ONE_RESOURCE_PARAM_ERROR);
    }

    @Test
    public void testWrongParametersOfResource() {
        Package currentPackage = loadPackage("sample_package_5");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().code(), "WS_101");
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.INVALID_RESOURCE_PARAMETER_ERROR);
    }

    @Test
    public void testHttpListenerAsWsListener() {
        Package currentPackage = loadPackage("sample_package_6");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testWrongMethodOfResource() {
        Package currentPackage = loadPackage("sample_package_7");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().code(), "WS_101");
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.FUNCTION_NOT_ACCEPTED_BY_THE_SERVICE);
    }

    @Test
    public void testHavingRemoteFunctionInsteadOfResource() {
        Package currentPackage = loadPackage("sample_package_8");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().code(), "WS_101");
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.FUNCTION_NOT_ACCEPTED_BY_THE_SERVICE);
    }

    @Test
    public void testCorrectReturnTypes() {
        Package currentPackage = loadPackage("sample_package_9");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testInvalidReturnTypes() {
        Package currentPackage = loadPackage("sample_package_10");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().code(), "WS_101");
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.INVALID_RETURN_TYPES_IN_RESOURCE);
    }

    @Test
    public void testWithAliasModuleNamePrefixErrorScenario() {
        Package currentPackage = loadPackage("sample_package_11");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketUpgradeServiceValidator.INVALID_RETURN_TYPES_IN_RESOURCE);
    }

    @Test
    public void testWithAliasModuleNamePrefix() {
        Package currentPackage = loadPackage("sample_package_12");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testOnOpenWithCaller() {
        Package currentPackage = loadPackage("sample_package_13");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testOnOpenWithoutCaller() {
        Package currentPackage = loadPackage("sample_package_14");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testOnOpenWithInvalidParameter() {
        Package currentPackage = loadPackage("sample_package_15");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 3);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Diagnostic diagnostic2 = (Diagnostic) diagnosticResult.diagnostics().toArray()[1];
        Diagnostic diagnostic3 = (Diagnostic) diagnosticResult.diagnostics().toArray()[2];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_PARAM_FOR_ONOPEN);
        Assert.assertEquals(diagnostic2.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_PARAM_FOR_ONCLOSE);
        Assert.assertEquals(diagnostic3.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_RETURN_TYPES);
    }

    @Test
    public void testOnOpenWithMoreThanOneParameter() {
        Package currentPackage = loadPackage("sample_package_16");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_PARAMS_FOR_ONOPEN);
    }

    @Test
    public void testOnOpenWithValidReturnTypes() {
        Package currentPackage = loadPackage("sample_package_17");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 0);
    }

    @Test
    public void testOnOpenWithInValidReturnTypes() {
        Package currentPackage = loadPackage("sample_package_18");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Diagnostic diagnostic2 = (Diagnostic) diagnosticResult.diagnostics().toArray()[1];
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 2);
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_RETURN_TYPES);
        Assert.assertEquals(diagnostic2.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_PARAMS_FOR_ONCLOSE);
    }

    @Test
    public void testOnOpenWithGenericErrorReturnTypes() {
        Package currentPackage = loadPackage("sample_package_19");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_RETURN_TYPES);
    }

    @Test
    public void testOnErrorWithSpecificErrorInputTypes() {
        Package currentPackage = loadPackage("sample_package_20");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_RETURN_TYPES);
    }

    @Test
    public void testOnErrorWithInvalidOneInputTypes() {
        Package currentPackage = loadPackage("sample_package_21");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 2);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Diagnostic diagnostic2 = (Diagnostic) diagnosticResult.diagnostics().toArray()[1];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_FOR_ONERROR_WITH_ONE_PARAMS);
        Assert.assertEquals(diagnostic2.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_PARAM_FOR_ONIDLETIMEOUT);

    }

    @Test
    public void testOnErrorWithInvalidInputTypes() {
        Package currentPackage = loadPackage("sample_package_22");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 2);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Diagnostic diagnostic2 = (Diagnostic) diagnosticResult.diagnostics().toArray()[1];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_FOR_ONERROR);
        Assert.assertEquals(diagnostic2.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_PARAMS_FOR_ONIDLETIMEOUT);

    }

    @Test
    public void testOnTextWithInvalidMandatoryInputParamAndWrongReturn() {
        Package currentPackage = loadPackage("sample_package_23");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 2);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Diagnostic diagnostic2 = (Diagnostic) diagnosticResult.diagnostics().toArray()[1];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_FOR_ON_TEXT_WITH_ONE_PARAMS);
        Assert.assertEquals(diagnostic2.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_RETURN_TYPES_ON_DATA);

    }

    @Test
    public void testOnTextWithInvalidInputParams() {
        Package currentPackage = loadPackage("sample_package_24");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_FOR_ON_TEXT);

    }

    @Test
    public void testOnBinaryWithInvalidMandatoryInputParams() {
        Package currentPackage = loadPackage("sample_package_25");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 1);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_FOR_ON_BINARY_WITH_ONE_PARAMS);

    }

    @Test
    public void testOnBinaryWithInvalidReturnTypes() {
        Package currentPackage = loadPackage("sample_package_26");
        PackageCompilation compilation = currentPackage.getCompilation();

        DiagnosticResult diagnosticResult = compilation.diagnosticResult();
        Assert.assertEquals(diagnosticResult.diagnostics().size(), 2);
        Diagnostic diagnostic1 = (Diagnostic) diagnosticResult.diagnostics().toArray()[0];
        Assert.assertEquals(diagnostic1.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_INPUT_FOR_ON_BINARY);
        Diagnostic diagnostic2 = (Diagnostic) diagnosticResult.diagnostics().toArray()[1];
        Assert.assertEquals(diagnostic2.diagnosticInfo().messageFormat(),
                WebSocketServiceValidator.INVALID_RETURN_TYPES_ON_DATA);

    }

    private Package loadPackage(String path) {
        Path projectDirPath = RESOURCE_DIRECTORY.resolve(path);
        BuildProject project = BuildProject.load(getEnvironmentBuilder(), projectDirPath);
        return project.currentPackage();
    }

    private static ProjectEnvironmentBuilder getEnvironmentBuilder() {
        Environment environment = EnvironmentBuilder.getBuilder().setBallerinaHome(DISTRIBUTION_PATH).build();
        return ProjectEnvironmentBuilder.getBuilder(environment);
    }
}
