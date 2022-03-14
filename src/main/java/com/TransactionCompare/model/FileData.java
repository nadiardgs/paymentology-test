package com.TransactionCompare.model;

import java.util.List;

public class FileData 
{
	private String fileName;
	
	private String fileTotalRecords;
	
	private String fileTotalMatchingRecords;
	
	private String fileTotalUnmatchingRecords;
	
	private String fileUnmatchingRecordsPositions;
	
	private List<UnmatchedReport> unmatchedReports;

	public List<UnmatchedReport> getUnmatchedReports() {
		return unmatchedReports;
	}

	public void setUnmatchedReports(List<UnmatchedReport> unmatchedReports) {
		this.unmatchedReports = unmatchedReports;
	}

	public String getFileUnmatchingRecordsPositions() {
		return fileUnmatchingRecordsPositions;
	}

	public void setFileUnmatchingRecordsPositions(String fileUnmatchingRecordsPositions) {
		this.fileUnmatchingRecordsPositions = fileUnmatchingRecordsPositions;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileTotalRecords() {
		return fileTotalRecords;
	}

	public void setFileTotalRecords(String fileTotalRecords) {
		this.fileTotalRecords = fileTotalRecords;
	}

	public String getFileTotalMatchingRecords() {
		return fileTotalMatchingRecords;
	}

	public void setFileTotalMatchingRecords(String fileTotalMatchingRecords) {
		this.fileTotalMatchingRecords = fileTotalMatchingRecords;
	}

	public String getFileTotalUnmatchingRecords() {
		return fileTotalUnmatchingRecords;
	}

	public void setFileTotalUnmatchingRecords(String fileTotalUnmatchingRecords) {
		this.fileTotalUnmatchingRecords = fileTotalUnmatchingRecords;
	}
	
}
