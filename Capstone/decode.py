import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder


# Load the image as parameter from POST args
#image = cv.imread("000120.jpg") # Error with face not looking at camera
#image = cv.imread("DavidKopec.jpg") # Same cluster is always returned
image = cv.imread("000001.jpg") # Same cluster is always returned

# Read the Features - To be updated to facemark, still using ORB atm
d = []

# create facemark detector and load lbf model:
facemark = cv.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

# load cascade detector
cascade = cv.CascadeClassifier('C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml')
faces = cascade.detectMultiScale(image, 1.3, 5)

ok, landmarks = facemark.fit(image, faces)

imageName = "test.jpg"
if ok:
    for marks in landmarks[0]:
        for mark in marks:
            d.append({'X': mark[0], 'Y': mark[1]})
                        
else:
    print("Landmark DETECTION failed")

X_test = pd.DataFrame(d)
# Run Label Encoder Over the Data
for i in X_test:
    le = LabelEncoder()
    le.fit(X_test[i])
    X_test[i] = le.transform(X_test[i])

# de-serialize static model.pkl file into an object called kmeans using pickle
with open('model_facemark_5k_noname.pkl', 'rb') as model:
    kmeans = pickle.load(model)

# Let X_test be the feature for which we want to predict the output
result = kmeans.predict(X_test)
result_cluster = np.where(kmeans.labels_ == result[0])[0]

result_images = []
for i in range(20):
    result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

print(result_images)