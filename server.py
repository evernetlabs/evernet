import os
import traceback

from dotenv import *
from flask import Flask, request, jsonify, g
from flask_cors import CORS

from service.config_service import ConfigService
from controller.health_check_controller import HealthCheckController

app = Flask(__name__)
CORS(app)
load_dotenv()

config_service = ConfigService()

HealthCheckController(app).register()

@app.before_request
def before_request():
    g.request_body = request.get_json(force=True, silent=True)
    g.config_service = config_service


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
