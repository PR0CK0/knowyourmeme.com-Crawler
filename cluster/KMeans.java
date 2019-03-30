package cluster;

import java.util.Arrays;

public class KMeans
{
	public KMeans(int clusterCount, int dimensions, Vector[] points)
	{
		clusters = new Cluster[clusterCount];
		
		for(int i = 0; i < clusterCount; i++)
		{
			clusters[i] = new Cluster(dimensions);
		}
		
		this.points = points;
		initializeClusterLocations();
	}
	
	public void update()
	{
		// Remove all points from all clusters (can be optimized to track deltas)
		for(Cluster cluster : clusters)
		{
			cluster.getPoints().clear();
		}
		
		// Update the points to belong to its nearest cluster
		for(Vector point : points)
		{
			Cluster nearest = getNearestCluster(point);
			nearest.getPoints().add(point);
		}
		
		// Update the clusters to be center of its data points
		for(Cluster cluster : clusters)
		{
			cluster.updatePosition();
		}
	}
	
	public double getError()
	{
		// Error = sum of the errors of all clusters
		double error = 0;
		
		for(Cluster cluster : clusters)
		{
			error += cluster.getError();
		}
		
		return error;
	}
	
	public Cluster getNearestCluster(Vector point)
	{
		Cluster nearest = null;
		double nearestDistance = Double.MAX_VALUE;
		
		for(Cluster cluster : clusters)
		{
			double distance = Vector.getDistanceSqrd(point, cluster.getPosition());
			if(nearest == null || distance < nearestDistance)
			{
				nearest = cluster;
				nearestDistance = distance;
			}
		}
		
		return nearest;
	}

	public Cluster[] getClusters()
	{
		return clusters;
	}
	
	public Vector[] getPoints()
	{
		return points;
	}
	
	@Override
	public String toString()
	{
		return "KMeans [points=" + Arrays.toString(points) + ", clusters=" + Arrays.toString(clusters) + "]";
	}

	private void initializeClusterLocations()
	{
		// Each cluster will start on a random point in our data-set
		for(Cluster cluster : clusters)
		{
			cluster.getPosition().copyValues(getRandomPoint());
		}
	}
	
	private Vector getRandomPoint()
	{
		int randIndex = (int) (Math.random() * points.length);
		return points[randIndex];
	}
	
	private final Vector[] points;
	private final Cluster[] clusters;
}