# Edited Tutorial From https://www.superdatascience.com/blogs/opencv-face-detection

import cv2
import matplotlib.pyplot as plt
import time

def convertToRGB(img): 
    return cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

#load test iamge
test1 = cv2.imread('DavidKopec2.jpeg')
#convert the test image to gray image as opencv face detector expects gray images
gray_img = cv2.cvtColor(test1, cv2.COLOR_BGR2GRAY)

plt.imshow(gray_img, cmap='gray')

#load cascade classifier training file for haarcascade
haar_face_cascade = cv2.CascadeClassifier('C:/OpenCV/sources/data/haarcascades/haarcascade_frontalface_alt.xml')

#let's detect multiscale (some images may be closer to camera than others) images
faces = haar_face_cascade.detectMultiScale(gray_img, scaleFactor=1.1, minNeighbors=5)

#print the number of faces found
print('Faces found: ', len(faces))

#go over list of faces and draw them as rectangles on original colored
for (x, y, w, h) in faces:
    cv2.rectangle(test1, (x, y), (x+w, y+h), (0, 255, 0), 2)

#convert image to RGB and show image
plt.imshow(convertToRGB(test1))
plt.show()