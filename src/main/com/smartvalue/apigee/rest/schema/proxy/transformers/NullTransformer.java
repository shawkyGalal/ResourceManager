package com.smartvalue.apigee.rest.schema.proxy.transformers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NullTransformer implements ApigeeObjectTransformer {

	@Override
	public void trasform(String bundleZipFileName , String newBundlePath) {
		String  fileName  = new File (bundleZipFileName).getName() ; 
        Path sourcePath = Path.of(bundleZipFileName);
        Path destinationPath = Path.of(newBundlePath + File.separatorChar + fileName);
        try {
            // Copy file from source to destination
        	// Create directories if they don't exist
            Files.createDirectories(destinationPath.getParent());
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("File copied successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	@Override
	public boolean filter(String bundleZipFileNam) {
		// TODO Auto-generated method stub
		return true;
	}

}
