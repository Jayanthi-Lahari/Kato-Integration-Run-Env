package com.bean;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;

import com.exception.TriggerFileNotFoundException;


public class FileValidatorBean {


    public List<File> scanAndValidate(Exchange exchange) throws TriggerFileNotFoundException {

    	String inputDir =
    	        exchange.getIn().getHeader("inputDir", String.class);
    	
        File folder = new File(inputDir);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new TriggerFileNotFoundException(
                    "Input directory does not exist.");
        }

        File[] xmlFiles = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".xml");
            }
        });

        List<File> validFiles = new ArrayList<>();

        if (xmlFiles != null) {

            for (File xml : xmlFiles) {

                String trgName = xml.getName()
                        .replaceFirst("\\.xml$", ".trg");

                File trg = new File(folder, trgName);

                if (trg.exists()) {
                    validFiles.add(xml);
                }
            }
        }

        if (validFiles.isEmpty()) {
            throw new TriggerFileNotFoundException(
                    "No XML files have matching TRG files in current poll.");
        }

        return validFiles;
    }
}