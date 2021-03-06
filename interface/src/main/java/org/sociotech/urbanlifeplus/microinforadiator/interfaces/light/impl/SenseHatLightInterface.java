/*
 * Copyright (c) 2017. Vitus Lehner. UrbanLife+. Universität der Bundeswehr München.
 */

package org.sociotech.urbanlifeplus.microinforadiator.interfaces.light.impl;

import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author vituslehner 03.07.17
 */
@Component
public class SenseHatLightInterface extends AbstractLightInterface {

    private static final String NAME = "Raspberry Pi Sense HAT Interface";

    private Process pythonProcess = null;
    private final String scriptPath;

    public SenseHatLightInterface() {
        super();

        scriptPath = copyScriptFile();
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected void update() {
        logger.debug("SenseHAT: Received light command: {}", getPhases());

        disposeProcess();

        String[] args = buildArgs();
        startProcess(args);
    }

    private String[] buildArgs() {
        List<String> args = new ArrayList<>();
        args.add("python");
        args.add(scriptPath);
        getPhases().forEach(phase -> {
            args.add(phase.getLightColor().toString());
            args.add(phase.getLightSymbol().toString());
        });
        return args.toArray(new String[args.size()]);
    }

    private void startProcess(String[] args) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args)
                    .inheritIO();
            logger.info("Executing command: {}", processBuilder.command());
            pythonProcess = processBuilder.start();
        } catch (IOException e) {
            logger.error("Could not start python process for script: {}", scriptPath, e);
            throw new IllegalStateException("Could not start python script.", e);
        }
    }

    @PreDestroy
    private void disposeProcess() {
        if (pythonProcess != null) {
            pythonProcess.destroyForcibly();
            pythonProcess = null;
        }
    }

    private String copyScriptFile() {
        try {
            InputStream original = this.getClass().getResourceAsStream("/scripts/rpi-sensehat.py");
            File tmpScript = File.createTempFile("rpi-sensehat", ".py");
            tmpScript.deleteOnExit();

            FileCopyUtils.copy(original, new FileOutputStream(tmpScript));
            boolean isExecutable = tmpScript.setExecutable(true);
            logger.info("Copied RPi SenseHAT Python Script to {} and made executable: {}", tmpScript.getAbsolutePath(), isExecutable);
            return tmpScript.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Could not create temporary python script file.");
            throw new IllegalStateException("Could not copy rpi sensehat python script.");
        }
    }
}
