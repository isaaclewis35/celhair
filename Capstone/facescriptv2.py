import pandas as pd 
import numpy as np
import matplotlib.pyplot as plt
import pickle

from sklearn.cluster import AffinityPropagation
from sklearn.preprocessing import LabelEncoder, StandardScaler

# Load Image Features
data = pd.read_csv("list_landmarks_celeba.txt").truncate(after=1000)

# Run Label Encoder Over the Data
for i in data:
	le = LabelEncoder()

	le.fit(data[i])
	data[i] = le.transform(data[i])


# Apply Standard Scaler
scaler = StandardScaler()
data = scaler.fit_transform(data)

df = pd.DataFrame(data,columns=['name','lefteye_x','lefteye_y','righteye_x','righteye_y','nose_x','nose_y','leftmouth_x','leftmouth_y','rightmouth_x','rightmouth_y'])
  
affinity_prop = AffinityPropagation().fit(df)
centroids = np.array(affinity_prop.cluster_centers_)

print(centroids)

# Show Data
plt.scatter(df['lefteye_x'],df['lefteye_y'],  c='blue', s=10, alpha=0.5, label='lefteye')
plt.scatter(df['righteye_x'],df['righteye_y'],  c='green', s=10, alpha=0.5, label='righteye')
plt.scatter(df['nose_x'],df['nose_y'],  c='yellow', s=10, alpha=0.5, label='nose')
plt.scatter(df['leftmouth_x'],df['leftmouth_y'],  c='purple', s=10, alpha=0.5, label='leftmouth')
plt.scatter(df['rightmouth_x'],df['rightmouth_y'],  c='orange', s=10, alpha=0.5, label='rightmouth')

# Show Centriods
plt.scatter(centroids[:, 0], centroids[:, 1], c='red', s=50, marker="x", label='clutser centers')
plt.title('CelebA Face Data')
plt.legend()
plt.show()

# Dump Model to Pickle File
with open('model2.pkl', 'wb') as model_file:
  pickle.dump(affinity_prop, model_file)