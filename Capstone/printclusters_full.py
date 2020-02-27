import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from sklearn.preprocessing import OneHotEncoder
from os import listdir
from os.path import isfile, join
from mpl_toolkits.mplot3d import Axes3D

# Load Image Features - Running OpenCV Feature dectection over every file in given directory
mypath = "training_images"

d = []
key = []

# Open cascade classifier:
cascade = cv.CascadeClassifier('C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml')

# create facemark detector and load lbf model:
facemark = cv.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]
images = np.empty(len(onlyfiles), dtype=object)
for n in range(0, len(onlyfiles)):
    # Load Image
    images[n] = cv.imread( join(mypath,onlyfiles[n]) )
    imageName = str(n+1).zfill(6) + ".jpg"

    currentImage = []
    try:
        # Run landmark detector:
        faces = cascade.detectMultiScale(images[n], 1.3, 5)
        ok, landmarks = facemark.fit(images[n], faces)
        
        # Extract Landmark data 
        if ok:
            for marks in landmarks[0]:
                for mark in marks:
                    currentImage.append({mark[0],mark[1]})

            d.append(currentImage)
            key.append(imageName)
                        
        else:
            print("Landmark DETECTION failed on image: ", imageName)
    except:

        print("Landmark APPEND failed on image: ", imageName)


# Turn data into dataframe for kmeans
df = pd.DataFrame(d)
#print(df)

# Run Label Encoder Over the Data
print("Done loading images!")
for image in df:
    for point in df.iloc[image,:]:
        x = np.asarray(point)
        #le = LabelEncoder()
        print(type(x))
        print(x)
        #le.fit(x)
        #df.iloc[image,:] = le.transform(x)
        x = x.reshape(1,-1)
        print(type(x))
        print(x)
        hot = OneHotEncoder()
        df.iloc[image,:] = hot.fit_transform(x)

# Run K Means
kmeans = KMeans(n_clusters=20, n_init=20, precompute_distances='auto',verbose=1, algorithm='auto')
kmeans.fit(df)

labels = kmeans.predict(df)

# in d we store the original data, key has the name of the image
clusters = {}
n = 0
for item in labels:
    if item in clusters:
        clusters[item].append(d[n])
    else:
        clusters[item] = [key[n]]
    n +=1


for item in clusters:
    print("Cluster ", item)
    for i in clusters[item]:
        print(i)
        