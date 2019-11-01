package model;

public class MemeConverter
{
	// Disable instantiation (just like math classes)
	private MemeConverter() {}
	
	private static String mintIRI(String memeURL)
	{
		int loc = 0;
		
		for(int i = 0; i < memeURL.length(); i++)
		{
			if (memeURL.charAt(i) == '/')
			{
				loc = i;
			}
		}
		
		String baseIRI = memeURL.substring(loc + 1, memeURL.length());
//		baseIRI = baseIRI.replace("-", "");
		// meeeeeeeh
		try
		{
			baseIRI = baseIRI.substring(0, 1).toUpperCase() + baseIRI.substring(1, baseIRI.length());			
		}
		catch (StringIndexOutOfBoundsException e)
		{
			
		}
		
		for (int i = 0; i < baseIRI.length(); i++)
		{
			if (baseIRI.charAt(i) == '#')
			{
				baseIRI = baseIRI.substring(0, i);
			}
		}
		
		System.out.println("base IRI is [" + baseIRI + "]");
		
		return baseIRI;
	}
	
	/***
	 * Converts a given meme to a proper RDF for our Meme Ontology.
	 * It ensures that unassigned data is not included in the RDF.  
	 * @param aMeme a meme to convert into RDF(XML).
	 * @return a string in RDF(XML) format for the specified meme
	 */
	public static String memeToRDF(Meme aMeme)
	{
		StringBuilder builder = new StringBuilder();
		
		// Remove spaces, keep it alphanumeric with underscore only to fit the RDF's XML
//		String modifiedName = removeSpaces(sanitize(aMeme.getName()));
		
//		String modifiedName = 
		
		// Start the XML with the name of the meme
		builder.append(String.format(
				"<owl:NamedIndividual rdf:about=\"http://erau-semantic-research.com/2019/memo/0.1/%sMeme\">\n",
				mintIRI(aMeme.getMemeURL())));
		
		// The type of resource of the entry (a meme)
		builder.append(String.format(
				"  <rdf:type rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/Meme\"/>\n"));
		
		// Meme name
		builder.append(String.format(
				//  rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\"
				"  <rdfs:label xml:lang=\"en\">%s</rdfs:label>\n",
				sanitize(aMeme.getName())));
		
		// Meme URL
		builder.append(String.format(
				//  rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\"
				"  <memeURL>\"%s\"</memeURL>\n",
				aMeme.getMemeURL()));
		
		// Syntax Tags (called Categories)
		for(String syntaxTag : aMeme.getCategories())
		{
			String modifiedCategory = removeSpaces(sanitize(syntaxTag));
			builder.append(String.format("  <hasCategory rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/%sCategory\"/>\n",
					sanitize(modifiedCategory)));
		}
		
		// Check if we have a type (empty string is unassigned)
//		if(!aMeme.getType().isEmpty())
//		{
			String type = sanitize(aMeme.getType());
			builder.append(String.format(
					"  <hasMemeType rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/%sMemeType\"/>\n",
					type));
//		}
		
		// Check if we have a content origin (empty string is unassigned)
		for(String contentOrigin : aMeme.getContentOrigin())
		{
			String modifiedContentOrigin = sanitize(contentOrigin);
			builder.append(String.format(
					// rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"
					"  <contentOrigin>%s</contentOrigin>\n",
					modifiedContentOrigin));
		}
		
		// Check if we have a valid year (-1 is unassigned)
		if(aMeme.getOriginYear() != -1)
		{
			builder.append(String.format(
					//  rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\"
					"  <contentYear>%d</contentYear>\n",
					aMeme.getOriginYear()));
		}
		
		
		// Check if we have a meme origin (empty string is unassigned)
		if(!aMeme.getMemeOrigin().isEmpty())
		{
			String memeOrigin = sanitize(aMeme.getMemeOrigin());
			builder.append(String.format(
					//  rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"
					"  <memeOrigin>%s</memeOrigin>\n",
					memeOrigin));
		}
		
		// Check if we have a valid year (-1 is unassigned)
		if(aMeme.getMemeYear() != -1)
		{
			builder.append(String.format(
					//  rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\"
					"  <memeYear>%d</memeYear>\n",
					aMeme.getMemeYear()));
		}
		
		// Semantic Tags (called Tags)
		for(String semanticTag : aMeme.getTags())
		{
			String modifiedTag = sanitize(semanticTag);
			builder.append(String.format(
					// rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"
					"  <tag>%s</tag>\n",
					sanitize(modifiedTag)));
		}
		
		// new
		// Meme body text
		builder.append(String.format(
				//  rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\"
				"  <textRepresentation>%s</textRepresentation>\n",
				sanitize(aMeme.getMemeText())));
		// Meme image URLs
		for(String url : aMeme.getImageLinks())
		{
			builder.append(String.format(
					// rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"
					"  <memeImage>%s</memeImage>\n",
					url));
		}
		// Related Meme URLs
		for(String url : aMeme.getLinksInMemeText())
		{
			builder.append(String.format(
					// rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"
					"  <relatedMeme>http://erau-semantic-research.com/2019/memo/0.1/%sMeme</relatedMeme>\n",
					mintIRI(url)));
		}
		
		// isDefinedBy
		builder.append(String.format(
				// rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\"
				"  <rdfs:isDefinedBy rdf:resource=\"http://erau-semantic-research.com/2019/memo/0.1/\"/>\n"));
		
		// End XML element
		builder.append("</owl:NamedIndividual>");
		
		return builder.toString();
	}
	
