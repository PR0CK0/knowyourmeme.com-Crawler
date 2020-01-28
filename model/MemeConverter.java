package model;

public class MemeConverter
{	
	
	// I want to clean up the names, doesnt really matter though
	private static String mintIRI(String memeURL)
	{
		int loc = 0;
		
		try
		{			
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
			
			return baseIRI;
		}
		catch (Exception exc)
		{
			System.out.println(memeURL);
		}
		
		return "";
	}
	
	/***
	 * Converts a given meme to a proper RDF for our Meme Ontology.
	 * @param aMeme a meme to convert into RDF(XML).
	 * @return a string in RDF(XML) format for the specified meme
	 */
	public String memeToRDF(Meme aMeme)
	{
		StringBuilder builder = new StringBuilder();
		
		// Remove spaces, keep it alphanumeric with underscore only to fit the RDF's XML
//		String modifiedName = removeSpaces(sanitize(aMeme.getName()));
				
		writeMemeName(aMeme, builder);
		writeRDFsLabel(aMeme, builder);
		writeMemeCategory(aMeme, builder);
		writeMemeCategories(aMeme, builder);
		writeMemeContentOrigin(aMeme, builder);
		
		// Check if we have a valid year (-1 is unassigned)
//		if(aMeme.getOriginYear() != -1)
//		{
		writeMemeContentOriginYear(aMeme, builder);
//		}
		
		// Check if we have a meme origin (empty string is unassigned)
//		if(!aMeme.getMemeOrigin().isEmpty())
//		{
		writeMemeOrigin(aMeme, builder);
//		}
		
		// Check if we have a valid year (-1 is unassigned)
//		if(aMeme.getMemeYear() != -1)
//		{
		writeMemeOriginYear(aMeme, builder);
//		}
		
		writeMemeTags(aMeme, builder);
		writeMemeBodyText(aMeme, builder);
		writeMemeImageURLs(aMeme, builder);
		// TODO this broke
		writeRelatedMemes(aMeme, builder);
		writeMemeURL(aMeme, builder);
		writeIsDefinedBy(builder);
		
		// End XML element
		builder.append("</owl:NamedIndividual>");
		
		return builder.toString();
	}
		
	// new
	// Meh, CSV can suck my balls, Kyle
	/***
	 * Converts a given meme to comma-separated values file (CSV).
	 * It will export both assigned and unassigned data to the CSV.
	 * @param aMeme a meme to convert into CSV
	 * @return a string in CSV format for the specified meme
	 */
	public String memeToCSV(Meme aMeme)
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
	public String memesToCSV(Meme... memes)
	{
		StringBuilder builder = new StringBuilder();
		
		// First line consists of headers
		builder.append("Name,Type,ContentOrigin,OriginYear,MemeOrigin,MemeYear,Categories,Tags\n");
		
		// Add each meme sequentially
		for(Meme meme : memes)
		{
			builder.append(memeToCSV(meme)); // has \n at end by default
		}
		
		return builder.toString();
	}
	
	private static final String ONTOLOGY_IRI = "http://erau-semantic-research.com/2020/memo/1.0/";
	
	private void writeIsDefinedBy(StringBuilder builder) 
	{
		builder.append(String.format(
				"  <rdfs:isDefinedBy rdf:resource=\""+ ONTOLOGY_IRI + "\"/>\n"));
	}

	private void writeRelatedMemes(Meme aMeme, StringBuilder builder) 
	{
		for(String url : aMeme.getLinksInMemeText())
		{
			System.out.println(aMeme.getName());
			
			// TODO changed to rdf resource
			builder.append(String.format(
					"  <relatedMeme rdf:resource=\"" + ONTOLOGY_IRI + "%sMeme\"/>\n",
					mintIRI(url)));
		}
	}

	private void writeMemeImageURLs(Meme aMeme, StringBuilder builder)
	{
		for(String url : aMeme.getImageLinks())
		{
			// ampersand in owl breaks parser
			if (url.contains("&"))
			{
				continue;
			}
			
			builder.append(String.format(
					"  <memeImage>\"%s\"</memeImage>\n",
					url));
		}
	}

	private void writeMemeBodyText(Meme aMeme, StringBuilder builder)
	{
		builder.append(String.format(
				"  <textRepresentation>%s</textRepresentation>\n",
				sanitize(aMeme.getMemeText())));
	}

	private void writeMemeTags(Meme aMeme, StringBuilder builder)
	{
		for(String semanticTag : aMeme.getTags())
		{
			String modifiedTag = sanitize(semanticTag);
			builder.append(String.format(
					"  <tag>%s</tag>\n",
					sanitize(modifiedTag)));
		}
	}

	private void writeMemeOriginYear(Meme aMeme, StringBuilder builder) 
	{
		builder.append(String.format(
				"  <memeYear>%d</memeYear>\n",
				aMeme.getMemeYear()));
	}

