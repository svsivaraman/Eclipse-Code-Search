package sampleplugin.views;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author ProSoft
 * 
 * RepositoryAccessor class performs the task of invoking/accessing the methods
 * such as search snippet, save snippet, delete snippet and search recent of the 
 * RepositoryManager class.
 * 
 * It contains the following methods:-
 * 
 * searchSnippet() 	 	 - Perform the operation of searching for snippet in the 
 * 						   repository based on the tags entered by the user.
 *
 * saveSnippet()		 - Perform the operation of saving the snippet, title and  
 * 						   tags as entered by the user in the repository.
 * 
 * deleteSnippet()		 - Perform the operation of deleting a snippet from the  
 * 						   repository based on the tags and title entered by the
 * 						   user.
 * 
 * searchRecent()		 - Perform the operation of searching for recently used 5 
 * 						   snippet by the user from the repository and display it.
 * 
 * clearStoreGroup()	 - Perform the operation of clearing text  controls present in the
 * 						   Store Snippet Group.
 */
public class RepositoryAccessor {

	 ArrayList<String> tagList,titleList,snippetList,snipIdList;
	 Validator validate = new Validator();
	 ShowNotification notify = new ShowNotification();
	 RepositoryManager rmgr = new RepositoryManager();
	 int listCount=0,itemSelected=0;
	 ArrayList<ArrayList<String>> getItemList;
	 boolean checkInput;
	 
	 /**
	 * @param tags			- Holds tags as entered by the user for a snippet
	 * @param snippet		- Holds the snippet value based on the tag selected
	 * @param snippet1		- Holds the snippet value based on the tag selected
	 * @param snippetLinks	- Holds list of tags of the snippets retrieved from
	 * 						  repository
	 * @param shTitle		- Holds the Snippet title value
	 * @param shTag			- Holds the Snippet tag(s) value
	 */
	public void searchSnippet(Text tags, Text snippet, Text snippet1, List snippetLinks, Text shTitle,Text shTag) {
    
		 String getTagText = tags.getText();
		 
		 //Add the array list of title,tag,snippet and id 
		 getItemList = rmgr.readSnippets(getTagText);
		 
		 //To validate user input is empty or not.
		 checkInput = validate.verifyInput(getTagText);
		 
		 //If value is 0 then no tags entered by user before searching for snippets
		 if(checkInput) {
			 notify.showMessage("Please enter search tags.",3);
			 tags.setFocus();
		 } else {
			 
			//Read Tags from repository and store it in arrayList for traversal
			 tagList=getItemList.get(0);
			 
			 //Read Title from repository and store it in arrayList for traversal
			 titleList=getItemList.get(1);
			 
			 //Read snippets from repository and store it in arrayList for traversal
			 snippetList=getItemList.get(2);
			 
			//Read snippet ID's from repository and store it in arrayList for traversal
			 snipIdList = getItemList.get(3);
			 
			 //To clear the List Box contents before adding items to it and clear text boxes
			 snippetLinks.removeAll();
			 
			 //Below piece of code is to add retrieved snippets to the List Box
			 if(!titleList.isEmpty()) {
				 for(listCount = 0;listCount < titleList.size();listCount++) {
				 	 snippetLinks.add(titleList.get(listCount)); 
			 	 }//End of For Loop
		     } else {
		    	 notify.showMessage("No Search Results Found",0);
		     }//End of Else
		 }//End of Outer/First If-Else
	 }// End of searchSnippet method

    /**
     * @param srTags		-Holds the keywords as entered by user/developer
     * @param snippet		-Holds the snippet value of non-editable text control 
     * @param snippet1		-Holds the snippet value  of editable text control
     * @param snippetLinks	-Holds list of tags for the snippets retrieved from
	 * 						  repository 
     * @param rdTitle		-Holds the Title value as entered by the user/developer
     * @param rdTag			-Holds the tags as entered by user/developer in Store Snippet Group
     */
    public void searchRecent(Text srTags,Text snippet, Text snippet1, List snippetLinks,Text rdTitle,Text rdTag) {
   
    	 //Add the array list of title,tag,snippet and id 
		 getItemList = rmgr.getRecent();
		 
		//Read Tags from repository and store it in arrayList for traversal
		 tagList=getItemList.get(0);
		 
		 //Read Title from repository and store it in arrayList for traversal
		 titleList=getItemList.get(1);
		 
		 //Read snippets from repository and store it in arrayList for traversal
		 snippetList=getItemList.get(2);
		 
		//Read snippet ID's from repository and store it in arrayList for traversal
		 snipIdList = getItemList.get(3);
		 
    	//To clear the list box before adding items to list.
    	 snippetLinks.removeAll();
    	 
    	if(!snippetList.isEmpty()) {
    		for(listCount = 0;listCount < titleList.size();listCount++) {
    		  snippetLinks.add(titleList.get(listCount));
    		}//End of For
    	} else {
    		notify.showMessage("No Search Results Found",0);
    	}//End of Else
    }//End of searchRecent method

