package cluster;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Vector
{
	public Vector(int dimensions)
	{
		values = new double[dimensions];
	}
	
	public Vector(Vector other)
	{
		values = new double[other.getDimensions()];
		copyValues(other);
	}
	
	public Vector(double... values)
	{
		this.values = values;
	}
	
	public Vector add(Vector other)
	{
		processElementWise(other, (a, b) -> a+b);
		return this;
	}
	
	public Vector subtract(Vector other)
	{
		processElementWise(other, (a, b) -> a-b);
		return this;
	}
	
	public Vector multiply(Vector other)
	{
		processElementWise(other, (a, b) -> a*b);
		return this;
	}
	
	public Vector divide(Vector other)
	{
		processElementWise(other, (a, b) -> a/b);
		return this;
	}
	
	public Vector power(Vector other)
	{
		processElementWise(other, (a, b) -> Math.pow(a, b));
		return this;
	}
	
	public Vector min(Vector other)
	{
		processElementWise(other, (a, b) -> Math.min(a, b));
		return this;
	}
	
	public Vector max(Vector other)
	{
		processElementWise(other, (a, b) -> Math.max(a, b));
		return this;
	}
	
	public Vector copyValues(Vector other)
	{
		processElementWise(other, (a, b) -> b);
		return this;
	}
	
	public double getMagnitudeSqrd()
	{
		double magnitude = 0;
		for(int i = 0; i < getDimensions(); i++)
		{
			magnitude += values[i] * values[i];
		}
		return magnitude;
	}
	
	public double getMagnitude()
	{
		return Math.sqrt(getMagnitudeSqrd());
	}
	
	public Vector add(double value)
	{
		applyForeach(element -> element + value);
		return this;
	}
	
	public Vector subtract(double value)
	{
		applyForeach(element -> element - value);
		return this;
	}
	
	public Vector multiply(double value)
	{
		applyForeach(element -> element * value);
		return this;
	}
	
	public Vector divide(double value)
	{
		applyForeach(element -> element / value);
		return this;
	}
	
	public Vector power(double value)
	{
		applyForeach(element -> Math.pow(element, value));
		return this;
	}
	
	public Vector set(double value)
	{
		applyForeach(element -> value);
		return this;
	}
	
	public int getDimensions()
	{
		return values.length;
	}
	
	public double[] getValues()
	{
		return values;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(values);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		Vector other = (Vector) obj;
		if (!Arrays.equals(values, other.values))
			return false;
		
		return true;
	}

	@Override
	public String toString()
	{
		return Arrays.toString(values);
	}
	
	public static double getDistanceSqrd(Vector a, Vector b)
	{
		if(a.getDimensions() != b.getDimensions())
		{
			throw new IllegalArgumentException("Failed to operate on vectors with different size.");
		}
		
		Vector delta = new Vector(a).subtract(b); // a-b
		return delta.getMagnitudeSqrd();
	}
	
	public static double getDistance(Vector a, Vector b)
	{
		return Math.sqrt(Vector.getDistanceSqrd(a, b));
	}

	private void processElementWise(Vector other, BiFunction<Double, Double, Double> operation)
	{
		if(getDimensions() != other.getDimensions())
		{
			throw new IllegalArgumentException("Failed to operate on vectors with different size.");
		}
		
		for(int i = 0; i < getDimensions(); i++)
		{
			values[i] = operation.apply(values[i], other.values[i]);
		}
	}
	
	private void applyForeach(Function<Double, Double> operation)
	{
		for(int i = 0; i < getDimensions(); i++)
		{
			values[i] = operation.apply(values[i]);
		}
	}
	
	private final double[] values;
}