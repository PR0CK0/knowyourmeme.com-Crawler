# Purpose
This section is dedicated to the Clustering part of the project. After the memes have been crawled and vectorized via RDF2Vec, the vector representations are passed into a clustering algorithm such as K-Means. This will group up "close" instances based on Euclidean distances (for any number of dimensions). The number of groups, or clusters, can be determined or automatically inferred by trying all and using the one with the most effective grouping.

## K-Means Algorithm Breakdown

### Number of Clusters
First, you need to define k, which is the number of clusters. 
There are ways to determine this number without human interaction by a method called "The Elbow Method", which in a nutshell, tries different k values and chooses the one with the least error (Error can be the sum of the distance to each datapoint for each cluster). Minimizing this value (error), would mean that the cluster are well placed and are in dense locations.

### Initial Clusters Position
After defining k, we start the algorithm by placing our clusters.
Methods of placing the initial clusters:
1. Placed in random locations. (any x,y,z)
2. Placed on random data-points.

### Clustering Loop
After placing your clusters, you iteratively do the following for each data point:
* Find the nearest cluster to this data-point by computing the Euclidian distance to each cluster.
* Set this data-point to belong to that cluster.

For each cluster:
* Calculate its 'mean' (hence the algorithms name, K-Mean) of the data-points location that belong to it.
..* Basically, finding the center of the data-points assigned to this cluster.

### Termination Condition
Repeat this process described in the Clustering Loop until:
* Specific number of iterations have been reached
* The clusters have not moved