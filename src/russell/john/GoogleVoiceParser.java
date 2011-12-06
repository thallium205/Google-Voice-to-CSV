package russell.john;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.SimpleNodeIterator;

public class GoogleVoiceParser
{
	public ArrayList<TextType> textMessages = new ArrayList<TextType>();
	
	GoogleVoiceParser(){}
	
	public void start()
	{
		// Find html
		File file = new File(System.getProperty("user.dir"));
		String[] extensions = {"html", "htm"};
		Iterator<File> iterator;
		iterator = FileUtils.iterateFiles(file, extensions, true);
		
		StringBuilder stringBuilder = new StringBuilder();
		
		// Read html
		while (iterator.hasNext())
		{
			try
			{
				FileReader fileReader = new FileReader(iterator.next());
				BufferedReader bufferedReader = new BufferedReader(fileReader);		
				String line = null;
				
				line = bufferedReader.readLine();
				while (line != null)
				{
					stringBuilder.append(line);
					line = bufferedReader.readLine();					
				}				
			} 
			
			catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
		
		// Parse html		
		try
		{		
			TextType textType;
			Parser parser = new Parser(stringBuilder.toString());	
			NodeList nodeList = new NodeList();
			HasAttributeFilter filterMessage = new HasAttributeFilter();			
			SimpleNodeIterator simpleNodeIterator;		
			
			filterMessage.setAttributeName("class");
			filterMessage.setAttributeValue("message");
			
			// Parse each component of a text message			
			// Date
			HasAttributeFilter filterDate = new HasAttributeFilter();	
			filterDate.setAttributeName("class");
			filterDate.setAttributeValue("dt");
			
			// Name
			HasAttributeFilter filterSender = new HasAttributeFilter();	
			filterSender.setAttributeName("class");
			filterSender.setAttributeValue("fn");
			
			// Telephone
			HasAttributeFilter filterTelephone = new HasAttributeFilter();
			filterTelephone.setAttributeName("class");
			filterTelephone.setAttributeValue("tel");
			
			// Message 
			StringFilter filterContent = new StringFilter();
			filterContent.setPattern("<q>");
			
			// For each message	
			nodeList = parser.parse(filterMessage);			
			simpleNodeIterator = nodeList.elements();			
					
			while (simpleNodeIterator.hasMoreNodes())
			{				
				textType = new TextType();
				Node node = simpleNodeIterator.nextNode();				
				String dateString = node.getChildren().extractAllNodesThatMatch(filterDate, false).toHtml();
				String nameString = node.getChildren().extractAllNodesThatMatch(filterSender, true).toHtml();				
				String telephoneString = node.getChildren().extractAllNodesThatMatch(filterTelephone, false).toString();
				String messageString = node.getChildren().elementAt(9).toPlainTextString();
				
				// Get the date
				textType.setDate(dateString.substring(dateString.indexOf("title=\"") + 7, dateString.length() - 2));
				
				// Get the name
				// If a recipient
				if (nameString.startsWith("<span class=\"fn\">"))
					textType.setName(nameString.substring(nameString.indexOf("class=\"fn\">") + 11, nameString.indexOf("</span>")));
				// If a sender
				else if (nameString.startsWith("<abbr class=\"fn\" title=\""))
					textType.setName(nameString.substring(nameString.indexOf("title=\"") + 7, nameString.length() - 2));
				
				// Get the number
				textType.setNumber(telephoneString.substring(telephoneString.indexOf("tel:") + 4, telephoneString.indexOf(";")));
				
				// Get the message (the "toPlainTextString()" is not properly rendering apostrophes right, so we will fix it				
				textType.setMessage(messageString.replace("&#39", "'").replace("';", "'"));
				
				// Store it
				textMessages.add(textType);						
			}
			
			// Sort by date TODO
			
			// Output to CSV 		
			// System.out.println(file.getCanonicalPath() + "\\Text_Messages.csv");
			FileWriter fileWriter = new FileWriter("Text_Messages.csv");
			Iterator<TextType> iter = textMessages.iterator();
			TextType text;
			
			// Generate CSV headers
			fileWriter.append("Date");
			fileWriter.append(",");
			fileWriter.append("Name");
			fileWriter.append(",");
			fileWriter.append("Number");
			fileWriter.append(",");
			fileWriter.append("Message");
			fileWriter.append("\n");
			
			// Generate CSV content
			while (iter.hasNext())
			{
				text = (TextType) iter.next();
				
				fileWriter.append(text.getDate());
				fileWriter.append(",");
				fileWriter.append(text.getName());
				fileWriter.append(",");
				fileWriter.append(text.getNumber());
				fileWriter.append(",");
				fileWriter.append(text.getMessage());
				fileWriter.append("\n");
			}
			
			fileWriter.flush();
			fileWriter.close();
		} 
		catch (ParserException e)
		{			
			e.printStackTrace();
		} 
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		
		
	}
	
	
	

}
