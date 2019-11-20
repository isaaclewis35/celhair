import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import base64
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder

class predictor():
	def __init__(self):
		pass

def stringToRGB(base64_string):
    imgdata = base64.b64decode(str(base64_string))
    image = Image.open(io.BytesIO(imgdata))
    return cv2.cvtColor(np.array(image), cv2.COLOR_BGR2RGB)


def predict(self, imagestring):
	# Load the image
	image = stringToRGB(imagestring)

	# Read the Features 
	X_test = pd.DataFrame(columns=['Name','X_Coord', 'Y_Coord'])

	#image = cv.imread("DavidKopec2.jpg")
	detector = cv.ORB_create(nfeatures=20)
	key = detector.detect(cv.cvtColor(image, cv.COLOR_BGR2GRAY))
	for p in key:
		name = "0.jpg"
		x = p.pt[0]
		y = p.pt[1]
		X_test = X_test.append({'Name': name,'X_Coord': x, 'Y_Coord': y}, ignore_index = True)

	       
	# Run Label Encoder Over the Data
	for i in X_test:
	    le = LabelEncoder()

	    le.fit(X_test[i])
	    X_test[i] = le.transform(X_test[i])


	# de-serialize mlp_nn.pkl file into an object called mlp_nn using pickle
	with open('model.pkl', 'rb') as handle:
	    kmeans = pickle.load(handle)    
	# no we can call various methods over mlp_nn as as:
	# Let X_test be the feature (UNIX timestamp) for which we want to predict the output 
	result = kmeans.predict(X_test)
	result_cluster = np.where(kmeans.labels_ == result[0])[0]

	result_images = []
	for i in range(40):
		result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

	return result_images