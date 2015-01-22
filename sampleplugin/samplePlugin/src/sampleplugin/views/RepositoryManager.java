package sampleplugin.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Text;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author ProSoft
 * 
 * RepositoryManager class performs the task of reading, writing and deleting 
 * snippet and tags in/from a repository. It also perform the task of showing
 * recently used snippet to the user.
 * 
 * It contains the following methods:-
 * 
 * getFileName() 	 	 - Takes the repository URL location and returns file 
 * 						   name.
 *
 * getDoc()			 	 - Takes repository file name as input and returns the
 * 						   Document object.
 * 
 * transformToXML()		 - This method transforms the respective DOM objects to
 *                         XML data and write it back to the XML file provided. 
 * 
 * readSnippets()		 - Perform operation of reading tags and snippets from the
 * 						   repository based on the tags entered by user and return
 * 						   the results by storing in a List.
 * 
 * writeCode()			 - The following method is used to save the snippet in the
 *                         repository by taking  values like tags,title and snippet
 *                         from the user. The values are passed to this method 
 *                         through the RepositoryAccessor class. This method is 
 *                         invoked in RepositoryAccessor class along with few 
 *                         parameters mentioned below. 
 * 
 * getRecent()			 - This method retrieves the last 5 recently used snippet
 * 						   to the user.
 * 
 * deleteNode()			 - This method deletes a particular snippet node from the 
 * 						   repository based on the user selected snippet. To perform 
 * 						   deletion, a snippet ID is used that passed along with the
 * 						   selected snippet to this method.
 * 
 * checkTitle()			 - This method performs the operation of verifying 
 *                         whether the given title for snippet already exists in 
 *                         repository or not and return value based on it to the 
 *                         RepositoryAccessor class.
 *                        
 * updateNode()			 - This method updates particular snippet value based on the
 *                         given title and clears the respective text boxes of Store
 *                         Snippet and SearchSnippet group except List Box.
 *                                                    
 */


public class RepositoryManager {
    
	//Used to invoke showMessage method for displaying
	//notification to user
	ShowNotification notifyMsg;
	
