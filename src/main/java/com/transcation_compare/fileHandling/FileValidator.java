package com.transcation_compare.fileHandling;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

public class FileValidator 
{
	
	public boolean isCsvFileExtension(String fileName) 
	{
        return FilenameUtils.getExtension(fileName).toLowerCase().equals("csv");
    }
	
	public boolean checkIfFilesExtensionAreCsv(String firstFileName, String secondFileName)
	{
        boolean isCsvFirstFile = isCsvFileExtension(firstFileName);
        boolean isCsvSecondFile = isCsvFileExtension(secondFileName);

        return !(isCsvFirstFile == false || isCsvSecondFile == false);
    }
	
	public List<String> getInputStreamAsList(InputStream fileInputStream)
	{
		BufferedReader bf = new BufferedReader(new InputStreamReader(fileInputStream, StandardCharsets.UTF_8));
		Stream<String> linesStream = bf.lines();
		return linesStream.collect(Collectors.toList());
	}
	
	/*
	 * This method returns TransactionID and WalletReference as a HashMap where the first is the key and the second is the value.
	 * TransactionID can't be null, but WalletReference can. In this case, the value is passed as null
	 * */
	public HashMap<String, String> getListAsHashMap(List<String> lines)
	{
		HashMap<String, String> data = new HashMap<>();
		
		String transactionId = "";
		
		for (String line : lines)
		{
			List<String> lineList = getLineAsList(line);
			
			if (lineList.size() >= 8) 
            {
            	transactionId = lineList.get(5);
            	
                if (transactionId == null) 
                {
                    return new HashMap<String, String>();
                }
                
                data.put(transactionId, lineList.get(7));
            } 
            else if (!transactionId.isEmpty())
            {
            	data.put(transactionId, null);
            }
            else
            {
            	return new HashMap<String, String>();
            }
        }
		
		return data;
	}
	
	public List<String> getLineAsList(String line) 
	{
        return Arrays.asList(line.split(","));
    }
}
