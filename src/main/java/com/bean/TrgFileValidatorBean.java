package com.bean;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import com.exception.TriggerFileNotFoundException;

@Component("trgFileValidatorBean")
public class TrgFileValidatorBean {

    public void trgExistFileDoesnotexist(Exchange exchange) throws Exception {

        String fileName = exchange.getIn()
                                  .getHeader("CamelFileNameOnly", String.class);

        String folder = exchange.getIn()
                                .getHeader("CamelFileParent", String.class);

        String sourceFilePath = exchange.getIn()
                                        .getHeader("CamelFileAbsolutePath", String.class);

        Path sourceEndPoint = Paths.get(sourceFilePath);

        String baseName =
                fileName.substring(0, fileName.lastIndexOf('.'));

        Path trgFile = Paths.get(folder, baseName + ".trg");

        System.out.println("Checking XML : " + sourceEndPoint);
        System.out.println("Checking TRG : " + trgFile);

        if (!Files.exists(trgFile)) {

            throw new TriggerFileNotFoundException(
                    "Trigger file not found for XML: "
                            + fileName
                            + ". Expected: "
                            + trgFile);
        }

        if (!Files.exists(sourceEndPoint)) {

            throw new TriggerFileNotFoundException(
                    "XML file does not exist but trigger file exists: "
                            + trgFile);
        }

        System.out.println("TRG file found: " + trgFile);
    }
}