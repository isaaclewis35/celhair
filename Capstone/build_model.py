import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.cluster import MiniBatchKMeans
from os import listdir
from os.path import isfile, join
import gc

# Load Image Features - Running OpenCV Feature dectection over every file in given directory
mypath = "img_align_celeba"

d = []
key = []
# Counter for manually running garbage collection when loading images
memCount = 0

# Open cascade classifier:
cascade = cv.CascadeClassifier('haarcascade_frontalface_alt.xml')

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

    images[n] = None
    # call garbage collector every so often to avoid memory issues with large fileset
    memCount = memCount + 1
    if (memCount >= 100):
        print("Images Cleared From memory:", memCount)
        gc.collect()
        memCount = 0



print("Finished Loading Images!")
# Turn data into numpy array for kmeans
df = np.array(d)

# Save the images in a pickle dump incase of memory error on kmeans fitting
with open('200k_images.pkl', 'wb') as model_file:
  pickle.dump(df, model_file, protocol=2)

# Running Garbage collector again before building the model, b/c of the size of the dataset
gc.collect()

# Run K Means
kmeans = KMeans(n_clusters=1000, n_init=10,n_jobs=20, precompute_distances=False,verbose=1,copy_x=False, algorithm='full')

#kmeans = MiniBatchKMeans(init='k-means++', n_clusters=100, batch_size=100,
#                      n_init=10, max_no_improvement=10, verbose=1)
kmeans.fit(df)
print("KMeans clusters generated!")

# Dump Model to Pickle File
with open('model_total_mini.pkl', 'wb') as model_file:
  pickle.dump(kmeans, model_file, protocol=2)



##############################################

# Load the image 
image = cv.imread("DavidKopec.jpg") 
imageName = "DavidKopec.jpg"
# Read the Features
d = []

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

# Let X_test be the feature for which we want to predict the output
# Get the cluster the image is predicted to fit into
result = int(kmeans.predict(X_test))
print("result:", result)

# Get the results of that cluster
result_cluster = np.where(kmeans.labels_ == result)[0]
print("result_cluster:", result_cluster)

result_images = []
for i in range(5):
    result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

print(result_images)