    //Get the URL location of the repository file and returns to the
    // methods that need this file as input for managing snippet 
    // and tags such as read,write and delete.
    /**
     * @return file - returns the repository file name
     */
    private File getFileName()
    {
	Bundle bundle;
	URL url;
	File file;
	URL fileUrl;
	
		try{
		
			bundle = Platform.getBundle("samplePlugin");
			url = FileLocator.find(bundle, new Path("resources/repository.xml"), null);
			fileUrl = FileLocator.toFileURL(url);
			file = new File(fileUrl.getFile());

			if(!file.exists())
			{
				file.createNewFile();
			}//End of If
	    
			return file;
	    
		}catch(IOException error){
			
			notifyMsg.showMessage("File read error..Try again.",0);
			return null;
			
		}//End of catch
    }//End of getFileName method

    
    /**
     * The following method will take the repository file as input
     * and will return the document object for xml node parsing and storing.
     *  
     * @return doc - A Document object that is used to normalize xml document
     * @return null - returns null if document object does not contain filename
     */
    private Document getDoc() {
    		
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder;
    	Document doc; 
  	    
  	    try{
  	    	
  	    	dBuilder = dbFactory.newDocumentBuilder();
  	  	    doc = dBuilder.parse(getFileName());
  			
  	  	    return doc;
  			
  	    }catch(Exception err){
  	    	
  	    	notifyMsg.showMessage("The following error occured:\n\n"+err.getMessage(),0);
  	    	return null;
  	    	
  	    }//End of catch
    }//End of getDoc() method
    
    
   /**
     * The following method is used to read tags & snippets from the
     * repository and display it in the repository view through
     * RepositoryAccessor class. This method is invoked in
     * RepositoryAccessor class which return the list of
     * selected/retrieved snippet and tags.
     * 
     * @param searchText - contains the tags as entered by the user
     * @return tagList 	 - an array list containing tags retrieved
     *  				   from repository
     */
    public ArrayList<ArrayList<String>> readSnippets(String searchText) {
	    
    	int i;
    	String[] searchTags=searchText.split(" ");
    	ArrayList<String> tagList,titleList, snippetList,snipIDList;
	    XPath xPath;
	    String tagExpression,titleExpression,snippetExpression,idExpression;
	    NodeList tagNodeList,titleNodeList,snippetNodes,deleteNodes;
	    ArrayList<ArrayList<String>> resultList; 
	    
	    try {

	      tagList = new ArrayList<String>();
	    	titleList = new ArrayList<String>();
	    	snippetList = new ArrayList<String>();
	    	snipIDList = new ArrayList<String>();
	    	
	    	//This List is used to store the ArrayList tagList and SnippetList and 
	    	// return them as one List
	    	resultList = new ArrayList<ArrayList<String>>();
	    	
	    	xPath =  XPathFactory.newInstance().newXPath();
	    	
	    	for(int noiseCount=0;noiseCount<searchTags.length;noiseCount++) {
	       		tagExpression = "//Code/Tags[contains(./text(), '"
	        			+ searchTags[noiseCount] +"')]/parent::Code/Tags/text()";
	    		tagNodeList = (NodeList) xPath.evaluate(tagExpression,getDoc(),XPathConstants.NODESET);
	  
	    		//Loop for storing Tags
	    		for(i=0; i<tagNodeList.getLength();i++){
	    				 tagList.add(tagNodeList.item(i).getNodeValue());
	    		}//End of First Inner For loop
	 
	    		titleExpression = "//Code/Tags[contains(./text(), '"
	        			+ searchTags[noiseCount] +"')]/parent::Code/Title/text()";
	    		titleNodeList = (NodeList) xPath.evaluate(titleExpression,getDoc(),XPathConstants.NODESET);
	  
	    		//Loop for storing Tags
	    		for(i=0; i<titleNodeList.getLength();i++){
	    				 titleList.add(titleNodeList.item(i).getNodeValue());
	    		}//End of First Inner For loop
	    		
	    		snippetExpression = "//Code/Tags[contains(./text(), '"
	        			+ searchTags[noiseCount] +"')]/parent::Code/Snippet/text()";
	        			
	        	snippetNodes = (NodeList) xPath.evaluate(snippetExpression,getDoc(),XPathConstants.NODESET);

	        	//Loop for storing Snippets
	        	for(i=0; i<snippetNodes.getLength();i++){
	        			snippetList.add(snippetNodes.item(i).getNodeValue());
	        	}//End of Second Inner For loop
	    		
	        	idExpression = "//Code/Tags[contains(./text(), '"
	        			+ searchTags[noiseCount] +"')]/parent::Code/@ID";
	  
	        	deleteNodes = (NodeList) xPath.evaluate(idExpression,getDoc(),XPathConstants.NODESET);
	      	  
	    		//Loop for storing snippet ID's
	    		for(i=0; i<deleteNodes.getLength();i++){
	    				 snipIDList.add(deleteNodes.item(i).getNodeValue());
	    		}//End of Third Inner For loop
	     }//End of Outer For loop

	    	getDoc().getDocumentElement().normalize();
	    	
	    	//The below code adds the tag and snippet List to a common list
	    	// so that only one list is returned.
	    	resultList.add(tagList);
	    	resultList.add(titleList);
	    	resultList.add(snippetList);
	    	resultList.add(snipIDList);
	    	
	    	return resultList;
	    } catch (Exception err) {
	    	notifyMsg.showMessage("The following errors occured:\n\n"+err.getMessage(),0);
	    return null;
	    }//End of Catch
	  }//End of readTags method
     
