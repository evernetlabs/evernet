import os
import traceback

from dotenv import *
from flask import Flask, request, jsonify, g
from mongita import MongitaClientDisk

from api.health_check_api import HealthCheckAPI

load_dotenv()

db_client = MongitaClientDisk(host=os.getenv("DATA_PATH", "data"))
db = db_client.vertex

app = Flask(__name__)

HealthCheckAPI(app).register()

@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)


@app.errorhandler(404)
def handle_404_error(e):
    return jsonify({
        "success": False,
        "error": str(e)
    }), 404


@app.errorhandler(Exception)
def handle_all_errors(e):
    print(traceback.format_exc())
    return jsonify({
        "success": False,
        "error": str(e)
    }), 500


if __name__ == '__main__':
    app.run(host=os.getenv("HOST"), port=int(os.getenv("PORT")), debug=os.getenv("ENV") != "PROD")
