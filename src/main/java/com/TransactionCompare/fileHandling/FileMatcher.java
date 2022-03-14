package com.TransactionCompare.fileHandling;

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

import com.TransactionCompare.model.UnmatchedReport;

public class FileMatcher 
{
	public FileValidator _fileValidator = new FileValidator();
	
	public List<String> matchHashMaps(HashMap<String, String> firstFile, HashMap<String, String> secondFile, InputStream fileIS)
    {
        List<String> listMatchAndUnmatch = new ArrayList<>();
        Integer matchCount = 0;
        Integer unmatchCount = 0;
        Integer count = 0;
        List<Integer> lstUnmatchedPositions = new ArrayList<Integer>();
        
        for (Map.Entry<String, String> set : firstFile.entrySet())
        {
        	count++;
        	
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
                    unmatchCount++;
                    
                    //for each unmatched element, get its position in the file
                    lstUnmatchedPositions.add(count);
                }
            }
        }
        
        listMatchAndUnmatch.add(String.valueOf(firstFile.size()));
        listMatchAndUnmatch.add(matchCount.toString());
        listMatchAndUnmatch.add(unmatchCount.toString());
        
        String numberString = lstUnmatchedPositions.stream().map(String::valueOf)
        	    .collect(Collectors.joining(";"));
        
        listMatchAndUnmatch.add(numberString);
        
        return listMatchAndUnmatch;
    }
	
	
	public List<UnmatchedReport> getUnmatchedReport(List<Integer> keys, String fileName)
	{
		_fileValidator = new FileValidator();
		
		UnmatchedReport unmatchedReport;
		
		List<UnmatchedReport> unmatchedReportList = new ArrayList<UnmatchedReport>();
		
		for (Integer key : keys)
		{
			try
			{
				String line = Files.readAllLines(Paths.get(fileName)).get(key);
				
				List<String>lineList = _fileValidator.lineToList(line);
				
				unmatchedReport = new UnmatchedReport();
				
				unmatchedReport.setDate(lineList.get(1));
        		unmatchedReport.setAmount(lineList.get(2));
        		unmatchedReport.setDescription(lineList.get(3));
        		
        		unmatchedReportList.add(unmatchedReport);
			} 
			
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return unmatchedReportList;
	}
	
	//for each unmatched entry, I must find its date, amount and description
	public UnmatchedReport getUnmatchedReport(String key, InputStream fileIS)
	{
		BufferedReader bf = null;
		String line;
		UnmatchedReport unmatchedReport;
		
		_fileValidator = new FileValidator();
		
		try
		{
			bf = new BufferedReader(new InputStreamReader(fileIS, StandardCharsets.UTF_8));
            
            while ((line = bf.readLine()) != null) 
            {
            	List<String> lineList = _fileValidator.lineToList(line);
            	
            	if (lineList.get(5).toLowerCase().equals(key.toLowerCase()))
            	{
            		unmatchedReport = new UnmatchedReport();
            		
            		unmatchedReport.setDate(lineList.get(1));
            		unmatchedReport.setAmount(lineList.get(2));
            		unmatchedReport.setDescription(lineList.get(3));
            		
            		return unmatchedReport;
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
		
		return new UnmatchedReport();
	}
}
