from app import app
import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from flask import request

@app.route('/')
def does_it_work():
    return 'It works!'
@app.route('/matches', methods=['POST'])
def getMatches():
    print(request.headers)

    print("getMatches Called")
    # Load the image as parameter from POST args
    image = request.files['image']
    print(type(image))
    f = open("test.jpg","w")
    image.save("test.jpg")
    f.close

    image = cv.imread("test.jpg") 
    # Setting to the right dimensions 
    print('Original Dimensions : ',image.shape)
    width = 178
    height = 218
    dim = (width, height)
    # resize image
    image = cv.resize(image, dim, interpolation = cv.INTER_AREA)
    print('Resized Dimensions : ',image.shape)

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

    # De-serialize static model.pkl file into an object called kmeans using pickle
    with open('model_50_clusters.pkl', 'rb') as model:
        kmeans = pickle.load(model)

    # Let X_test be the feature for which we want to predict the output
    # Get the cluster the image is predicted to fit into
    result = int(kmeans.predict(X_test))

    print("Result Cluster: #", result)
    # Get the results of that cluster
    result_cluster = np.where(kmeans.labels_ == result)[0]

    result_images = []
    for i in range(20):
        result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

    #print(result_images)

    final = ""
    for i in result_images:
        final = final + " " + i

    print(final)

    # Return names of result cluster image in json dump
    return final