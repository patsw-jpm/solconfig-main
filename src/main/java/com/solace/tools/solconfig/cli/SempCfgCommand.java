package com.solace.tools.solconfig.cli;

import com.solace.tools.solconfig.Commander;
import com.solace.tools.solconfig.SempClient;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import com.solace.tools.solconfig.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.Callable;

@Command(name = "solconfig", mixinStandardHelpOptions = true,
        versionProvider = SempCfgCommand.BuildVersion.class,
        description = "Backing Up and Restoring Solace PubSub+ Broker Configuration with SEMPv2 protocol. " +
                "Use the 'backup' command to export the configuration of objects on a PS+  Broker into a single JSON, " +
                "then use the 'create' or 'update' command to restore the configuration.",
        subcommands = {
            BackupCommand.class,
            DeleteCommand.class,
            CreateCommand.class,
            UpdateCommand.class,
            PrintSpecCommand.class,
            TestCommand.class,
            CommandLine.HelpCommand.class
        },
        showDefaultValues = true)
public class SempCfgCommand implements Callable<Integer> {

    @Option(names = {"-H", "--host"}, description = "URL to access the management endpoint of the broker")
    private String adminHost = "http://localhost:8080";

    @Option(names = {"-u", "--admin-user"}, description = "The username of the management user")
    private String adminUser = "admin";

    @Option(names = {"-p", "--admin-password"}, description = "The password of the management user")
    private String adminPwd = "admin";

    @Option(names = {"-k", "--insecure"}, description = "Allow insecure server connections when using SSL")
    private boolean insecure = false;

    @Option(names = "--cacert", description = "CA certificate file to verify peer against when using SSL")
    private Path cacert;

    @Option(names = "--curl-only", description = "Print curl commands only, no effect on 'backup' command")
    private boolean curlOnly = false;

    @Option(names = "--use-template", description = "Whether to support templating")
    private boolean useTemplate = false;

    @CommandLine.Spec  CommandLine.Model.CommandSpec spec;
    @Override
    public Integer call() {
        throw new CommandLine.ParameterException(spec.commandLine(), "Missing required subcommand!");
    }

    protected Commander commander;
    protected void init(){
        Optional.ofNullable(cacert).ifPresent(path -> {
            if (!Files.isReadable(path)) {
                Utils.errPrintlnAndExit("Path %s doesn't exist or is un-readable!", path.toAbsolutePath());
            }
        });

        if (adminHost.endsWith("/")){
            adminHost = adminHost.substring(0, adminHost.length()-1);
        }

        commander = Commander.ofSempClient(new SempClient(adminHost, adminUser, adminPwd, insecure, cacert));
        commander.setCurlOnly(curlOnly);
        commander.setUseTemplate(useTemplate);
    }

    @Override
    public String toString() {
        return "SempCfgCommand{" +
                "adminHost='" + adminHost + '\'' +
                ", adminUser='" + adminUser + '\'' +
                ", adminPwd='" + adminPwd + '\'' +
                ", curlOnly=" + curlOnly +
                '}';
    }

    public static class BuildVersion implements CommandLine.IVersionProvider {
        @Override
        public String[] getVersion() {
            var pkg = BuildVersion.class.getPackage();
            return new String[]{pkg.getImplementationTitle() + ": " + pkg.getImplementationVersion(),
                    "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
                    "OS: ${os.name} ${os.version} ${os.arch}"};
        }
    }
}
