package model;

import java.util.Arrays;

public class Meme
{	
	@Override
	public String toString()
	{
		return  "Name = " + name +
				"\nOrigin Year = " + contentYear + 
				"\nContent Origin = " + Arrays.toString(contentOrigin) +
				"\nTags = " + Arrays.toString(tags) +
				"\nCategories = " + Arrays.toString(categories) +
				"\nMeme Year = " + memeYear +
				"\nMeme Origin = " + memeOrigin +
				"\nType = " + type;
	}

	public int getMemeYear() 
	{
		return memeYear;
	}

	public void setMemeYear(int memeYear) 
	{
		this.memeYear = memeYear;
		
		// TEMPORARY: Deduce type from meme year when supplied
		if(memeYear >= 2012)
		{
			setType("Dank");
		}
		else
		{
			setType("Classic");
		}
	}

	public String getMemeOrigin() 
	{
		return memeOrigin;
	}

	public void setMemeOrigin(String memeOrigin) 
	{
		this.memeOrigin = memeOrigin;
	}

	public String[] getTags()
	{
		return tags;
	}

	public String[] getCategories() 
	{
		return categories;
	}

	public String getName()
	{
		return name;
	}
	
	public int getOriginYear()
	{
		return contentYear;
	}
	
	public String[] getContentOrigin()
	{
		return contentOrigin;
	}
	
	public String getType()
	{
		return type;
	}
	
	public void setTags(String[] tags)
	{
		this.tags = tags;
	}
	
	public void setCategories(String[] categories) 
	{
		this.categories = categories;
	}

	public void setName(String aName)
	{
		name = aName;
	}
	
	public void setOriginYear(int year)
	{
		contentYear = year;
	}

	public void setContentOrigin(String[] anOrigin)
	{
		contentOrigin = anOrigin;
	}
	
	public void setType(String aType)
	{
		type = aType;
	}
	
	private String name = "";
	private String[] categories = {};
	private int contentYear = -1;
	private String[] contentOrigin = {};
	private int memeYear = -1;
	private String memeOrigin = "";
	private String[] tags = {};
	private String type = "";
}