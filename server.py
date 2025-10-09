import os
import traceback

import pymongo
from dotenv import *
from flask import Flask, request, jsonify, g

from health.health_api import HealthAPI

app = Flask(__name__)
load_dotenv()

jwt_signing_key = os.getenv("JWT_SIGNING_KEY")
db = pymongo.MongoClient(os.getenv("DB_HOST"), int(os.getenv("DB_PORT"))).evernet

HealthAPI(app).register()


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
