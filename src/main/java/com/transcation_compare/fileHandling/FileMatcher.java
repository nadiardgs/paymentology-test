package com.transcation_compare.fileHandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.transcation_compare.model.FileData;
import com.transcation_compare.model.UnmatchedFileData;
import com.transcation_compare.model.UnmatchedFileData;

public class FileMatcher 
{
	public FileValidator _fileValidator = new FileValidator();
	
	public FileData matchHashMapsAsFileData(HashMap<String, String> firstFile, List<String> firstFileList, HashMap<String, String> secondFile)
    {
        FileData fileData;
        
        Integer matchCount = 0;
        
        Integer unmatchCount = 0;
        
        UnmatchedFileData unmatchedFileData;
        
        List<UnmatchedFileData> lstUnmatchedFileData = new ArrayList<UnmatchedFileData>();
        
        for (Map.Entry<String, String> set : firstFile.entrySet())
        {
            if (secondFile.entrySet().contains(set))
            {
                matchCount++;
            }
            else
            {
                if (secondFile.keySet().contains(set.getKey()))
                {
                    matchCount++;
                }
                else
                {
                	unmatchedFileData = findUnmatchedFileDataByKey(firstFileList, set.getKey());
                	lstUnmatchedFileData.add(unmatchedFileData);
                	unmatchCount++;
                }
            }
        }
        
        fileData = new FileData();
        
        fileData.setFileTotalMatchingRecords(matchCount);
        
        fileData.setFileTotalUnmatchingRecords(unmatchCount);
        
        fileData.setUnmatchedFileDatas(lstUnmatchedFileData);
        
        fileData.setFileTotalRecords(firstFileList.size());
        
        return fileData;
    }
	
	public UnmatchedFileData findUnmatchedFileDataByKey(List<String> fileAsList, String key)
	{	
		_fileValidator = new FileValidator();
		
		UnmatchedFileData unmatchedFileData;
		
		List<String> fileLine = new ArrayList<String>();
		
		for (String line : fileAsList)
		{
			fileLine = _fileValidator.getLineAsList(line);
			
			if (fileLine.get(5).equals(key))
			{
				unmatchedFileData  = new UnmatchedFileData();
				
				unmatchedFileData.setDate(fileLine.get(1));
				unmatchedFileData.setAmount(fileLine.get(2));
				unmatchedFileData.setDescription(fileLine.get(3));
				
				return unmatchedFileData;
			}
		}
		
		return new UnmatchedFileData();
	}
	
	/*
	 * Accept the list with the file data and the list of positions of unmatched transactions
	 * And return a list of unmatched reports
	 * */
	public List<UnmatchedFileData> getUnmatchedFileDataList(List<String> fileAsList, List<Integer> unmatchedPositions)
	{
		
		UnmatchedFileData UnmatchedFileData;
		
		List<UnmatchedFileData> UnmatchedFileDataList = new ArrayList<UnmatchedFileData>();
		
		for (Integer unmatchedPosition : unmatchedPositions)
		{
			String line = fileAsList.get(unmatchedPosition);
			
			List<String> lineList = _fileValidator.getLineAsList(line);
			
			UnmatchedFileData = new UnmatchedFileData();
			
			UnmatchedFileData.setDate(lineList.get(1));
    		UnmatchedFileData.setAmount(lineList.get(2));
    		UnmatchedFileData.setDescription(lineList.get(3));
    		
    		UnmatchedFileDataList.add(UnmatchedFileData);
		}
		
		return UnmatchedFileDataList;
	}
	
	
	public List<UnmatchedFileData> getUnmatchedFileData(List<Integer> keys, String fileName)
	{
		_fileValidator = new FileValidator();
		
		UnmatchedFileData UnmatchedFileData;
		
		List<UnmatchedFileData> UnmatchedFileDataList = new ArrayList<UnmatchedFileData>();
		
		for (Integer key : keys)
		{
			try
			{
				String line = Files.readAllLines(Paths.get(fileName)).get(key);
				
				List<String>lineList = _fileValidator.getLineAsList(line);
				
				UnmatchedFileData = new UnmatchedFileData();
				
				UnmatchedFileData.setDate(lineList.get(1));
        		UnmatchedFileData.setAmount(lineList.get(2));
        		UnmatchedFileData.setDescription(lineList.get(3));
        		
        		UnmatchedFileDataList.add(UnmatchedFileData);
			} 
			
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return UnmatchedFileDataList;
	}
	
	//for each unmatched entry, I must find its date, amount and description
	public UnmatchedFileData getUnmatchedFileData(String key, InputStream fileIS)
	{
		BufferedReader bf = null;
		String line;
		UnmatchedFileData UnmatchedFileData;
		
		_fileValidator = new FileValidator();
		
		try
		{
			bf = new BufferedReader(new InputStreamReader(fileIS, StandardCharsets.UTF_8));
            
            while ((line = bf.readLine()) != null) 
            {
            	List<String> lineList = _fileValidator.getLineAsList(line);
            	
            	if (lineList.get(5).toLowerCase().equals(key.toLowerCase()))
            	{
            		UnmatchedFileData = new UnmatchedFileData();
            		
            		UnmatchedFileData.setDate(lineList.get(1));
            		UnmatchedFileData.setAmount(lineList.get(2));
            		UnmatchedFileData.setDescription(lineList.get(3));
            		
            		return UnmatchedFileData;
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
		
		return new UnmatchedFileData();
	}
}
