import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder


def resizeImage(image):
	print('Original Dimensions : ',image.shape)
	width = 178
	height = 218
	dim = (width, height)
	image = cv.resize(image, dim, interpolation = cv.INTER_AREA)
	print('Resized Dimensions : ',image.shape)
	return image


def loadModel(modelName):
	# De-serialize static model.pkl file into an object called kmeans using pickle
	with open(modelName, 'rb') as model:
	    return pickle.load(model)


# Path for result images
mypath = "training_images_large"

# Load the image 
image = cv.imread("DavidKopec.jpg") 
imageName = "DavidKopec.jpg"

image = resizeImage(image)

# Read the Features
d = []

# Create facemark detector and load lbf model:
facemark = cv.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

# Load cascade detector
cascade = cv.CascadeClassifier('haarcascade_frontalface_alt.xml')

try:
	# Run landmark detector:
	faces = cascade.detectMultiScale(image, 1.3, 5)
	ok, landmarks = facemark.fit(image, faces)
	        
	# Extract Landmark data 
	if ok:
	    for marks in landmarks[0]:
	        for mark in marks:
	            d.append(np.array(mark[0],mark[1]))
	                        
	else:
	    print("Landmark DETECTION failed on image: ", imageName)

except:
	print("Did not detect a face! Try looking right at the camera.")
	pass
	                        

X_test = np.array(d)
X_test = X_test.reshape(1, -1)

kmeans = loadModel('model_100_clusters.pkl')

# Let X_test be the feature for which we want to predict the output
# Get the cluster the image is predicted to fit into
result = int(kmeans.predict(X_test))

print("Result Cluster: #", result)
# Get the results of that cluster
result_cluster = np.where(kmeans.labels_ == result)[0]

result_images = []
for i in range(20):
    result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

print(result_images)
