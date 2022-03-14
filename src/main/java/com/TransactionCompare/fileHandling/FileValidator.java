package com.TransactionCompare.fileHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class FileValidator 
{
	public boolean isCsvFileExtension(String fileName) 
	{
        return FilenameUtils.getExtension(fileName).toLowerCase().equals("csv");
    }
	
	public boolean checkIfFilesExtensionAreCsv(String firstFileName, String secondFileName){
        boolean isCsvFirstFile = isCsvFileExtension(firstFileName);
        boolean isCsvSecondFile = isCsvFileExtension(secondFileName);

        return !(isCsvFirstFile == false || isCsvSecondFile == false);
    }
	
	public HashMap<String, String> convertCsvToHashMap(InputStream inputStream) {
        BufferedReader bf = null;
        HashMap<String, String> data = new HashMap<>();
        String line;
        
        try {
            bf = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            
            while ((line = bf.readLine()) != null) 
            {
                List<String> lineList = lineToList(line);

                String transactionId = "";
                
                if (lineList.size() >= 8) 
                {
                	transactionId = lineList.get(5);
                	
                    if (transactionId == null) 
                    {
                        return new HashMap<String, String>();
                    }
                    
                    data.put(transactionId, lineList.get(7));
                } 
                else 
                {
                	data.put(transactionId, null);
                }
            }

        } 
        
        catch (IOException e) 
        {
            throw new RuntimeException("Reading CSV failed.", e);
        } 
        
        catch (UnsupportedOperationException uoe) 
        {
            throw new RuntimeException("Operation not supported.", uoe);   
        } 
        
        finally 
        {
            if (bf != null)
                try 
            {
                bf.close();
            } 
            catch (IOException e) 
            {
                throw new RuntimeException("Closing CSV reader failed.", e);
            }
        }

        return data;
    }
	
	public List<String> lineToList(String line) {
        return Arrays.asList(line.split(","));
    }
}