	/***
	 * Converts a given meme array to a proper RDF for our Meme Ontology.
	 * It ensures that unassigned data is not included in the RDF.  
	 * @param memes an array of memes to convert into RDF
	 * @return a string in RDF(XML) format for the specified meme
	 */
	public static String memesToRDF(Meme... memes)
	{
		StringBuilder builder = new StringBuilder();

		// Adds each meme sequentially, then separates by 2 empty lines
		for(int i = 0; i < memes.length; i++)
		{
			Meme meme = memes[i];
			builder.append(MemeConverter.memeToRDF(meme)); // has \n at end by default
			
			// Add new lines only if not the last element
			if(i != memes.length - 1)
			{
				builder.append("\n\n");
			}
		}
		
		return builder.toString();
	}
	
	private static String removeSpaces(String s)
	{
		return s.replaceAll("\\s", "").trim();
	}
	
	private static String onlyAlphanumeric(String s)
	{
		return s.replaceAll("[^a-zA-Z0-9_ ]", "").trim();
	}
	
	private static String fixAmpersand(String s)
	{
		return s.replaceAll("&", " and ").trim();
	}
	
	private static String removeHTML(String s)
	{
		int leftAnglePosition = -1;
		int rightAnglePosition = -1;
		String result = s;
		
		do
		{
			leftAnglePosition = result.indexOf("<");
			rightAnglePosition = result.indexOf(">");
			
			// In a perfect world, where each < is happily married to >
			// And, the law states < always precedes > because ladies first.
			// If this law is broken, the monster will prevail and consume us all
			// His name? Deathlord Eclipserino
			if(leftAnglePosition != -1 && rightAnglePosition != -1)
			{
				String htmlPortion = result.substring(leftAnglePosition, rightAnglePosition+1);
				result = result.replace(htmlPortion, "");
			}
		}
		while(leftAnglePosition != -1);
		
		return result;
	}
	
	private static String sanitize(String s)
	{
		return removeHTML(fixAmpersand(onlyAlphanumeric(s)));
	}
	