    /**
      * The following method is used to get 5 recently used snippets
      * from the repository and display it in the repository view through
      * RepositoryAccessor class. This method is invoked in
      * RepositoryAccessor class which returns the array list of last 6
      * recent snippets.
      * 
      * @return recentList 	 - an array list containing recently used 6 
      * 					   snippets retrieved from repository
      */
    public ArrayList<ArrayList<String>> getRecent()
    {
    	int loop,listCount,listIndex;
    	XPath xPath =  XPathFactory.newInstance().newXPath();
    	ArrayList<String> tagList,titleList, snippetList,idList;
	    String tagExp,titleExp,snipExp,idExp;
	    NodeList tagNList,titleNList,snipNList,idNList;
	    ArrayList<ArrayList<String>> recentList; 	
    	
	    titleExp = "//Code/Title/text()";
	    tagExp = "//Code/Tags/text()";
	    snipExp = "//Code/Snippet/text()";
	    idExp = "//Code/@ID";
	    
    	try {
    		recentList = new ArrayList<ArrayList<String>>();
    	
    		//Get total number of elements in XML file with tag 'Code' and limit list to 5 if greater
    		listCount=getDoc().getElementsByTagName("Code").getLength();
    		if(listCount >= 5)
    			listCount=5;
    		
    		tagNList = (NodeList) xPath.evaluate(tagExp,getDoc(),XPathConstants.NODESET);
    		tagList = new ArrayList<String>(tagNList.getLength());
	    
    		//Add to the arraylist the last 5 code snippets from the XML file
    		for(loop = 0, listIndex = tagNList.getLength()-1; loop < listCount;loop++,listIndex--)
    			tagList.add(tagNList.item(listIndex).getNodeValue());
    		
    		titleNList = (NodeList) xPath.evaluate(titleExp,getDoc(),XPathConstants.NODESET);
    		titleList = new ArrayList<String>(titleNList.getLength());
	    
    		//Add to the arraylist the last 5 code snippets from the XML file
    		for(loop = 0, listIndex = titleNList.getLength()-1; loop < listCount;loop++,listIndex--)
    			titleList.add(titleNList.item(listIndex).getNodeValue());
    	
    		snipNList = (NodeList) xPath.evaluate(snipExp,getDoc(),XPathConstants.NODESET);
    		snippetList = new ArrayList<String>(snipNList.getLength());
	    
    		//Add to the arraylist the last 5 code snippets from the XML file
    		for(loop = 0, listIndex = snipNList.getLength()-1; loop < listCount;loop++,listIndex--)
    			snippetList.add(snipNList.item(listIndex).getNodeValue());
    	
    		idNList = (NodeList) xPath.evaluate(idExp,getDoc(),XPathConstants.NODESET);
    		idList = new ArrayList<String>(idNList.getLength());
	    
    		//Add to the arraylist the last 5 code snippets from the XML file
    		for(loop = 0, listIndex = idNList.getLength()-1; loop < listCount;loop++,listIndex--)
    			idList.add(idNList.item(listIndex).getNodeValue());
    	
    		getDoc().getDocumentElement().normalize();

    		recentList.add(tagList);
    		recentList.add(titleList);
    		recentList.add(snippetList);
    		recentList.add(idList);
    		
    		return recentList;
    	} catch (Exception e) {
    		notifyMsg.showMessage("File loading error. Please try again.",0);
    		return null;
    	}//End of Catch
    }//End of getRecent() Method
	
    /**
     *  
     * @param getTitle	- Hold the snippet title value as provided by the user/developer
     * @return 1/0		- Returns 1 if title already exists or else 0
     */
    public int checkTitle(Text getTitle) {
    	
    	Document doc = getDoc();
        XPath xPath = XPathFactory.newInstance().newXPath();
        NodeList getNodes = null;
        
        try {
        	getNodes = (NodeList) xPath.evaluate("//Code/Title[text() ='" + getTitle.getText() + "']/parent::Code/Snippet",doc, XPathConstants.NODESET);
        } catch(Exception e) {
        	notifyMsg.showMessage("File loading error. Please try again.",0);
        }
       
    	//Duplicate Title Found or not
        if(getNodes.getLength() > 0) {
        	return 1;
        }else {
        	return 0;
        }//End of If-Else
    }//End of checkTitle method
    
