import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from mpl_toolkits.mplot3d import Axes3D

# De-serialize static model.pkl file into an object called kmeans using pickle
with open('model_10k.pkl', 'rb') as model:
    kmeans = pickle.load(model)

smallest_cluster = 420
smallest = 10000
for i in range(100):
    print("Cluster #", i, ": ")
    cluster = np.where(kmeans.labels_ == i)[0]
    print(cluster)
    if len(cluster) < smallest:
    	smallest = cluster
    	smallest_cluster = i

print("Smallest Cluster #", smallest_cluster, ": ")
print(smallest)