	private static final String ONTOLOGY_IRI = "http://erau-semantic-research.com/2019/memo/0.1/";
	
	
	// new
	// Meh, CSV can suck my balls, Kyle
	/***
	 * Converts a given meme to comma-separated values file (CSV).
	 * It will export both assigned and unassigned data to the CSV.
	 * @param aMeme a meme to convert into CSV
	 * @return a string in CSV format for the specified meme
	 */
	public static String memeToCSV(Meme aMeme)
	{
		StringBuilder builder = new StringBuilder();
		
		// Column Order: Name,Type,ContentOrigin,OriginYear,MemeOrigin,MemeYear,Categories,Tags
		
		// Name
		// Remove spaces, keep it alphanumeric with underscore only to fit the CSV
		String modifiedName = removeSpaces(sanitize(aMeme.getName()));
		builder.append(modifiedName);
		builder.append(",");
		
		// Type
		// Check if we have a type (empty string is unassigned)
		if(!aMeme.getType().isEmpty())
		{
			String type = sanitize(aMeme.getType());
			builder.append(type);
		}
		builder.append(",");
		
		// Content Origin (comma separated so needs quotation to not break column order)
		builder.append("\"");
		for(int i = 0; i < aMeme.getContentOrigin().length; i++)
		{
			// Remove spaces and double quotation marks
			String contentOrigin = aMeme.getContentOrigin()[i];
			String modifiedContentOrigin = sanitize(contentOrigin); 
			builder.append(modifiedContentOrigin);
			
			if(i != aMeme.getContentOrigin().length - 1) // if not last element, add a comma
			{
				builder.append(",");
			}
		}
		builder.append("\"");
		builder.append(",");
		
		// Origin Year
		// Check if we have a valid year (-1 is unassigned)
		if(aMeme.getOriginYear() != -1)
		{
			builder.append(aMeme.getOriginYear());
		}
		builder.append(",");
		
		// Meme Origin
		// Check if we have a meme origin (empty string is unassigned)
		if(!aMeme.getMemeOrigin().isEmpty())
		{
			String memeOrigin = sanitize(aMeme.getMemeOrigin());
			builder.append(memeOrigin);
		}
		builder.append(",");
		
		// Meme Year
		// Check if we have a valid year (-1 is unassigned)
		if(aMeme.getMemeYear() != -1)
		{
			builder.append(aMeme.getMemeYear());
		}
		builder.append(",");
		
		// Categories (syntax tags) (comma separated so needs quotation to not break column order)
		builder.append("\"");
		for(int i = 0; i < aMeme.getCategories().length; i++)
		{
			String category = aMeme.getCategories()[i];
			String modifiedCategory = removeSpaces(sanitize(category));
			builder.append(modifiedCategory);
			
			if(i != aMeme.getCategories().length - 1) // if not last element, add a comma
			{
				builder.append(",");
			}
		}
		builder.append("\"");
		builder.append(",");
		
		// Tags (semantic tags) (comma separated so needs quotation to not break column order)
		builder.append("\"");
		for(int i = 0; i < aMeme.getTags().length; i++)
		{
			String tag = aMeme.getTags()[i];
			String modifiedTag = sanitize(tag);
			builder.append(modifiedTag);
			
			if(i != aMeme.getTags().length - 1) // if not last element, add a comma
			{
				builder.append(",");
			}
		}
		builder.append("\"");
		builder.append("\n");
		
		return builder.toString();
	}
	
	/***
	 * Converts a given meme array to comma-separated values file (CSV).
	 * It will export both assigned and unassigned data to the CSV.
	 * @param memes an array of memes to convert into CSV
	 * @return a string in CSV format for the specified memes
	 */
	public static String memesToCSV(Meme... memes)
	{
		StringBuilder builder = new StringBuilder();
		
		// First line consists of headers
		builder.append("Name,Type,ContentOrigin,OriginYear,MemeOrigin,MemeYear,Categories,Tags\n");
		
		// Add each meme sequentially
		for(Meme meme : memes)
		{
			builder.append(MemeConverter.memeToCSV(meme)); // has \n at end by default
		}
		
		return builder.toString();
	}
}