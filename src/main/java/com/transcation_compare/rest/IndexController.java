package com.transcation_compare.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.transcation_compare.fileHandling.*;
import com.transcation_compare.model.FileData;
import com.transcation_compare.model.UnmatchedFileData;

@Controller
public class IndexController {
	private final String REDIRECT = "redirect:/";
	private final String INDEX = "index";

	private FileValidator _fileValidator;
	private FileMatcher _fileMatcher;

	private List<FileData> fileData = new ArrayList<FileData>();
	private List<UnmatchedFileData> unmatchedFileDatas = new ArrayList<UnmatchedFileData>();
	
	@GetMapping("/")
	public String homepage() 
	{
		return INDEX;
	}

	@GetMapping("/index")
	public String handleGetRequest(Model model)
	{
		model.addAttribute("fileData", fileData);
		model.addAttribute("umatchedReports", unmatchedFileDatas);
		
		return INDEX;
	}
	
	@PostMapping("/index")
	public String handlePostRequest(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
			RedirectAttributes redirectAttributes) 
	{
		
		_fileValidator = new FileValidator();
		
		_fileMatcher = new FileMatcher();

		if (file1.isEmpty() || file2.isEmpty()) 
		{
			redirectAttributes.addFlashAttribute("message", "Please select the files.");
			return REDIRECT;
		}

		String firstFileName = StringUtils.cleanPath(file1.getOriginalFilename());
		String secondFileName = StringUtils.cleanPath(file2.getOriginalFilename());

		if (!_fileValidator.checkIfFilesExtensionAreCsv(firstFileName, secondFileName)) 
		{
			redirectAttributes.addFlashAttribute("message", "Both files' extension must be .csv.");
			return REDIRECT;
		}

		try 
		{
			InputStream firstFileIS = file1.getInputStream();
			InputStream secondFileIS = file2.getInputStream();
			
			List<String> firstFileList = _fileValidator.getInputStreamAsList(firstFileIS);
			List<String> secondFileList = _fileValidator.getInputStreamAsList(secondFileIS);

			HashMap<String, String> firstFileHashMap = _fileValidator.getListAsHashMap(firstFileList);
			HashMap<String, String> secondFileHashMap = _fileValidator.getListAsHashMap(secondFileList);
			
			if (!checkIfHashMapIsEmpty(firstFileHashMap, firstFileName, redirectAttributes).isEmpty())
			{
				return REDIRECT;
			}
			else if (!checkIfHashMapIsEmpty(secondFileHashMap, secondFileName, redirectAttributes).isEmpty())
			{
				return REDIRECT;
			}

			FileData firstFileData = _fileMatcher.matchHashMapsAsFileData(firstFileHashMap, firstFileList, secondFileHashMap);
			FileData secondFileData = _fileMatcher.matchHashMapsAsFileData(secondFileHashMap, secondFileList, firstFileHashMap);
			
			firstFileData.setFileName(firstFileName);
			secondFileData.setFileName(secondFileName);
			
			fileData.clear();
			unmatchedFileDatas.clear();
			
			fileData.add(firstFileData);
			fileData.add(secondFileData);
			
			//I only need to get a report of unmatched data if exists any unmatched data		
			if (firstFileData.getFileTotalUnmatchingRecords() > 0 || secondFileData.getFileTotalUnmatchingRecords() > 0)
			{				
				for (FileData file : fileData)
				{	
					unmatchedFileDatas.addAll(file.getUnmatchedFileDatas());
					
				}
			}
		} 
		
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/index";
	}
	
	public String checkIfHashMapIsEmpty(HashMap<String, String> hashMap, String fileName, RedirectAttributes redirectAttributes)
	{
		String errorMessage = "Error in file %s: All rows must have data for the column TransactionID";
		
		if (hashMap.isEmpty())
		{
			redirectAttributes.addFlashAttribute("message", String.format(errorMessage, fileName));
			return errorMessage;
		}
		return "";
	}

	public Integer convertToInteger(String string)
	{
		return Integer.parseInt(string);
	}
	
	
}
