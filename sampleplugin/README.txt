###################################
#	PLUGIN INSTALLATION	  #
###################################

1 - Copy the codeSearchPlugin_1.0.jar file in the plugins folder of your Eclipse installation 
    directory.

2 - Open Eclipse.

3 - Go to Window > Show View > Other > Code Search

	3.1 - Select "Search Repository" for searching or saving snippet in database
	3.2 - Select "Search Web" for searching snippet using web browser.	

If everything is set up correctly, the tab of Web and Repository views should show up in the
eclipse view, or you get an error message otherwise.

Note: Repository file is already created in the resources folder and in case deleted manually,
      the file will be created in the same folder under same name by the plug-in automatically.


###################################
#	  FUNCTIONALITIES         #
###################################


Code Search provides information about various usage of snippet through several code search
engines or from the repository provided by the plug-in. The particular code search engine can
be selected by navigating the list provided in the "Web View" and the results are displayed 
back. Similarly, "Repository View" provides option for searching snippet using tags or keywords
which return results to the list box present in the repository view.   

However, Code Search can also be manually triggered. There are disparate ways of performing it:

1 - Through the contextual menu of the code Editor: Right click on the code editor and navigate
	to Code Search and select the view(s).
2 - Through the contextual menu of the package Explorer: Right click on methods or class 
	elements and navigate to Code Search and select the view(s).


