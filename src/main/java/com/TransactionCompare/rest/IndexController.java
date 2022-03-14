package com.TransactionCompare.rest;

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
import com.TransactionCompare.fileHandling.*;
import com.TransactionCompare.model.FileData;
import com.TransactionCompare.model.UnmatchedReport;

@Controller
public class IndexController {
	private final String REDIRECT = "redirect:/";
	private final String INDEX = "index";

	private FileValidator _fileValidator;
	private FileMatcher _fileMatcher;

	private List<FileData> fileData = new ArrayList<FileData>();
	
	@GetMapping("/")
	public String homepage() {
		return INDEX;
	}

	@GetMapping("/index")
	public String handleGetRequest(Model model)
	{
		model.addAttribute("fileData", fileData);
		return INDEX;
	}
	
	@PostMapping("/index")
	public String handlePostRequest(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
			RedirectAttributes redirectAttributes) {
		
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

			HashMap<String, String> firstFileHashMap = _fileValidator.convertCsvToHashMap(firstFileIS);
			HashMap<String, String> secondFileHashMap = _fileValidator.convertCsvToHashMap(secondFileIS);

			String errorMessage = "Error in file %s: All rows must have data for the column TransactionID";
			
			if (firstFileHashMap.isEmpty())
			{
				redirectAttributes.addFlashAttribute(String.format(errorMessage, firstFileName));
				return REDIRECT;
			}
			else if (secondFileHashMap.isEmpty())
			{
				redirectAttributes.addFlashAttribute(String.format(errorMessage, secondFileName));
				return REDIRECT;
			}

			List<String> firstXSecond = _fileMatcher.matchHashMaps(firstFileHashMap, secondFileHashMap, firstFileIS);
			List<String> secondXFirst = _fileMatcher.matchHashMaps(secondFileHashMap, firstFileHashMap, secondFileIS);
			
			fileData = addFileData(firstFileName, firstXSecond, secondFileName, secondXFirst);
			
			for (FileData file : fileData)
			{
				String unmatchingRecordsPositions = file.getFileUnmatchingRecordsPositions();
				
				List<Integer> lstRecordsPositions = 
					    Arrays.stream(unmatchingRecordsPositions.split(";")).map(Integer::parseInt).collect(Collectors.toList());
				
				List<UnmatchedReport> unmatchedReport = _fileMatcher.getUnmatchedReport(lstRecordsPositions, file.getFileName());
				
				file.setUnmatchedReports(unmatchedReport);
			}
		} 
		
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "redirect:/index";
	}

	public FileData addData(String fileName, List<String> fileData)
	{
		FileData file = new FileData();
		
		file.setFileName(fileName);
		file.setFileTotalRecords(fileData.get(0));
		file.setFileTotalMatchingRecords(fileData.get(1));
		file.setFileTotalUnmatchingRecords(fileData.get(2));
		file.setFileUnmatchingRecordsPositions(fileData.get(3));
		
		return file;
	}
	
	public List<FileData> addFileData(String firstFileName, List<String> firstFileData, String secondFileName, List<String> secondFileData)
	{
		List<FileData> fileData = new ArrayList<FileData>();
		
		FileData firstFile = addData(firstFileName, firstFileData);
		fileData.add(firstFile);
		
		FileData secondFile = addData(secondFileName, secondFileData);
		fileData.add(secondFile);
		
		return fileData;
	}
}
