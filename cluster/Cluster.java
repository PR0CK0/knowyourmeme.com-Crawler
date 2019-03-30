package cluster;

import java.util.ArrayList;
import java.util.List;

public class Cluster
{
	public Cluster(int dimensions)
	{
		points = new ArrayList<>();
		position = new Vector(dimensions);
	}
	
	public void updatePosition()
	{
		// Reset position to 0 on all dimensions
		position.set(0);
		
		// If there are no points, then the position should stop at 0 for all dimensions
		if(points.size() == 0)
			return;
		
		// Sum all points on all dimensions
		for(Vector point : points)
		{
			position.add(point);
		}
		
		// Average the position to get the mean
		position.divide(points.size());
	}
	
	public double getError()
	{
		// Error = sum of the distance to each point
		double error = 0;
		
		for(Vector point : points)
		{
			error += Vector.getDistance(position, point);
		}
		
		return error;
	}
	
	public Vector getFurthestPoint()
	{
		Vector furthest = null;
		double furthestDistance = Double.MIN_VALUE;
		
		for(Vector point : points)
		{
			double distance = Vector.getDistanceSqrd(point, position);
			if(furthest == null || distance < furthestDistance)
			{
				furthest = point;
				furthestDistance = distance;
			}
		}
		
		return furthest;
	}
	
	public List<Vector> getPoints()
	{
		return points;
	}
	
	public Vector getPosition()
	{
		return position;
	}
	
	@Override
	public String toString()
	{
		return "Cluster [position=" + position + "]";
	}

	private final List<Vector> points;
	private final Vector position;
}