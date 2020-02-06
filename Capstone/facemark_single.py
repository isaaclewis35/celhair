import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from os import listdir
from os.path import isfile, join


# load image: 
image = cv2.imread("DavidKopec.jpg", 0)

# find faces:
cascade = cv2.CascadeClassifier('C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml')
faces = cascade.detectMultiScale(image, 1.3, 5)

# create landmark detector and load lbf model:
facemark = cv2.face.createFacemarkLBF()
facemark.loadModel("lbfmodel.yaml")

# run landmark detector:
ok, landmarks = facemark.fit(image, faces)

# print results:
print ("landmarks LBF",ok, landmarks)

# write landmarks to output
for marks in landmarks:
	print("Outer loop")
	for mark in marks:
		print("Inner Loop")
		x = int(mark[0][0].item())
		y = int(mark[0][1].item())
		print("X: ", x, "X Type", type(x))
		print("Y: ", y, "Y Type", type(y))
		cv2.rectangle(image, (x, y), (x, y), (0, 0, 255), 2)

plt.imshow(image)
# Save
cv2.imwrite("result3.png",image)