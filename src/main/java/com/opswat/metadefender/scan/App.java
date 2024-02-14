package com.opswat.metadefender.scan;


import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class App {

	public static CommandLine parseOptions(String[] args) {
		Options options = new Options();

		Option scanURL = new Option("u", "url", true, "Metadefender Core/Cloud REST URL");
		scanURL.setRequired(true);
        options.addOption(scanURL);

        Option apiKey = new Option("k", "apikey", true, "APIKey");
        apiKey.setRequired(false);
        options.addOption(apiKey);

		Option input = new Option("f", "folder", true, "Input folder path");
        input.setRequired(true);
        options.addOption(input);

        Option exclude = new Option("e", "exclude", true, "Do not process these files/folders, separated by ','");
        exclude.setRequired(false);
        options.addOption(exclude);

        Option logFilePath = new Option("l", "log", true, "Path to log file");
        logFilePath.setRequired(true);
        options.addOption(logFilePath);

        Option rule = new Option("r", "rule", true, "Workflow name");
        rule.setRequired(false);
        options.addOption(rule);

        Option isPrivateScan = new Option("p", "private_scan", true, "Set to true if you want to scan private with MetaDefender Cloud");
        isPrivateScan.setRequired(false);
        options.addOption(isPrivateScan);

        Option showBlockedFilesOnly = new Option("b", "show_blocked_files_only", true, "Set to true if you want to show blocked files only");
        showBlockedFilesOnly.setRequired(false);
        options.addOption(showBlockedFilesOnly);
        
        Option timeout = new Option("t", "timeout", true, "Timeout per scan (s)");
        showBlockedFilesOnly.setRequired(false);
        options.addOption(timeout);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            formatter.printHelp("Metadefender scanner", options);
        }
        return cmd;
	}

	public static void main(String[] args) {

		CommandLine cmd = parseOptions(args);
		if (cmd == null) System.exit(1);

		String scanURL = cmd.getOptionValue("url");
		String rule = cmd.getOptionValue("rule") == null ? "" : cmd.getOptionValue("rule");
		String source = cmd.getOptionValue("folder");
		String exclude = cmd.getOptionValue("exclude") == null ? "" : cmd.getOptionValue("exclude");
		String logFilePath = cmd.getOptionValue("log");
		String apiKey = cmd.getOptionValue("apikey") == null ? "" : cmd.getOptionValue("apikey");
		boolean isCreateLog = true;
		boolean isPrivateScan = cmd.getOptionValue("private_scan") == null ? false : Boolean.parseBoolean(cmd.getOptionValue("private_scan"));
		boolean isShowBlockedOnly = cmd.getOptionValue("show_blocked_files_only") == null ? false : Boolean.parseBoolean(cmd.getOptionValue("show_blocked_files_only"));
		int timeout = cmd.getOptionValue("timeout") == null ? 60 : Integer.parseInt(cmd.getOptionValue("timeout"));


		try {
			ConsoleLog console = new ConsoleLog("MetaDefender Scanner");
			console.logInfo("Running with the bellow configurations");
			console.logInfo("Scan URL: " + scanURL);
			if (!apiKey.equals("")) {
				console.logInfo("API Key: " + apiKey.substring(0,3) + "***");
			}
			console.logInfo("Timeout: " + timeout + " (s)");
			console.logInfo("Rule: " + rule);	
			console.logInfo("Scan folder/files: " + source);
			console.logInfo("Exclude folder/files: " + exclude);
			console.logInfo("Show blocked files only: " + isShowBlockedOnly);
			console.logInfo("Private scan: " + isPrivateScan);		
			console.logInfo("--------------------------------------------------------");

			boolean foundBlockedResult = false;
			Scanner sc = new Scanner(scanURL, apiKey, source, exclude, rule, isPrivateScan, timeout,
					isCreateLog, logFilePath);
			ArrayList<ScanResult> results = sc.call();
			if (results == null) {
				Utils.writeLogFile(logFilePath, "Failed to get the result", false, true);
				System.exit(1);
			} else {
				for (ScanResult rs : results) {
					if (rs.getDataID().equals("")) {
						console.logError(
								rs.getFilepath() + "|" + rs.getBlockedReason());
						foundBlockedResult = true;
					} else {
						if (rs.getBlockedResult().equals("Blocked")) {
							console.logError(rs.getFilepath() + " | "
									+ Utils.createScanResultLink(scanURL, rs.getDataID()) + " | "
									+ rs.getBlockedReason());
							foundBlockedResult = true;
						} else if (!isShowBlockedOnly) {
							console.logInfo(rs.getFilepath() + " | "
									+ Utils.createScanResultLink(scanURL, rs.getDataID()) + " | " + rs.getScanResult());
						}
					}
				}
			}
			if (foundBlockedResult) {
				console.logError("Found an issue during scan");
				Utils.writeLogFile(logFilePath, "[ERROR] Found an issue during scan", true, true);
				System.exit(1);
			} else {
				console.logInfo("Scanned successfully. No issue found");
				Utils.writeLogFile(logFilePath, "Scanned successfully. No issue found", true, true);
				System.exit(0);
			}
		} catch (Exception e) {
			Utils.writeLogFile(logFilePath, "[ERROR] Error during scan" + e.getMessage(), true, true);
			System.exit(1);
		}

	}
}
