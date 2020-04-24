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


# Path for result images
mypath = "img_align_celeba"

# Load the image 
image = cv.imread("goon_set/000010.jpg") 
imageName = "DavidKopec.jpg"

cv.imshow("Testing Image!", image)
cv.waitKey()
cv.destroyAllWindows()
image = resizeImage(image)

# Read the Features
d = []

# Create facemark detector and load lbf model:
facemark = cv.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

# Load cascade detector
cascade = cv.CascadeClassifier('haarcascade_frontalface_alt.xml')

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


	                        

X_test = np.array(d)
X_test = X_test.reshape(1, -1)

with open("model_total.pkl", 'rb') as model:
	kmeans =  pickle.load(model)

# Let X_test be the feature for which we want to predict the output
# Get the cluster the image is predicted to fit into
result = int(kmeans.predict(X_test))

print("Result Cluster: #", result)
# Get the results of that cluster
result_cluster = np.where(kmeans.labels_ == result)[0]

print(result_cluster)

result_images = []
for i in range(result_cluster.size):
	result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")


first_result_path = "img_align_celeba/" + result_images[0]
first_result = cv.imread(first_result_path)

cv.imshow("Result!", first_result)
cv.waitKey()
cv.destroyAllWindows()

print(result_images)
