package com.transcation_compare.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
import com.transcation_compare.model.UnmatchedReport;

@Controller
public class IndexController {
	private final String REDIRECT = "redirect:/";
	private final String INDEX = "index";

	private FileValidator _fileValidator;
	private FileMatcher _fileMatcher;

	private List<FileData> fileData = new ArrayList<FileData>();
	private List<UnmatchedReport> unmatchedReports = new ArrayList<UnmatchedReport>();
	
	@GetMapping("/")
	public String homepage() 
	{
		return INDEX;
	}

	@GetMapping("/index")
	public String handleGetRequest(Model model)
	{
		model.addAttribute("fileData", fileData);
		model.addAttribute("umatchedReports", unmatchedReports);
		
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

			String errorMessage = "Error in file %s: All rows must have data for the column TransactionID";
			
			if (firstFileHashMap.isEmpty())
			{
				redirectAttributes.addFlashAttribute("message", String.format(errorMessage, firstFileName));
				return REDIRECT;
			}
			
			else if (secondFileHashMap.isEmpty())
			{
				redirectAttributes.addFlashAttribute("message", String.format(errorMessage, secondFileName));
				return REDIRECT;
			}

			List<String> firstXSecond = _fileMatcher.matchHashMaps(firstFileHashMap, secondFileHashMap);
			List<String> secondXFirst = _fileMatcher.matchHashMaps(secondFileHashMap, firstFileHashMap);
			
			
			//I only need to get a report of unmatched data if exists any unmatched data		
			if (convertToInteger(firstXSecond.get(2)) > 0 && convertToInteger(firstXSecond.get(2)) > 0)
			{
				fileData = _fileValidator.addFileData(firstFileName, firstXSecond, firstFileList, 
						secondFileName, secondXFirst, secondFileList);
				
				for (FileData file : fileData)
				{					
					String unmatchingRecordsPositions = file.getFileUnmatchingRecordsPositions();
					
					List<Integer> lstRecordsPositions = Arrays.stream(unmatchingRecordsPositions.split(";"))
							.mapToInt(Integer::parseInt)
							.boxed()
							.collect(Collectors.toList());
					
					unmatchedReports = _fileMatcher.getUnmatchedReportList(file.getFileList(), lstRecordsPositions);
					
					file.setUnmatchedReports(unmatchedReports);
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

	public Integer convertToInteger(String string)
	{
		return Integer.parseInt(string);
	}
	
	
}
