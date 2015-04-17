package bitman.s117.libwatchcat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Spartan on 2015/4/10.
 */
public class CmdLineInvoker {
    private static final int STDOUT_BUFFER_SIZE = 256;
    private static final int STDERR_BUFFER_SIZE = 256;
    private boolean rootPrivilege;
    private String cmd;
    private String stdout;
    private String stderr;
    private int exitno;


    public CmdLineInvoker(String cmd, boolean rootPrivilege) {
        this.rootPrivilege = rootPrivilege;
        this.cmd = cmd;
        this.exitno = 0;
    }

    public void setCmd(String cmd, boolean rootPrivilege) {
        this.rootPrivilege = rootPrivilege;
        this.cmd = cmd;
        this.exitno = 0;
    }

    public int run() {
        int rtnVal;
        String[] cmdStrings;
        BufferedReader stdoutBuffer = null;
        BufferedReader stderrBuffer = null;

        stdout = "";
        stderr = "";

        if (rootPrivilege) {
            cmdStrings = new String[]{"su", "-c", cmd};
        } else {
            cmdStrings = new String[]{"sh", "-c", cmd};
        }

        try {
            Process process = Runtime.getRuntime().exec(cmdStrings);
            process.waitFor();
            stdoutBuffer = new BufferedReader(new InputStreamReader(process.getInputStream()), STDOUT_BUFFER_SIZE);
            stderrBuffer = new BufferedReader(new InputStreamReader(process.getErrorStream()), STDERR_BUFFER_SIZE);

            String line = null;

            while (null != (line = stdoutBuffer.readLine())) {
                stdout += line + "\n";
            }
            while (null != (line = stderrBuffer.readLine())) {
                stderr += line + "\n";
            }

            exitno = process.exitValue();
            process.destroy();
            rtnVal = 0;
        } catch (Exception e) {
            rtnVal = -1;
        } finally {
            if (stderrBuffer != null) {
                try {
                    stderrBuffer.close();
                } catch (IOException e1) {
                }
            }
            if (stdoutBuffer != null) {
                try {
                    stdoutBuffer.close();
                } catch (IOException e1) {
                }
            }
        }

        return rtnVal;
    }

    public String getStdout() {
        return stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public int getExitno() {
        return exitno;
    }
}