	private void writeMemeOrigin(Meme aMeme, StringBuilder builder)
	{
		String memeOrigin = sanitize(aMeme.getMemeOrigin());
		builder.append(String.format(
				"  <memeOrigin>%s</memeOrigin>\n",
				memeOrigin));
	}

	private void writeMemeContentOriginYear(Meme aMeme, StringBuilder builder) 
	{
		builder.append(String.format(
					"  <contentYear>%d</contentYear>\n",
					aMeme.getOriginYear()));
	}

	private void writeMemeContentOrigin(Meme aMeme, StringBuilder builder) 
	{
		for(String contentOrigin : aMeme.getContentOrigin())
		{
			String modifiedContentOrigin = sanitize(contentOrigin);
			builder.append(String.format(
					"  <contentOrigin>%s</contentOrigin>\n",
					modifiedContentOrigin));
		}
	}

	private void writeMemeCategories(Meme aMeme, StringBuilder builder) 
	{
		for(String syntaxTag : aMeme.getCategories())
		{
			String modifiedCategory = removeSpaces(sanitize(syntaxTag));
			builder.append(String.format("  <hasCategorySpecification rdf:resource=\"http://erau-semantic-research.com/2020/memo/1.0/%sCategorySpecification\"/>\n",
					sanitize(modifiedCategory)));
		}
	}

	private void writeMemeURL(Meme aMeme, StringBuilder builder) 
	{
		builder.append(String.format(
				"  <memeURL>\"%s\"</memeURL>\n",
				aMeme.getMemeURL()));
	}

	private void writeRDFsLabel(Meme aMeme, StringBuilder builder) 
	{
		builder.append(String.format(
				"  <rdfs:label xml:lang=\"en\">%s</rdfs:label>\n",
				sanitize(aMeme.getName())));
	}

	private static void writeMemeName(Meme aMeme, StringBuilder builder) 
	{
		builder.append(String.format(
				"<owl:NamedIndividual rdf:about=\""+ ONTOLOGY_IRI + "%sMeme\">\n",
				mintIRI(aMeme.getMemeURL())));
	}

	private void writeMemeCategory(Meme aMeme, StringBuilder builder) 
	{
		final String prefix = "  <rdf:type rdf:resource=\"" + ONTOLOGY_IRI;
		
		if (aMeme.getMemeURL().contains("cultures"))
		{
			builder.append(String.format(
					prefix + "CultureMemeCategory\"/>\n"));
		}
		else if (aMeme.getMemeURL().contains("events"))
		{
			builder.append(String.format(
					prefix + "EventMemeCategory\"/>\n"));
		}
		else if (aMeme.getMemeURL().contains("people"))
		{
			builder.append(String.format(
					prefix + "PeopleMemeCategory\"/>\n"));
		}
		else if (aMeme.getMemeURL().contains("sites"))
		{
			builder.append(String.format(
					prefix + "SiteMemeCategory\"/>\n"));
		}
		else if (aMeme.getMemeURL().contains("sites"))
		{
			builder.append(String.format(
					prefix + "SubcultureMemeCategory\"/>\n"));
		}
		else
		{
			builder.append(String.format(
					prefix + "MemeMemeCategory\"/>\n"));
		}
	}
	
	/***
	 * Converts a given meme array to a proper RDF for our Meme Ontology.
	 * It ensures that unassigned data is not included in the RDF.  
	 * @param memes an array of memes to convert into RDF
	 * @return a string in RDF(XML) format for the specified meme
	 */
	public String memesToRDF(Meme... memes)
	{
		StringBuilder builder = new StringBuilder();

		// Adds each meme sequentially, then separates by 2 empty lines
		for(int i = 0; i < memes.length; i++)
		{
			Meme meme = memes[i];
			builder.append(memeToRDF(meme)); // has \n at end by default
			
			// Add new lines only if not the last element
			if(i != memes.length - 1)
			{
				builder.append("\n\n");
			}
		}
		
		return builder.toString();
	}
	
	private String removeSpaces(String s)
	{
		return s.replaceAll("\\s", "").trim();
	}
	
	private String onlyAlphanumeric(String s)
	{
		return s.replaceAll("[^a-zA-Z0-9_ ]", "").trim();
	}
	
	private String fixAmpersand(String s)
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
	
	private String sanitize(String s)
	{
		return removeHTML(fixAmpersand(onlyAlphanumeric(s)));
	}
}

// Check if we have a type (empty string is unassigned)
//if(!aMeme.getType().isEmpty())
//{
//	String type = sanitize(aMeme.getType());
//	builder.append(String.format(
//			"  <hasMemeType rdf:resource=\"http://erau-semantic-research.com/2020/memo/1.0/%sMemeType\"/>\n",
//			type));
//}