import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
import gc


# De-serialize static model.pkl file into an object called kmeans using pickle
with open('model_total.pkl', 'rb') as model:
    kmeans = pickle.load(model)

smallest_cluster = 1001
smallest = float('inf')

for i in range(1000):
    print("Cluster #", i, ": ")
    cluster = np.where(kmeans.labels_ == i)[0]
    size = cluster.size
    print("Cluster Size: ", cluster.size)
    print(cluster)
    if size <= smallest:
    	smallest = cluster.size
    	smallest_cluster = i
    cluster = None
    gc.collect()

print("Smallest Cluster #", smallest_cluster, ": ")
print("Size: ", smallest)
print(np.where(kmeans.labels_ == smallest_cluster)[0])