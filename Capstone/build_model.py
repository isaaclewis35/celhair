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
#mypath = "test_set"
mypath = "training_images_large"

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
                    currentImage.append(np.array(mark[0],mark[1]))

            d.append(currentImage)
            key.append(imageName)
                        
        else:
            print("Landmark DETECTION failed on image: ", imageName)
    except:

        print("Landmark APPEND failed on image: ", imageName)


# Turn data into dataframe for kmeans
#df = pd.DataFrame(d)
df = np.array(d)
#print(df)


# Run K Means
kmeans = KMeans(n_clusters=20, n_init=20, precompute_distances='auto',verbose=1, algorithm='auto')
kmeans.fit(df)

# Dump Model to Pickle File
with open('model_facemark.pkl', 'wb') as model_file:
  pickle.dump(kmeans, model_file, protocol=2)