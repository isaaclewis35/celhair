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
#mypath = "test_set"
mypath = "training_images_large"

d = []
# Open cascade classifier:
cascade = cv.CascadeClassifier('C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml')
#cascade = cv.CascadeClassifier('C:/OpenCV/sources/data/lbpcascades/lbpcascade_frontalface_improved.xml')

# create facemark detector and load lbf model:
facemark = cv.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]
images = np.empty(len(onlyfiles), dtype=object)
for n in range(0, len(onlyfiles)):
    # Load Image
    images[n] = cv.imread( join(mypath,onlyfiles[n]) )
    imageName = str(n+1).zfill(6) + ".jpg"

    try:
        # Run landmark detector:
        faces = cascade.detectMultiScale(images[n], 1.3, 5)
        ok, landmarks = facemark.fit(images[n], faces)
        # Extract Landmark data 
        if ok:
            for marks in landmarks[0]:
                for mark in marks:
                    d.append({'X': mark[0], 'Y': mark[1]})
                        
        else:
            print("Landmark DETECTION failed on image: ", imageName)
    except:
        print("Landmark APPEND failed on image: ", str(n+1).zfill(6) + ".jpg")

# Turn data into dataframe for kmeans
df = pd.DataFrame(d)

# Run Label Encoder Over the Data
print("Done loading images!")
for i in df:
    le = LabelEncoder()
    le.fit(df[i])
    df[i] = le.transform(df[i])

# Run K Means
kmeans = KMeans(n_clusters=20, random_state=69, n_init=50, precompute_distances='auto', algorithm='auto').fit(df)
centroids = np.array(kmeans.cluster_centers_)


# Dump Model to Pickle File
with open('model_facemark_5k_noname.pkl', 'wb') as model_file:
  pickle.dump(kmeans, model_file, protocol=2)