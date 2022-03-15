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

import com.transcation_compare.model.UnmatchedReport;

public class FileMatcher 
{
	public FileValidator _fileValidator = new FileValidator();
	
	/*
	 * This method returns a List<String> containing a report for the given file, as follows:
	 * List position 0: number of transactions
	 * List position 1: number of matching transactions
	 * List position 2: number of unmatched transactions
	 * List position 3: a string with each position where an unmatched transaction was found in the file, separated by ";"
	 * (will be later read to get the unmatched report)
	 * */
	public List<String> matchHashMaps(HashMap<String, String> firstFile, HashMap<String, String> secondFile)
    {
        List<String> listMatchAndUnmatch = new ArrayList<>();
        Integer matchCount = 0;
        Integer unmatchCount = 0;
        Integer unmatchedPosition = 0;
        List<Integer> lstUnmatchedPositions = new ArrayList<Integer>();
        
        for (Map.Entry<String, String> set : firstFile.entrySet())
        {
        	unmatchedPosition++;
        	
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
                    lstUnmatchedPositions.add(unmatchedPosition);
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
	
	/*
	 * Accept the list with the file data and the list of positions of unmatched transactions
	 * And return a list of unmatched reports
	 * */
	public List<UnmatchedReport> getUnmatchedReportList(List<String> fileAsList, List<Integer> unmatchedPositions)
	{
		
		UnmatchedReport unmatchedReport;
		
		List<UnmatchedReport> unmatchedReportList = new ArrayList<UnmatchedReport>();
		
		for (Integer unmatchedPosition : unmatchedPositions)
		{
			String line = fileAsList.get(unmatchedPosition);
			
			List<String> lineList = _fileValidator.getLineAsList(line);
			
			unmatchedReport = new UnmatchedReport();
			
			unmatchedReport.setDate(lineList.get(1));
    		unmatchedReport.setAmount(lineList.get(2));
    		unmatchedReport.setDescription(lineList.get(3));
    		
    		unmatchedReportList.add(unmatchedReport);
		}
		
		return unmatchedReportList;
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
				
				List<String>lineList = _fileValidator.getLineAsList(line);
				
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
            	List<String> lineList = _fileValidator.getLineAsList(line);
            	
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
