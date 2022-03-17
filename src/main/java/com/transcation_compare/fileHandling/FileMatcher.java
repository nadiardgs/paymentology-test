package com.transcation_compare.fileHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.transcation_compare.model.FileData;
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

}