    /**
     * @param snippet1	- Holds the snippet value as provided by the user
     * @param tagInput	- Holds the tag value as provided by the user
     * @param title		- Holds the title of snippet as provided by the user
     */
    public int saveSnippet( Text snippet1, Text tagInput,Text title) {
   
    	int flag=0,titleStatus;
    	MessageDialog userDialog;
    	
		 //Check the input status of tag, title and snippet values
		 if(validate.verifyInput(title.getText())) {
			 notify.showMessage("Please enter a title for the snippet..",3);
			 title.setFocus();
		 } else if(validate.verifyInput(tagInput.getText())) {
			 notify.showMessage("Please enter tag(s) for the snippet..",3);
			 tagInput.setFocus();
		 } else if(validate.verifyInput(snippet1.getText())) {
			 notify.showMessage("Please enter a snippet to save...",3);
			 snippet1.setFocus();
		 } else if(!validate.verifyInput(snippet1.getText()) || !validate.verifyInput(snippet1.getText()) || !validate.verifyInput(snippet1.getText())) {
			 
			 //Check for duplicate title exists or not
			 titleStatus=rmgr.checkTitle(title);
			
			 if(titleStatus==0) {
				 //Add New Snippet to repository
				 flag = rmgr.writeCode(snippet1.getText(),tagInput.getText(),title.getText());
				 
				 if(flag==1) {
				 		notify.showMessage("Code Snippet saved successfully",1);
				 		return 1;
				 	} else {
				 		notify.showMessage("Code Snippet not saved. File not loaded. Please try again.",0);
				 	}//End of flag If-Else
			 } else if(titleStatus==1){
				 //Ask user/developer for overwriting snippet in repository
				 userDialog = new MessageDialog(new Shell(),"Alert Message",null,"Title entered already exists? Do you want to update only the snippet?",MessageDialog.QUESTION,new String[] { "Yes",
				  "No"},0);
				 
				//Dialog status of 1 (No) indicates "Add New Snippet" snippet and 0(Yes) for "Update only Snippet"
				 if(userDialog.open()==0) {
					 flag = rmgr.updateNode(title,snippet1);
					 if(flag==1) {
					 		notify.showMessage("Code Snippet updated successfully",1);
					 		return 1;
					 	} else {
					 		notify.showMessage("Code Snippet not updated. File not loaded. Please try again.",0);
					 	}//End of flag If-Else
				 } else {
					 notify.showMessage("Title already exists. Please enter a new title",3);
					 title.setText("");
					 title.setFocus();
				 }//End of userDialog If-Else
			 }//End of title status If-Else 
		 }//End of Outer Multiple If-Else
		 return 0;
    }// End of saveSnippet method
    
    /**
     * @param snippetId		- Holds the ID of the snippet to be deleted
     * @param saveTags		- Holds the snippet tags value to be deleted
     * @param snipTitle		- Holds the snippet title value to be deleted
     * @param getSnippet	- Holds the snippet value to be deleted
     */
    public void deleteSnippet(Text snippetId, Text saveTags, Text snipTitle,Text getSnippet) {
    	
    	//Get status of the delete functionality from RepositoryManager class
    	int status = rmgr.delSnippet(snippetId.getMessage());

    	//IF 1 then snippet is deleted
    	if(status==1) {
    		notify.showMessage("Code Snippet deleted successfully",1);
    		getSnippet.getText();
    		clearStoreGroup(snipTitle, saveTags, snippetId);
    	} else {
    		notify.showMessage("Code Snippet not deleted. File Error Problem.",0);
    	}//End if Else
    }//End of deleteSnippet method
    
    /**
     * @param snippet		- Holds the snippet value to be displayed
     * @param snippet1		- Holds the snippet value to be displayed and 
     * 						  modified by user for storing in repository
     * @param snippetLinks	- Holds list of tags for the snippets retrieved
     * 						  from repository 
     * @param snipTitle		- Holds the snippet title value	to be displayed
     * @param snipTags		- Holds the snippet tag(s) value to be displayed
     */
    public void displaySnippets(Text snippet, Text snippet1, List snippetLinks, Text snipTitle, Text snipTags) {
    
    	int[] selectedItems = snippetLinks.getSelectionIndices();

    	for (int loopIndex = 0; loopIndex < selectedItems.length; loopIndex++) {
    		snippet.setText(snippetList.get(selectedItems[loopIndex]));
    		snippet1.setText(snippetList.get(selectedItems[loopIndex]));
    		snippet1.setMessage(snipIdList.get(selectedItems[loopIndex]));
    		snipTitle.setText(titleList.get(selectedItems[loopIndex]));
    		snipTags.setText(tagList.get(selectedItems[loopIndex]));
    	
    		//Capture item selected from the list
        itemSelected = selectedItems[loopIndex];
    	}//End of For Loop
    }//End of displaySnippets method

    /**
     * @param stTitle			- Holds the Title Text control object 
     * @param stTags			- Holds the Tags Text control object
     * @param stSnippet			- Holds the Editable Snippet control object
     */
    public void clearStoreGroup(Text stTitle, Text stTags, Text stSnippet) {
    	stTitle.setText("");
    	stTags.setText("");
    	stSnippet.setText("");
    }//End of clearStoreGroup method

}//End of RepositoryAccessor Class
