import cv2 as cv
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import urllib
import json
import pickle
import requests
from sklearn.cluster import KMeans
from sklearn.preprocessing import LabelEncoder
from flask import request
from flask import Flask

app = Flask(__name__)

@app.route('/')
def getMatches():
    # Masquerade as Mozilla because some web servers may not like python bots.
    hdr = {'User-Agent': 'Mozilla/5.0'}
    # Set up the request
    #req = urllib.Request(request.vars.url, headers=hdr)
    url = request.args.get('url')
    req = urllib.request.urlopen(url, headers=hdr)


    try:
        # Load the image as parameter
        # Obtain the content of the url
        con = urllib.urlopen( req )
        # Read the content and convert it into an numpy array
        im_array = np.asarray(bytearray(con.read()), dtype=np.uint8)
        # Convert the numpy array into an image.
        image =  cv.imdecode(im_array, cv.IMREAD_UNCHANGED)

        # Stop CvtHelper errors with RBG cutoff
        #image = image.astype('uint8')

        # Read the Features - To be updated to facemark
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

        # de-serialize mlp_nn.pkl file into an object called mlp_nn using pickle
        with open('/home/davidrharvey/web2py/applications/celhair/uploads/model.pkl', 'rb') as handle:
            kmeans = pickle.load(handle)

        # Let X_test be the feature (UNIX timestamp) for which we want to predict the output
        result = kmeans.predict(X_test)
        result_cluster = np.where(kmeans.labels_ == result[0])[0]

        result_images = []
        for i in range(40):
            result_images.append(str(result_cluster[i]).zfill(6) + ".jpg")

        # Return names of result cluster image in json dump
        return json.dumps(result_images)


    except urllib.HTTPError as e:
        return e.fp.read()
