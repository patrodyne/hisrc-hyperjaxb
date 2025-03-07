package org.jvnet.hyperjaxb.ejb.tests.floating;

import static java.lang.Double.parseDouble;

public class DoubleArrayAdapter
{
	public static double[][] unmarshal(byte[] doubleArrayBytes)
	{
		double[][] rowsArray = null;
		if ( doubleArrayBytes != null )
		{
			String doubleArrayText = new String(doubleArrayBytes).replaceAll("\\s+","");
			String[] rows = doubleArrayText.split(";");
			rowsArray = new double[rows.length][];
			for ( int rowIndex=0; rowIndex < rows.length; ++rowIndex )
			{
				String row = rows[rowIndex];
				if ( !row.isBlank() )
				{
					String[] cols = row.split(",");
					if ( cols.length > 0 )
					{
						double[] colsArray = new double[cols.length];
						for ( int colIndex=0; colIndex < cols.length; ++colIndex )
							colsArray[colIndex] = parseDouble(cols[colIndex]);
						rowsArray[rowIndex] = colsArray;
					}
				}
			}
		}
		return rowsArray;
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
