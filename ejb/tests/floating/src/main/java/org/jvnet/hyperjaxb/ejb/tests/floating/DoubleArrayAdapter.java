package org.jvnet.hyperjaxb.ejb.tests.floating;

import static java.lang.Double.parseDouble;

import java.util.ArrayList;
import java.util.List;

public class DoubleArrayAdapter
{
	public static double[][] unmarshal(byte[] doubleArrayBytes)
	{
		double[][] doubleArray = null;
		if ( doubleArrayBytes != null )
		{
			List<List<Double>> doubleArrayList = new ArrayList<>();
			String doubleArrayText = new String(doubleArrayBytes);
			for ( String row : doubleArrayText.split(";") )
			{
				if ( !row.isBlank() )
				{
					List<Double> doubleList = new ArrayList<>();
					for ( String cell : row.split(",") )
						doubleList.add(parseDouble(cell));
					doubleArrayList.add(doubleList);
				}
			}
			doubleArray = new double[doubleArrayList.size()][];
			for ( int row = 0; row < doubleArrayList.size(); ++row )
			{
				List<Double> cells = doubleArrayList.get(row);
				double[] cellsArray = new double[cells.size()];
				for ( int col=0; col < cells.size(); ++col )
					cellsArray[col] = cells.get(col);
				doubleArray[row] = cellsArray;
			}
		}
		return doubleArray;
	}
	
	public static byte[] marshal(double[][] doubleArray)
	{
		StringBuilder sb = new StringBuilder();
		for ( int rx=0; rx < doubleArray.length; ++rx )
		{
			double[] row = doubleArray[rx];
			for ( int cx=0; cx < row.length; ++cx )
			{
				if ( cx < row.length )
					sb.append(row[cx] + (cx < row.length-1 ? "," : ";"));
			}
		}
		return sb.toString().getBytes();
	}
}
