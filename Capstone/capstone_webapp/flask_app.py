import json
import os
import unpickle
from flask import Flask,jsonify,request
from flask_cors import CORS


app = Flask(__name__)
CORS(app)
@app.route("/",methods=['GET'])
def return_prediction():
    image = request.args.get('image')
    result = unpickle.predict(image)
    return jsonfiy(result)

if __name__ == "__main__":
    app.run()