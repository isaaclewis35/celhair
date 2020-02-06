import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from os import listdir
from os.path import isfile, join

# Load Image Features - Running OpenCV Feature dectection over every file in given directory
mypath = "test_set"


# Creating dataframe
df = pd.DataFrame(columns=[])

# Open cascade classifier:
cascade = cv.CascadeClassifier('C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml')

# create facemark detector and load lbf model:
facemark = cv.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]
images = np.empty(len(onlyfiles), dtype=object)
for n in range(0, len(onlyfiles)):
    # Load Image
    print("Loading Features From images: ", str(n+1).zfill(6) + ".jpg")
    images[n] = cv.imread( join(mypath,onlyfiles[n]) )


    # Run landmark detector:
    faces = cascade.detectMultiScale(images[n], 1.3, 5)
    ok, landmarks = facemark.fit(images[n], faces)
    if(not ok):
        print("Landmark detection error on image: ", str(n+1).zfill(6) + ".jpg")


    print("Facial Landmarks:", landmarks)
    # Append Landmark data to dataframe
    df = df.append(landmarks)


# Run Label Encoder Over the Data
for i in df:
    le = LabelEncoder()

    le.fit(df[i])
    df[i] = le.transform(df[i])

# Run K Means
kmeans = KMeans(n_clusters=20, random_state=69, n_init=50, precompute_distances='auto', algorithm='auto').fit(df)
centroids = np.array(kmeans.cluster_centers_)

print("Centriods: ", centroids)

# Dump Model to Pickle File
with open('model_facemark.pkl', 'wb') as model_file:
  pickle.dump(kmeans, model_file, protocol=2)