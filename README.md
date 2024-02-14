
# MetaDefender Scanner

The Java client project to connect MetaDefender Cloud and MetaDefender Core to scan files from a folder.

To build the jar:

	mvn clean install

To run the Scanner

	java -jar scanner.jar [<the bellow args>]
	
	 -b,--show_blocked_files_only <arg>   Set to true if you want to show
                                          blocked files only
	 -e,--exclude <arg>                   Do not process these files/folders,
										  separated by ','
	 -f,--folder <arg>                    Input folder path
	 -k,--apikey <arg>                    APIKey
	 -l,--log <arg>                       Path to log file
	 -p,--private_scan <arg>              Set to true if you want to scan
										  private with MetaDefender Cloud
	 -r,--rule <arg>                      Workflow name
	 -t,--timeout <arg>                   Timeout per scan (s)
	 -u,--url <arg>                       Metadefender Core/Cloud REST URL