package model;

public class MemeConverter
{
	// Disable instantiation (just like math classes)
	private MemeConverter() {}
	
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
		String modifiedName = removeSpaces(sanitize(aMeme.getName()));
		
		// Start the XML with the name of the meme
		builder.append(String.format(
				"<owl:NamedIndividual rdf:about=\"%s#%sMeme\">\n",
				ONTOLOGY_IRI,
				modifiedName));
		
		// The type of resource of the entry (a meme)
		builder.append(String.format(
				"\t<rdf:type rdf:resource=\"%s#Meme\"/>\n",
				ONTOLOGY_IRI));
		
		// Syntax Tags (called Categories)
		for(String syntaxTag : aMeme.getCategories())
		{
			String modifiedCategory = removeSpaces(sanitize(syntaxTag));
			builder.append(String.format("\t<syntaxTag rdf:resource=\"%s#%sSyntaxTag\"/>\n",
					ONTOLOGY_IRI,
					modifiedCategory));
		}
		
		// Check if we have a type (empty string is unassigned)
		if(!aMeme.getType().isEmpty())
		{
			String type = sanitize(aMeme.getType());
			builder.append(String.format(
					"\t<memeType rdf:resource=\"%s#%sType\"/>\n",
					ONTOLOGY_IRI,
					type));
		}
		
		// Check if we have a content origin (empty string is unassigned)
		for(String contentOrigin : aMeme.getContentOrigin())
		{
			String modifiedContentOrigin = sanitize(contentOrigin);
			builder.append(String.format(
					"\t<contentOrigin rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">%s</contentOrigin>\n",
					modifiedContentOrigin));
		}
		
		// Check if we have a valid year (-1 is unassigned)
		if(aMeme.getOriginYear() != -1)
		{
			builder.append(String.format(
					"\t<contentYear rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\">%d</contentYear>\n",
					aMeme.getOriginYear()));
		}
		
		
		// Check if we have a meme origin (empty string is unassigned)
		if(!aMeme.getMemeOrigin().isEmpty())
		{
			String memeOrigin = sanitize(aMeme.getMemeOrigin());
			builder.append(String.format(
					"\t<memeOrigin rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">%s</memeOrigin>\n",
					memeOrigin));
		}
		
		// Check if we have a valid year (-1 is unassigned)
		if(aMeme.getMemeYear() != -1)
		{
			builder.append(String.format(
					"\t<memeYear rdf:datatype=\"http://www.w3.org/2001/XMLSchema#positiveInteger\">%d</memeYear>\n",
					aMeme.getMemeYear()));
		}
		
		// Semantic Tags (called Tags)
		for(String semanticTag : aMeme.getTags())
		{
			String modifiedTag = sanitize(semanticTag);
			builder.append(String.format(
					"\t<semanticTag rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">%s</semanticTag>\n",
					modifiedTag));
		}
		
		// End XML element
		builder.append("</owl:NamedIndividual>\n");
		
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
	
	private static final String ONTOLOGY_IRI = "http://erau.edu/ontology/meme.owl";
}
