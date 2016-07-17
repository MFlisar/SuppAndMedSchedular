package com.prom.suppandmedschedular.helper.classes;

import java.util.ArrayList;
import java.util.List;

import com.prom.suppandmedschedular.db.classes.SubstanceToTake;

public class Link {

	private int parentIndex;
	private List<Integer> childrenIndizes;
	
	public Link(int parentIndex)
	{
		this.parentIndex = parentIndex;
		childrenIndizes = new ArrayList<Integer>();
	}
	
	public void addChildIfNotExists(Integer index)
	{
		if (!childrenIndizes.contains(index))
			childrenIndizes.add(index);
	}
	
	public void addChildrenIfNotExists(List<Integer> indices)
	{
		for (int i = 0; i < indices.size(); i++)
			if (!childrenIndizes.contains(indices.get(i)))
				childrenIndizes.add(indices.get(i));
	}
	
	public void removeChild(Integer index)
	{
		childrenIndizes.remove(index);
	}
	
	public void removeChildAt(int index)
	{
		childrenIndizes.remove(index);
	}
	
	public boolean containsChild(Integer index)
	{
		return childrenIndizes.contains(index);
	}
	
	public int getCountChildren()
	{
		return childrenIndizes.size();
	}
	
	public Integer getChildAt(int index)
	{
		return childrenIndizes.get(index);
	}
	
	public int getParent() {
		return parentIndex;
	}

	public List<Integer> getChildrenIndizes() {
		return childrenIndizes;
	}
	
	public String getChildrenString(List<SubstanceToTake> substances)
	{
		String value = "";
		for (int i = 0; i < childrenIndizes.size(); i++)
		{
			if (i > 0)
				value += ", ";
			value += substances.get(childrenIndizes.get(i)).getSubstance().getName();
		}
		return value;
	}
}
