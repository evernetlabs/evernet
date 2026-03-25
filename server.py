import os
import traceback

import pymongo
from dotenv import load_dotenv
from flask import Flask, request, jsonify, g
from flask_cors import CORS

from api.health_check_api import HealthCheckAPI
from web import Web

app = Flask(__name__)
CORS(app)
load_dotenv()

db = pymongo.MongoClient(os.getenv("DB_HOST"), int(os.getenv("DB_PORT"))).evernet

Web(app).register()

HealthCheckAPI(app).register()


@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)


@app.errorhandler(404)
def handle_404_error(e):
    return jsonify({
        "success": False,
        "message": str(e)
    }), 404


@app.errorhandler(Exception)
def handle_all_errors(e):
    print(traceback.format_exc())
    return jsonify({
        "success": False,
        "message": str(e)
    }), 500


if __name__ == '__main__':
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT")), debug=os.getenv("ENV") != "PROD")
