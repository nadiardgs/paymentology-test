package com.transcation_compare.model;

import java.util.List;

public class FileData 
{
	private String fileName;
	
	private Integer fileTotalRecords;
	
	private Integer fileTotalMatchingRecords;
	
	private Integer fileTotalUnmatchingRecords;
	
	private List<UnmatchedFileData> unmatchedFileDatas;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getFileTotalRecords() {
		return fileTotalRecords;
	}

	public void setFileTotalRecords(Integer fileTotalRecords) {
		this.fileTotalRecords = fileTotalRecords;
	}

	public Integer getFileTotalMatchingRecords() {
		return fileTotalMatchingRecords;
	}

	public void setFileTotalMatchingRecords(Integer fileTotalMatchingRecords) {
		this.fileTotalMatchingRecords = fileTotalMatchingRecords;
	}

	public Integer getFileTotalUnmatchingRecords() {
		return fileTotalUnmatchingRecords;
	}

	public void setFileTotalUnmatchingRecords(Integer fileTotalUnmatchingRecords) {
		this.fileTotalUnmatchingRecords = fileTotalUnmatchingRecords;
	}

	public List<UnmatchedFileData> getUnmatchedFileDatas() {
		return unmatchedFileDatas;
	}

	public void setUnmatchedFileDatas(List<UnmatchedFileData> unmatchedFileDatas) {
		this.unmatchedFileDatas = unmatchedFileDatas;
	}
}
