package io.rapid;

/**
 * Created by Leos on 22.03.2017.
 */

class EntityOrderDetail
{
	private String mProperty;
	private Sorting mSorting;


	public EntityOrderDetail(String property, Sorting sorting)
	{
		mProperty = property;
		mSorting = sorting;
	}


	public String getProperty()
	{
		return mProperty;
	}


	public void setProperty(String property)
	{
		mProperty = property;
	}


	public Sorting getSorting()
	{
		return mSorting;
	}


	public void setSorting(Sorting sorting)
	{
		mSorting = sorting;
	}
}