/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.solace.tools.solconfig;

import picocli.CommandLine;
import com.solace.tools.solconfig.cli.SempCfgCommand;

public class App {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new SempCfgCommand()).execute(args);
        System.exit(exitCode);
    }
}
