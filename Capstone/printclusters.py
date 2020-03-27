import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from mpl_toolkits.mplot3d import Axes3D

# De-serialize static model.pkl file into an object called kmeans using pickle
with open('model_50_clusters.pkl', 'rb') as model:
    kmeans = pickle.load(model)

for i in range(50):
    print("Cluster #", i, ": ")
    print(np.where(kmeans.labels_ == i)[0])