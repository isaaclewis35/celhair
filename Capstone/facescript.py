import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from os import listdir
from os.path import isfile, join
from IPython.display import display, HTML

# Load Image Features - Running OpenCV Feature dectection over every file in given directory
mypath = "test_set"


# X/Y Coords of 20 features for ORB detector
df = pd.DataFrame(columns=['X_1','X_2','X_3','X_3','X_4','X_5','X_6','X_7','X_8','X_9','X_10','X_11','X_12','X_13','X_14','X_15','X_16','X_17','X_18','X_19','X_20','Y_1','Y_2','Y_3','Y_3','Y_4','Y_5','Y_6','Y_7','Y_8','Y_9','Y_10','Y_11','Y_12','Y_13','Y_14','Y_15','Y_16','Y_17','Y_18','Y_19','Y_20']) 
detector = cv.ORB_create(nfeatures=20) # Feature Detector

# create facemark detector and load lbf model:
#facemark = cv2.face.createFacemarkLBF()
#facemark.loadModel("lbfmodel.yaml")

onlyfiles = [ f for f in listdir(mypath) if isfile(join(mypath,f)) ]
images = np.empty(len(onlyfiles), dtype=object)
for n in range(0, len(onlyfiles)):
    print("Loading Features From images:", str(n+1).zfill(6) + ".jpg")
    images[n] = cv.imread( join(mypath,onlyfiles[n]) )
    key = detector.detect(cv.cvtColor(images[n], cv.COLOR_BGR2GRAY))
    xList =[]
    yList =[]
    for p in key:
    	x = p.pt[0]
    	y = p.pt[1]
    	xList.append(x)
    	yList.append(y)

    temp = pd.DataFrame(columns=['X_1','X_2','X_3','X_3','X_4','X_5','X_6','X_7','X_8','X_9','X_10','X_11','X_12','X_13','X_14','X_15','X_16','X_17','X_18','X_19','X_20','Y_1','Y_2','Y_3','Y_3','Y_4','Y_5','Y_6','Y_7','Y_8','Y_9','Y_10','Y_11','Y_12','Y_13','Y_14','Y_15','Y_16','Y_17','Y_18','Y_19','Y_20']) 
    for i in range(len(temp)):
        temp = temp.append({'X_{}'.format(i): xList[i], 'Y_{}'.format(i): yList[i]}, ignore_index = True)
    
    df = df.append(temp)

display(df)
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
with open('model.pkl', 'wb') as model_file:
  pickle.dump(kmeans, model_file, protocol=2)