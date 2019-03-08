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
		String modifiedName = aMeme.getName().replaceAll("[^a-zA-Z0-9_ ]", "").replaceAll("\\s", "").trim();
		builder.append(modifiedName);
		builder.append(",");
		
		// Type
		builder.append(aMeme.getType());
		builder.append(",");
		
		// Content Origin (comma separated so needs quotation to not break column order)
		builder.append("\"");
		for(int i = 0; i < aMeme.getContentOrigin().length; i++)
		{
			String contentOrigin = aMeme.getContentOrigin()[i];
			builder.append(contentOrigin);
			if(i != aMeme.getContentOrigin().length - 1) // if not last element, add a comma
			{
				builder.append(",");
			}
		}
		builder.append("\"");
		builder.append(",");
		
		// Origin Year
		builder.append(aMeme.getOriginYear());
		builder.append(",");
		
		// Meme Origin
		builder.append(aMeme.getMemeOrigin());
		builder.append(",");
		
		// Meme Year
		builder.append(aMeme.getMemeYear());
		builder.append(",");
		
		// Categories (syntax tags) (comma separated so needs quotation to not break column order)
		builder.append("\"");
		for(int i = 0; i < aMeme.getCategories().length; i++)
		{
			String category = aMeme.getCategories()[i];
			builder.append(category);
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
			builder.append(tag);
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
		String modifiedName = aMeme.getName().replaceAll("[^a-zA-Z0-9_ ]", "").replaceAll("\\s", "").trim();
		
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
			builder.append(String.format("\t<syntaxTag rdf:resource=\"%s#%sSyntaxTag\"/>\n", ONTOLOGY_IRI, syntaxTag));
		}
		
		// Check if we have a type (empty string is unassigned)
		if(!aMeme.getType().isEmpty())
		{
			builder.append(String.format(
					"\t<memeType rdf:resource=\"%s#%sType\"/>\n", ONTOLOGY_IRI,
					aMeme.getType()));
		}
		
		// Check if we have a content origin (empty string is unassigned)
		for(String contentOrigin : aMeme.getContentOrigin())
		{
			builder.append(String.format(
					"\t<contentOrigin rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">%s</contentOrigin>\n",
					contentOrigin));
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
			builder.append(String.format(
					"\t<memeOrigin rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">%s</memeOrigin>\n",
					aMeme.getMemeOrigin()));
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
			builder.append(String.format(
					"\t<semanticTag rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">%s</semanticTag>\n",
					semanticTag));
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
	
	private static final String ONTOLOGY_IRI = "http://erau.edu/ontology/meme.owl";
}
