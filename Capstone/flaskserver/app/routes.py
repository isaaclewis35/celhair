from app import app
import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle
import cv2 as cv
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder

@app.route('/')
@app.route('/matches', methods=['POST'])
def getMatches():
    # Load the image as parameter from POST args
    image = request.args.get('image')

    # Read the Features - To be updated to facemark, still using ORB atm
    X_test = pd.DataFrame(columns=['Name','X_Coord', 'Y_Coord'])

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

    # de-serialize static model.pkl file into an object called kmeans using pickle
    with open('/static/model.pkl', 'rb') as handle:
        kmeans = pickle.load(handle)

    # Let X_test be the feature for which we want to predict the output
    result = kmeans.predict(X_test)
    result_cluster = np.where(kmeans.labels_ == result[0])[0]

    result_images = []
    for i in range(20):
        result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

    # Return names of result cluster image in json dump
    return json.dumps(result_images)