    /**
     *
     * @param readTitle		- Holds the snippet title value for searching
     * @param readSnippet	- Holds the snippet value that is to be updated in repository
     * @return	1/0			- returns 1 is updates successfully in repository else 0.
     */
    public int updateNode(Text readTitle,Text readSnippet) {
    	
    	Document doc = getDoc();
        XPath xPath = XPathFactory.newInstance().newXPath();
        Node getNode = null;
        String newSnippet = readSnippet.getText();
        
        try {
        	getNode = (Node) xPath.evaluate("//Code/Title[text() ='" + readTitle.getText() + "']/parent::Code/Snippet",doc, XPathConstants.NODE);
        	
        	//Replace the old snippet with new snippet in repository
        	getNode.getFirstChild().setNodeValue(newSnippet);
        	
        	//Convert DOM objects to XML data
        	doc.getDocumentElement().normalize();
        	
        	//Write back to the repository
        	transformToXMl(doc);
        	
        	return 1;	
        	
        } catch(Exception e) {
        	notifyMsg.showMessage("File loading error. Please try again.",0);
        }//End of Try-Catch
    	return 0;
    }//End of updateNode Method
    
        
    /**
     * 
     * @param snippet	- contains the code snippet value as provided by 
     * 					  the user in repository view 
     * @param tag		- contains the tags value as provided by the user
     * 					  in repository view 	
     * @param title		- contains the title of snippet as provided by 
     * 					  the user in repository view
     * @return			- returns 1 or 0 based on the snippet saved in 
     * 					  repository successfully
     */
    public int writeCode(String snippet,String tag,String title)
    {
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    	Date date = new Date();
    	Element root1;
    	Document doc;
      int idVal;
      String idAttr;
      XPath xPath = XPathFactory.newInstance().newXPath();
      
    	//Get the document name from the URL
    	doc= getDoc();
		
    	try {

			  idAttr = xPath.evaluate("//Code[last()]/@ID", getDoc());
        
			  //Get the ID number and increment by 1
			  idVal = Integer.parseInt(idAttr.substring(idAttr.length()-2)) + 1;
			  
			  root1 = doc.getDocumentElement();

				Element child = doc.createElement("Code");
				child.setAttribute("ID","Code" + idVal);
				root1.appendChild(child);

				Element child1 = doc.createElement("Tags");
				child1.appendChild(doc.createTextNode(tag));
				child.appendChild(child1);

				Element child2 = doc.createElement("Title");
				child2.appendChild(doc.createTextNode(title));
				child.appendChild(child2);

				Element child3 = doc.createElement("Snippet");
				child3.appendChild(doc.createTextNode(snippet));
				child.appendChild(child3);

				Element child4 = doc.createElement("TimeStamp");
				child4.appendChild(doc.createTextNode(dateFormat.format(date)));
				child.appendChild(child4);

				//Transform DOM objects to XML file
				transformToXMl(doc);
				
    			return 1;
	    
			}catch(Exception e){
	 		
				notifyMsg.showMessage("Following error occured: \n\n"+e.getMessage(),0);
				return 0;
			}//End of catch
    }//End of writeCode() method

    
    /**
     * @param getDoc	- Holds the Document object which contains all the DOM objects
     */
    private void transformToXMl(Document getDoc) {

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer;
		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(getDoc);
		String xmlString;
		BufferedWriter writeXML; 
		
		try {
			transformer = factory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
			transformer.transform(source, result);

			xmlString = sw.toString();

			writeXML = new BufferedWriter(new FileWriter(getFileName()));
			writeXML.write(xmlString);
			writeXML.flush();
			writeXML.close();
		} catch(Exception e) {
			notifyMsg.showMessage("Following error occured: \n\n"+e.getMessage(),0);
		}//End of Try-Catch
    }//End of transformToXML method

    /**
     * @param deleteID	- contains code ID of the respective snippet to be
     * 					  deleted. 
     * @return 		 	- returns 1 or 0 based on the snippet being deleted
     * 					  successfully 
     */
    public int delSnippet(String deleteID) {
	
		String delExpression;
    	XPath xPath;
    	Document doc;
    	
    	//Get the document name from the URL location specified
    	doc=getDoc(); 
    
    	//XPath query to fetch the specific node for delete
    	xPath =  XPathFactory.newInstance().newXPath();
    	delExpression = "//Code[@ID='"+ deleteID +"']";
    	
    	try {
    		NodeList nodelist1 = (NodeList) xPath.evaluate(delExpression,doc,XPathConstants.NODESET);
    		for(int i=0; i<nodelist1.getLength();i++) {
    			nodelist1.item(i).getParentNode().removeChild(nodelist1.item(i));
    		}//End of For Loop
    		doc.getDocumentElement().normalize();
    		transformToXMl(doc);
    		return 1;
    	} catch (Exception err) {
    		notifyMsg.showMessage("The following error occured:\n\n"+err.getMessage(),0);
    		return 0;
		}//End of Catch
	}//End of deleteSnippet method
}//End of RepositoryManager class


